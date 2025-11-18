package com.asynctide.turnbridge.tenant;

import com.asynctide.turnbridge.domain.Tenant;
import com.asynctide.turnbridge.repository.TenantRepository;
import com.asynctide.turnbridge.security.AuthoritiesConstants;
import com.asynctide.turnbridge.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 從 HTTP Request 解析目前租戶，未帶 Header 時可落入預設租戶。
 */
@Component
public class TenantResolver {

    private final TenantRepository tenantRepository;
    private final TenantProperties properties;

    public TenantResolver(TenantRepository tenantRepository, TenantProperties properties) {
        this.tenantRepository = tenantRepository;
        this.properties = properties;
    }

    public Optional<TenantContext> resolve(HttpServletRequest request) {
        if (!SecurityUtils.isAuthenticated()) {
            return Optional.empty();
        }
        boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN);
        String headerName = properties.getHeaderName();
        String requestedCode = request.getHeader(headerName);
        if (!StringUtils.hasText(requestedCode)) {
            requestedCode = properties.getDefaultCode();
        }
        if (!StringUtils.hasText(requestedCode)) {
            if (isAdmin) {
                return Optional.of(new TenantContext(null, null, true, Collections.emptyList()));
            }
            throw new TenantResolveException("缺少 " + headerName + "，無法判定租戶", "請於 Header 帶入 " + headerName);
        }
        final String resolvedCode = requestedCode.trim().toUpperCase(Locale.ROOT);
        Tenant tenant = tenantRepository
            .findOneByCode(resolvedCode)
            .orElseThrow(() -> new TenantResolveException("指定的租戶不存在：" + resolvedCode, "請確認 Header " + headerName + " 是否輸入正確"));
        return Optional.of(new TenantContext(tenant.getId(), tenant.getCode(), isAdmin, List.of()));
    }
}
