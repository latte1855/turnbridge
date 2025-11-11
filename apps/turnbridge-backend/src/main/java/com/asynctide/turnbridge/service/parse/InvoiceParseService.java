package com.asynctide.turnbridge.service.parse;

import com.asynctide.turnbridge.domain.UploadJob;
import com.asynctide.turnbridge.domain.UploadJobItem;
import com.asynctide.turnbridge.domain.enumeration.JobItemStatus;
import com.asynctide.turnbridge.repository.UploadJobItemRepository;
import com.asynctide.turnbridge.storage.StorageProvider;
import com.asynctide.turnbridge.support.ProfileDetectorService;
import jakarta.transaction.Transactional;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;
import org.springframework.stereotype.Service;

@Service
public class InvoiceParseService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceParseService.class);

    private final StorageProvider storage;
    private final UploadJobItemRepository itemRepo;
    private final ProfileDetectorService profileDetector;

    public InvoiceParseService(StorageProvider storage, UploadJobItemRepository itemRepo, ProfileDetectorService profileDetector) {
        this.storage = storage;
        this.itemRepo = itemRepo;
        this.profileDetector = profileDetector;
    }

    /** 從 UploadJob 原始檔打開並解析，批次寫入 UploadJobItem，回傳 (total, ok, error)。 */
    @Transactional
    public ParseStats parseAndPersistItems(UploadJob job) {
        if (job.getOriginalFile() == null) return new ParseStats(0, 0, 0);

        String bucket = job.getOriginalFile().getBucket();
        String key    = normalize(job.getOriginalFile().getBucket(), job.getOriginalFile().getObjectKey());

        try (InputStream in = storage.open(bucket, key).orElseThrow(() -> new IllegalStateException("原始檔不存在"))) {
            String filename = StringUtils.defaultString(job.getSourceFilename(), key).toLowerCase(Locale.ROOT);
            if (filename.endsWith(".zip")) {
                return parseZip(job, in);
            } else {
                return parseCsv(job, in, job.getProfile());
            }
        } catch (Exception e) {
            log.error("解析失敗: jobId={}, {}", job.getJobId(), e.getMessage(), e);
            return new ParseStats(0, 0, 0);
        }
    }

    private ParseStats parseZip(UploadJob job, InputStream in) throws IOException {
        int total = 0, ok = 0, err = 0;
        try (ZipInputStream zis = new ZipInputStream(in)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                if (!entry.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) continue;
                ParseStats stats = parseCsv(job, zis, job.getProfile());
                total += stats.total(); ok += stats.ok(); err += stats.error();
                zis.closeEntry();
            }
        }
        return new ParseStats(total, ok, err);
    }

    private ParseStats parseCsv(UploadJob job, InputStream in, String profileOrNull) throws IOException {
        List<UploadJobItem> batch = new ArrayList<>(1024);
        int total = 0, ok = 0, err = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String headerLine = br.readLine();
            if (headerLine == null) return new ParseStats(0, 0, 0);

            String[] headers = headerLine.split(",", -1);
            Map<String,Integer> idx = index(headers);
            String profile = StringUtils.defaultString(profileOrNull, profileDetector.detectByHeader(idx.keySet()));

            String line;
            int lineNo = 1;
            while ((line = br.readLine()) != null) {
                lineNo++;
                total++;
                String[] cols = line.split(",", -1);

                InvoiceRecord rec = map(profile, idx, cols, lineNo);

                var item = new UploadJobItem();
                item.setJob(job);
                item.setLineNo(lineNo);
                item.setInvoiceNo(rec.invoiceNo());
                item.setBuyerId(rec.buyerId());
                // 將 CSV 的 amount 視為「含稅金額」
                item.setAmountIncl(rec.amount() == null ? null : BigDecimal.valueOf(rec.amount()));
                item.setTraceId(job.getJobId() + "-" + String.format("%06d", lineNo));

                // 最小驗證
                List<String> errs = new ArrayList<>();
                if (StringUtils.isBlank(rec.buyerId())) errs.add("buyerId 缺漏");
                if (rec.amount() == null) errs.add("amount 非數字");
                if (rec.amount() != null && rec.amount() <= 0) errs.add("amount 必須 > 0");

                if (errs.isEmpty()) {
                    item.setStatus(JobItemStatus.OK);
                    item.setResultCode("0000");
                    item.setResultMsg("OK");
                    ok++;
                } else {
                    item.setStatus(JobItemStatus.ERROR);
                    item.setResultCode("0400");
                    item.setResultMsg(String.join("; ", errs));
                    err++;
                }
                batch.add(item);

                if (batch.size() >= 1000) {
                    itemRepo.saveAll(batch);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) itemRepo.saveAll(batch);
        }
        return new ParseStats(total, ok, err);
    }

    private static Map<String,Integer> index(String[] headers) {
        Map<String,Integer> map = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            map.put(headers[i].trim().toLowerCase(Locale.ROOT), i);
        }
        return map;
    }

    private static InvoiceRecord map(String profile, Map<String,Integer> idx, String[] cols, int lineNo) {
        String invoiceNo;
        String buyerId;
        Double amount = null;

        if ("Profile-Legacy".equals(profile)) {
            invoiceNo = get(cols, idx.get("inv_no"));
            buyerId   = get(cols, idx.get("buyer_id"));
            amount    = toDouble(get(cols, idx.get("amt")));
        } else {
            invoiceNo = get(cols, idx.get("invoiceno"));
            buyerId   = get(cols, idx.get("buyerid"));
            amount    = toDouble(get(cols, idx.get("amount")));
        }
        return new InvoiceRecord(lineNo, invoiceNo, buyerId, amount);
    }

    private static String get(String[] cols, Integer i) { return i == null || i >= cols.length ? null : StringUtils.trimToNull(cols[i]); }
    private static Double toDouble(String s) {
        try { return s == null ? null : Double.valueOf(s); } catch (Exception e) { return null; }
    }

    private static String normalize(String bucket, String key) {
        String prefix = bucket + "/";
        return key != null && key.startsWith(prefix) ? key.substring(prefix.length()) : key;
    }

    public record InvoiceRecord(int lineNo, String invoiceNo, String buyerId, Double amount) {}
    public record ParseStats(int total, int ok, int error) {}
}
