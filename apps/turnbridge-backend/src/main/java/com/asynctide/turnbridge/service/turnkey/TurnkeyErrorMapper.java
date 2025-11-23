package com.asynctide.turnbridge.service.turnkey;

import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Turnkey/MOF 錯誤碼對應 TB-xxxx。
 */
@Component
public class TurnkeyErrorMapper {

    /**
     * 依據《Turnkey 使用說明書 v3.9.pdf》／`docs/turnkey/manual/08_result_codes.md`整理出的常見錯誤對照。
     */
    private static final Map<String, String> EXACT_MAPPING = Map.ofEntries(
        Map.entry("1001", "TB-3002"), // XML 格式錯誤
        Map.entry("1002", "TB-3001"), // 必填欄位缺漏
        Map.entry("1003", "TB-3003"), // 資料長度超過限制
        Map.entry("1004", "TB-3002"), // 日期格式錯誤
        Map.entry("1005", "TB-3002"), // 數值格式錯誤
        Map.entry("1006", "TB-3004"), // 稅額試算錯誤
        Map.entry("1007", "TB-3002"), // 發票號碼格式不符
        Map.entry("2001", "TB-4001"),
        Map.entry("2002", "TB-4002"),
        Map.entry("2003", "TB-4002"),
        Map.entry("2004", "TB-4002"),
        Map.entry("2005", "TB-4003"),
        Map.entry("2006", "TB-4003"),
        Map.entry("3001", "TB-5001"),
        Map.entry("3002", "TB-5002"),
        Map.entry("3003", "TB-5001"),
        Map.entry("3004", "TB-5002"),
        Map.entry("4001", "TB-5005"),
        Map.entry("4002", "TB-5005"),
        Map.entry("4003", "TB-5005"),
        Map.entry("4004", "TB-5005"),
        Map.entry("5001", "TB-5003"),
        Map.entry("5002", "TB-5003"),
        Map.entry("5003", "TB-5003"),
        Map.entry("5004", "TB-5004"),
        Map.entry("9001", "TB-9001"),
        Map.entry("9002", "TB-9002"),
        Map.entry("9003", "TB-9003"),
        Map.entry("9004", "TB-9003"),
        Map.entry("E200", "TB-5003"),
        Map.entry("E410", "TB-4001"),
        Map.entry("E411", "TB-4003"),
        Map.entry("E420", "TB-5001"),
        Map.entry("E430", "TB-5002"),
        Map.entry("DUPLICATE", "TB-5002")
    );

    private static final Map<String, ErrorMeta> TB_METADATA = Map.ofEntries(
        Map.entry("TB-3001", meta("TURNKEY.MIG_VALIDATION", false, "FIX_DATA")),
        Map.entry("TB-3002", meta("TURNKEY.MIG_VALIDATION", false, "FIX_DATA")),
        Map.entry("TB-3003", meta("TURNKEY.MIG_VALIDATION", false, "FIX_DATA")),
        Map.entry("TB-3004", meta("TURNKEY.MIG_VALIDATION", false, "FIX_DATA")),
        Map.entry("TB-4001", meta("PLATFORM.LIFECYCLE_INVALID_ORDER", false, "FIX_LIFECYCLE_FLOW")),
        Map.entry("TB-4002", meta("PLATFORM.LIFECYCLE_STATE_NOT_ALLOWED", false, "FIX_LIFECYCLE_FLOW")),
        Map.entry("TB-4003", meta("PLATFORM.LIFECYCLE_ALREADY_CANCELLED", false, "SKIP_DUPLICATE")),
        Map.entry("TB-4004", meta("PLATFORM.LIFECYCLE_ALREADY_REVOKED", false, "SKIP_DUPLICATE")),
        Map.entry("TB-5001", meta("PLATFORM.DATA_INVOICE_NOT_EXISTS", false, "FIX_DATA")),
        Map.entry("TB-5002", meta("PLATFORM.DATA_DUPLICATE", false, "SKIP_DUPLICATE")),
        Map.entry("TB-5003", meta("PLATFORM.DATA_AMOUNT_MISMATCH", false, "FIX_DATA")),
        Map.entry("TB-5004", meta("PLATFORM.DATA_TAXTYPE_INVALID", false, "FIX_DATA")),
        Map.entry("TB-5005", meta("PLATFORM.DATA_CARRIER_INVALID", false, "FIX_DATA")),
        Map.entry("TB-9001", meta("SYSTEM.PLATFORM_MAINTENANCE", true, "CHECK_PLATFORM")),
        Map.entry("TB-9002", meta("SYSTEM.PLATFORM_TIMEOUT", true, "CHECK_PLATFORM")),
        Map.entry("TB-9003", meta("SYSTEM.PLATFORM_ERROR", true, "CHECK_PLATFORM"))
    );

    /**
     * Map ProcessResult error code to TB error code.
     *
     * @param resultCode ProcessResult ResultCode
     * @param rawErrorCode ProcessResult ErrorCode
     * @return mapped TB code info
     */
    public MappedError map(String resultCode, String rawErrorCode) {
        if ("0".equals(resultCode)) {
            return new MappedError(null, null, false, null, normalize(rawErrorCode));
        }
        String normalized = normalize(rawErrorCode);
        String tbCode = EXACT_MAPPING.get(normalized);
        if (tbCode == null && normalized != null) {
            tbCode = guessByPattern(normalized);
        }
        if (tbCode == null) {
            tbCode = "TB-9003";
        }
        ErrorMeta meta = TB_METADATA.getOrDefault(tbCode, meta("SYSTEM.PLATFORM_ERROR", true, "CHECK_PLATFORM"));
        return new MappedError(tbCode, meta.category(), meta.canAutoRetry(), meta.recommendedAction(), normalized);
    }

    private String normalize(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        return raw.trim().toUpperCase(Locale.ROOT);
    }

    private String guessByPattern(String normalized) {
        if (normalized.startsWith("E4")) {
            return "TB-4002";
        }
        if (normalized.startsWith("E5")) {
            return "TB-5001";
        }
        if (normalized.contains("LIFE")) {
            return "TB-4001";
        }
        if (normalized.contains("AMOUNT")) {
            return "TB-5003";
        }
        if (normalized.matches("1\\d{3}")) {
            return "TB-3002";
        }
        if (normalized.matches("2\\d{3}")) {
            return "TB-4002";
        }
        if (normalized.matches("3\\d{3}")) {
            return "TB-5001";
        }
        if (normalized.matches("4\\d{3}")) {
            return "TB-5005";
        }
        return null;
    }

    private static ErrorMeta meta(String category, boolean canAutoRetry, String recommendedAction) {
        return new ErrorMeta(category, canAutoRetry, recommendedAction);
    }

    private record ErrorMeta(String category, boolean canAutoRetry, String recommendedAction) {}

    public record MappedError(String tbCode, String tbCategory, boolean canAutoRetry, String recommendedAction, String sourceCode) {}
}
