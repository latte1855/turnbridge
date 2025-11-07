package com.asynctide.turnbridge.support;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 冪等性檢查服務（M0 簡化，使用記憶體；未來可換 Redis）。
 * <p>用法：HTTP Header: Idempotency-Key。重複的 Key 將被拒絕建立新批次。</p>
 */
@Service
public class IdempotencyService {
    private final ConcurrentHashMap<String, Boolean> seen = new ConcurrentHashMap<>();

    /** 首次見到則標記並回 true；若已存在則回 false。 */
    public boolean markIfNew(String key) {
        return seen.putIfAbsent(key, Boolean.TRUE) == null;
    }
}
