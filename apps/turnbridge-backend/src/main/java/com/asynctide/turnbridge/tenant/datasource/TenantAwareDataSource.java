package com.asynctide.turnbridge.tenant.datasource;

import com.asynctide.turnbridge.tenant.TenantContextHolder;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.util.StringUtils;

/**
 * 包裝 DataSource，於每次 Connection 實際執行 SQL 前設定 Postgres Session 參數。
 */
public class TenantAwareDataSource extends AbstractDataSource {

    private final DataSource delegate;

    public TenantAwareDataSource(DataSource delegate) {
        this.delegate = delegate;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return wrap(delegate.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return wrap(delegate.getConnection(username, password));
    }

    private Connection wrap(Connection connection) {
        return (Connection) Proxy.newProxyInstance(
            connection.getClass().getClassLoader(),
            new Class[] { Connection.class },
            new TenantAwareConnectionHandler(connection)
        );
    }

    private static final class TenantAwareConnectionHandler implements InvocationHandler {

        private static final Set<String> STATEMENT_METHODS = new HashSet<>(
            Arrays.asList("prepareStatement", "prepareCall", "createStatement", "nativeSQL")
        );

        private final Connection target;
        private TenantSnapshot lastSnapshot;

        private TenantAwareConnectionHandler(Connection target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if (STATEMENT_METHODS.contains(name)) {
                applyTenantContext();
            }
            if ("unwrap".equals(name)) {
                Class<?> iface = (Class<?>) args[0];
                if (iface.isInstance(proxy)) {
                    return proxy;
                }
                if (iface.isInstance(target)) {
                    return target;
                }
            }
            if ("isWrapperFor".equals(name)) {
                Class<?> iface = (Class<?>) args[0];
                return iface.isInstance(proxy) || iface.isInstance(target);
            }
            return method.invoke(target, args);
        }

        private void applyTenantContext() throws SQLException {
            TenantSnapshot snapshot = TenantSnapshot.fromContext();
            if (Objects.equals(snapshot, lastSnapshot)) {
                return;
            }
            if (snapshot.hasTenant()) {
                setConfig("app.tenant_id", snapshot.tenantId().toString());
                setConfig("app.is_admin", "false");
                setConfig("app.allowed_tenant_ids", "");
            } else if (snapshot.admin()) {
                setConfig("app.tenant_id", "");
                setConfig("app.is_admin", "true");
                setConfig("app.allowed_tenant_ids", snapshot.allowedCsv());
            } else {
                setConfig("app.tenant_id", "");
                setConfig("app.is_admin", "false");
                setConfig("app.allowed_tenant_ids", "");
            }
            lastSnapshot = snapshot;
        }

        private void setConfig(String key, String value) throws SQLException {
            try (Statement statement = target.createStatement()) {
                statement.execute("select set_config('" + key + "', '" + sanitize(value) + "', true)");
            }
        }

        private String sanitize(String value) {
            return value == null ? "" : value.replace("'", "''");
        }
    }

    private record TenantSnapshot(Long tenantId, boolean admin, String allowedCsv) {

        static TenantSnapshot fromContext() {
            return TenantContextHolder
                .get()
                .map(ctx -> new TenantSnapshot(ctx.tenantId(), ctx.admin(), csv(ctx.allowedTenantIds())))
                .orElse(new TenantSnapshot(null, false, ""));
        }

        private static String csv(java.util.List<Long> ids) {
            if (ids == null || ids.isEmpty()) {
                return "";
            }
            return StringUtils.collectionToDelimitedString(ids, ",");
        }

        boolean hasTenant() {
            return tenantId != null;
        }
    }
}
