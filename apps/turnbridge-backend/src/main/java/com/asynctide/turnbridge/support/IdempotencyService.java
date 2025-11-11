package com.asynctide.turnbridge.support;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.asynctide.turnbridge.config.ApplicationProperties;
import com.asynctide.turnbridge.config.ApplicationProperties.UploadProps;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 冪等性檢查服務（M0：記憶體 + TTL；未來可換 Redis）。
 * <p>用法：HTTP Header: Idempotency-Key。重複的 Key 將被拒絕建立新批次。</p>
 * - Header: Idempotency-Key
 * - 首次見到回 true，否則回 false
 * - 以 TTL 到期自動清除，避免長時運行占用記憶體
 */
@Service
public class IdempotencyService {

    private static final class Seen {
        final Instant at;
        Seen(Instant at) { this.at = at; }
    }

    private final ConcurrentHashMap<String, Seen> seen = new ConcurrentHashMap<>();
    private final UploadProps props;

    public IdempotencyService(ApplicationProperties props) {
        this.props = props.getUploadProps();
    }

    /** 首次見到則標記並回 true；若已存在且未過期則回 false。 */
    public boolean markIfNew(String key) {
        final Instant now = Instant.now();
        final Instant expiry = now.minus(props.getIdempotencyTtl());
        Seen old = seen.putIfAbsent(key, new Seen(now));
        if (old == null) return true;                // 新進
        // 舊 key 若已過期，重置時間視為新
        if (old.at.isBefore(expiry)) {
            seen.put(key, new Seen(now));
            return true;
        }
        return false;
    }

    /** 每 30 分鐘掃描一次，清掉過期的 key。 */
    @Scheduled(fixedDelay = 30 * 60 * 1000L, initialDelay = 5 * 60 * 1000L)
    public void evictExpired() {
        Instant expiry = Instant.now().minus(props.getIdempotencyTtl());
        for (Map.Entry<String, Seen> e : seen.entrySet()) {
            if (e.getValue().at.isBefore(expiry)) {
                seen.remove(e.getKey(), e.getValue());
            }
        }
    }
}
