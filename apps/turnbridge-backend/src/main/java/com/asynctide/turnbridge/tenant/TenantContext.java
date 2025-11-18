package com.asynctide.turnbridge.tenant;

import java.util.List;

/**
 * 代表目前請求的租戶資訊。
 *
 * @param tenantId 租戶 ID（可為 null，表示管理者全域檢視）
 * @param tenantCode 租戶代碼（可為 null）
 * @param admin 是否具備全域管理者權限
 * @param allowedTenantIds可被授權查閱的其他租戶 ID
 */
public record TenantContext(Long tenantId, String tenantCode, boolean admin, List<Long> allowedTenantIds) {

    public boolean hasTenant() {
        return tenantId != null;
    }
}
