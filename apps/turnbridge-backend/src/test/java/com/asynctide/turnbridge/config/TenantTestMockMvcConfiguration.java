package com.asynctide.turnbridge.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * 測試環境統一加上租戶 Header，避免每個測試個別設定。
 */
@TestConfiguration
public class TenantTestMockMvcConfiguration {

    private static final String DEFAULT_TENANT_CODE = "TEN-001";

    @Bean
    public MockMvcBuilderCustomizer tenantHeaderCustomizer() {
        return builder -> builder.defaultRequest(
            MockMvcRequestBuilders.get("/").header("X-Tenant-Code", DEFAULT_TENANT_CODE)
        );
    }
}
