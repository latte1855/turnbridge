package com.asynctide.turnbridge.my.turnkey;

import static org.assertj.core.api.Assertions.assertThat;

import com.asynctide.turnbridge.service.turnkey.TurnkeyErrorMapper;
import com.asynctide.turnbridge.service.turnkey.TurnkeyErrorMapper.MappedError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * TurnkeyErrorMapper 單元測試，驗證常見錯誤碼是否回傳預期的 TB-xxxx 對照。
 */
class TurnkeyErrorMapperTest {

    private final TurnkeyErrorMapper mapper = new TurnkeyErrorMapper();

    /**
     * 成功情境：ResultCode=0 代表 MOF 接受資料，Mapper 僅保留原始 ErrorCode。
     */
    @Test
    @DisplayName("ResultCode=0 仍保留原始錯誤碼，且不產生 TB code")
    void map_whenResultSuccess_shouldKeepSourceCode() {
        MappedError mapped = mapper.map("0", "1234");
        assertThat(mapped.tbCode()).isNull();
        assertThat(mapped.sourceCode()).isEqualTo("1234");
        assertThat(mapped.tbCategory()).isNull();
    }

    /**
     * 生命週期錯誤：2002 應回 TB-4002，代表流程順序不合法不可重送。
     */
    @Test
    @DisplayName("生命週期錯誤 2002 對應 TB-4002")
    void map_whenLifecycleError_shouldReturnTb4002() {
        MappedError mapped = mapper.map("9", "2002");
        assertThat(mapped.tbCode()).isEqualTo("TB-4002");
        assertThat(mapped.tbCategory()).isEqualTo("PLATFORM.LIFECYCLE_STATE_NOT_ALLOWED");
        assertThat(mapped.canAutoRetry()).isFalse();
        assertThat(mapped.recommendedAction()).isEqualTo("FIX_LIFECYCLE_FLOW");
    }

    /**
     * 載具/買受人錯誤：4002（手機條碼格式）需統一映射至 TB-5005 方便 Portal 呈現。
     */
    @Test
    @DisplayName("載具錯誤 4002 對應 TB-5005")
    void map_whenCarrierError_shouldReturnTb5005() {
        MappedError mapped = mapper.map("9", "4002");
        assertThat(mapped.tbCode()).isEqualTo("TB-5005");
        assertThat(mapped.tbCategory()).isEqualTo("PLATFORM.DATA_CARRIER_INVALID");
    }

    /**
     * 稅額或金額異常：5004 屬於稅別錯誤，應映射為 TB-5004。
     */
    @Test
    @DisplayName("稅別錯誤 5004 對應 TB-5004")
    void map_whenTaxTypeInvalid_shouldReturnTb5004() {
        MappedError mapped = mapper.map("9", "5004");
        assertThat(mapped.tbCode()).isEqualTo("TB-5004");
        assertThat(mapped.recommendedAction()).isEqualTo("FIX_DATA");
    }

    /**
     * 平台維護：9001 屬系統性問題，Mapper 需輸出 TB-9001 以便重送策略判斷。
     */
    @Test
    @DisplayName("平台維護錯誤 9001 對應 TB-9001")
    void map_whenPlatformMaintenance_shouldReturnTb9001() {
        MappedError mapped = mapper.map("9", "9001");
        assertThat(mapped.tbCode()).isEqualTo("TB-9001");
        assertThat(mapped.tbCategory()).isEqualTo("SYSTEM.PLATFORM_MAINTENANCE");
        assertThat(mapped.canAutoRetry()).isTrue();
    }
}
