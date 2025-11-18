package com.asynctide.turnbridge.tenant;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 多租戶設定（Header 名稱、預設租戶等）。
 */
@ConfigurationProperties(prefix = "turnbridge.tenant", ignoreUnknownFields = false)
public class TenantProperties {

    /**
     * 預設租戶代碼（未提供 Header 時使用；生產環境建議清空以強制傳入）。
     */
    private String defaultCode;

    /**
     * 指示租戶的 HTTP Header 名稱。
     */
    private String headerName = "X-Tenant-Code";

    public String getDefaultCode() {
        return defaultCode;
    }

    public void setDefaultCode(String defaultCode) {
        this.defaultCode = defaultCode;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
}
