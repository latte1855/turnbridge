package com.example.agenttest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * HMAC 簽章工具（繁中註解）
 * <p>以 HMAC-SHA256 計算 body 簽章並回傳 Base64。</p>
 */
public final class HmacSigner {
    private HmacSigner() {}

    /**
     * 計算 HMAC-SHA256 簽章。
     * @param secret 共享密鑰
     * @param body 原文內容（JSON 或字串）
     * @return Base64 編碼之簽章字串
     */
    public static String hmacSha256Base64(String secret, String body) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
            byte[] raw = mac.doFinal(body.getBytes());
            return Base64.getEncoder().encodeToString(raw);
        } catch (Exception e) {
            throw new RuntimeException("HMAC 計算失敗", e);
        }
    }
}
