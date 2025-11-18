package com.asynctide.turnbridge.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

/**
 * 解析租戶並提供給後續流程使用。
 */
@Component
public class TenantRequestFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(TenantRequestFilter.class);
    private static final UrlPathHelper PATH_HELPER = new UrlPathHelper();

    private final TenantResolver tenantResolver;

    public TenantRequestFilter(TenantResolver tenantResolver) {
        this.tenantResolver = tenantResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String path = PATH_HELPER.getPathWithinApplication(request);
        if (shouldBypass(path, request)) {
            filterChain.doFilter(request, response);
            return;
        }
        tenantResolver.resolve(request).ifPresent(TenantContextHolder::set);
        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }

    private boolean shouldBypass(String path, HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        return !path.startsWith("/api/") ||
        path.startsWith("/api/authenticate") ||
        path.startsWith("/api/register") ||
        path.startsWith("/api/account");
    }

}
