package com.asynctide.turnbridge.service.turnkey;

import com.asynctide.turnbridge.domain.enumeration.MessageFamily;
import com.asynctide.turnbridge.turnkey.TurnkeyProperties;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 監控 Turnkey B2SSTORAGE 目錄，若檔案滯留過久則發出警告。
 */
@Component
public class TurnkeyPickupMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(TurnkeyPickupMonitor.class);

    private final TurnkeyProperties turnkeyProperties;
    private final AtomicInteger srcStuckGauge;
    private final AtomicInteger packStuckGauge;
    private final AtomicInteger uploadPendingGauge;
    private final AtomicInteger errGauge;
    private final AtomicLong lastScanGauge;
    private final Counter alertCounter;
    private final MeterRegistry meterRegistry;
    private final Map<String, AtomicInteger> stageFamilyGauges = new ConcurrentHashMap<>();
    private final AtomicReference<PickupSnapshot> latestSnapshot = new AtomicReference<>();

    public TurnkeyPickupMonitor(TurnkeyProperties turnkeyProperties, MeterRegistry meterRegistry) {
        this.turnkeyProperties = turnkeyProperties;
        this.meterRegistry = meterRegistry;
        this.srcStuckGauge = meterRegistry.gauge("turnkey_pickup_src_stuck_files", new AtomicInteger(0));
        this.packStuckGauge = meterRegistry.gauge("turnkey_pickup_pack_stuck_files", new AtomicInteger(0));
        this.uploadPendingGauge = meterRegistry.gauge("turnkey_upload_pending_files", new AtomicInteger(0));
        this.errGauge = meterRegistry.gauge("turnkey_err_files", new AtomicInteger(0));
        this.lastScanGauge = meterRegistry.gauge("turnkey_pickup_last_scan_epoch", new AtomicLong(0));
        this.alertCounter = Counter.builder("turnkey_pickup_alert_total")
            .description("Turnkey pickup monitor alerts triggered")
            .register(meterRegistry);
    }

    @Scheduled(cron = "${turnbridge.turnkey.pickup-monitor-cron:30 */5 * * * *}")
    public void monitorPickupStatus() {
        String base = turnkeyProperties.getB2sStorageSrcBase();
        if (!StringUtils.hasText(base)) {
            return;
        }
        Duration srcMaxAge = Duration.ofMinutes(Math.max(1, turnkeyProperties.getPickupMaxAgeMinutes()));
        Instant srcThreshold = Instant.now().minus(srcMaxAge);
        Duration packMaxAge = Duration.ofMinutes(Math.max(1, turnkeyProperties.getPackMaxAgeMinutes()));
        Instant packThreshold = Instant.now().minus(packMaxAge);
        long totalSrcStuck = 0;
        long totalPackStuck = 0;
        long totalUpload = 0;
        long totalErr = 0;
        boolean alertTriggered = false;
        for (MessageFamily family : MessageFamily.values()) {
            Path srcDir = Path.of(base, family.name(), "SRC");
            long srcStuck = inspectDirectory(srcDir, srcThreshold, family.name(), "SRC", turnkeyProperties.getPickupMaxAgeMinutes());
            totalSrcStuck += srcStuck;
            alertTriggered = alertTriggered || srcStuck > 0;
            updateStageGauge("SRC", family.name(), srcStuck);

            Path packDir = Path.of(base, family.name(), "Pack");
            long packStuck = inspectDirectory(packDir, packThreshold, family.name(), "Pack", turnkeyProperties.getPackMaxAgeMinutes());
            totalPackStuck += packStuck;
            alertTriggered = alertTriggered || packStuck > 0;
            updateStageGauge("PACK", family.name(), packStuck);

            Path uploadDir = Path.of(base, family.name(), "Upload");
            long uploadCount = countFiles(uploadDir);
            totalUpload += uploadCount;
            updateStageGauge("UPLOAD", family.name(), uploadCount);

            Path errDir = Path.of(base, family.name(), "ERR");
            long errCount = inspectErrDirectory(errDir, family.name());
            totalErr += errCount;
            alertTriggered = alertTriggered || errCount > 0;
            updateStageGauge("ERR", family.name(), errCount);
        }
        srcStuckGauge.set(safeInt(totalSrcStuck));
        packStuckGauge.set(safeInt(totalPackStuck));
        uploadPendingGauge.set(safeInt(totalUpload));
        errGauge.set(safeInt(totalErr));
        long lastScanEpoch = Instant.now().truncatedTo(ChronoUnit.SECONDS).getEpochSecond();
        lastScanGauge.set(lastScanEpoch);
        if (alertTriggered) {
            alertCounter.increment();
        }
        Map<String, Long> stageSnapshot = new LinkedHashMap<>();
        stageFamilyGauges.forEach((key, gauge) -> stageSnapshot.put(key, (long) gauge.get()));
        latestSnapshot.set(
            new PickupSnapshot(lastScanEpoch, totalSrcStuck, totalPackStuck, totalUpload, totalErr, alertTriggered, Map.copyOf(stageSnapshot))
        );
    }

    private int safeInt(long value) {
        return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }

    private void updateStageGauge(String stage, String family, long count) {
        AtomicInteger gauge = stageFamilyGauges.computeIfAbsent(stage + '|' + family, key ->
            meterRegistry.gauge("turnkey_pickup_stage_files", Tags.of("stage", stage, "family", family), new AtomicInteger(0))
        );
        if (gauge != null) {
            gauge.set(safeInt(count));
        }
    }

    private long inspectDirectory(Path dir, Instant threshold, String messageType, String label, long limitMinutes) {
        if (!Files.exists(dir)) {
            return 0;
        }
        final long[] count = { 0 };
        try (var stream = Files.list(dir)) {
            stream.forEach(path -> {
                if (checkFileAge(path, threshold, messageType, label, limitMinutes)) {
                    count[0]++;
                }
            });
        } catch (IOException ex) {
            LOG.warn("無法讀取 Turnkey 目錄 {}：{}", dir, ex.getMessage());
        }
        return count[0];
    }

    private long inspectErrDirectory(Path dir, String messageType) {
        if (!Files.exists(dir)) {
            return 0;
        }
        try (var stream = Files.list(dir)) {
            long count = stream.count();
            if (count > 0) {
                LOG.error("Turnkey ERR 目錄累積 {} 筆檔案，messageType={}, path={}", count, messageType, dir);
            }
            return count;
        } catch (IOException ex) {
            LOG.warn("無法讀取 Turnkey ERR 目錄 {}：{}", dir, ex.getMessage());
            return 0;
        }
    }

    private long countFiles(Path dir) {
        if (!Files.exists(dir)) {
            return 0;
        }
        try (var stream = Files.list(dir)) {
            return stream.count();
        } catch (IOException ex) {
            LOG.warn("無法讀取 Turnkey 目錄 {}：{}", dir, ex.getMessage());
            return 0;
        }
    }

    private boolean checkFileAge(Path file, Instant threshold, String messageType, String label, long limitMinutes) {
        try {
            FileTime lastModified = Files.getLastModifiedTime(file);
            if (lastModified.toInstant().isBefore(threshold)) {
                LOG.warn(
                    "Turnkey {} 目錄檔案滯留超過 {} 分鐘：messageType={}, file={}",
                    label,
                    limitMinutes,
                    messageType,
                    file
                );
                return true;
            }
        } catch (IOException ex) {
            LOG.warn("無法取得檔案時間 {}：{}", file, ex.getMessage());
        }
        return false;
    }

    public Optional<PickupSnapshot> getLatestSnapshot() {
        return Optional.ofNullable(latestSnapshot.get());
    }

    public record PickupSnapshot(
        long lastScanEpoch,
        long srcStuck,
        long packStuck,
        long uploadPending,
        long err,
        boolean alertTriggered,
        Map<String, Long> stageFamilyCounts
    ) {}
}
