package de.oliverpabst.pqt.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DBConnection {

    private static final Logger log = LoggerFactory.getLogger(DBConnection.class);

    private HikariDataSource dataSource = null;

    private final String connectionName;
    private final String hostName;
    private final String userName;
    private String password;
    private final String port;
    private final String databaseName;

    public DBConnection(final String connectionName, final String hostName, final String port,
                        final String databaseName, final String userName, final String password) {
        this.connectionName = connectionName;
        this.hostName = hostName;
        this.userName = userName;
        this.password = password;
        this.port = port;
        this.databaseName = databaseName;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getHostName() {
        return hostName;
    }

    public String getUserName() {
        return userName;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getPort() {
        return port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String toString() {
        return connectionName + ": " + databaseName;
    }

    private synchronized void connect() {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://" + hostName + ":" + port + "/" + databaseName);
        config.setUsername(userName);
        config.setPassword(password);
        config.setPoolName(connectionName);
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(30_000);
        dataSource = new HikariDataSource(config);
        log.info("Connection pool '{}' created.", connectionName);
    }

    private Connection acquireConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            connect();
        }
        return dataSource.getConnection();
    }

    /**
     * Returns a {@link Statement} for executing user-supplied SQL.
     * Closing the returned statement also returns its underlying pooled connection.
     * Prefer {@link #executeQuery(String, Object[], ResultSetHandler)} for all
     * internal, parameterisable queries.
     */
    public Statement rawStatement() throws SQLException {
        final Connection conn = acquireConnection();
        return new PooledStatement(conn.createStatement(), conn);
    }

    /**
     * Executes a parameterised query using a {@link PreparedStatement}, passes the
     * {@link ResultSet} to {@code handler}, closes all JDBC resources, and returns
     * the handler's result. Parameters are set positionally via
     * {@link PreparedStatement#setObject(int, Object)}.
     */
    public <T> T executeQuery(final String sql, final Object[] params,
                              final ResultSetHandler<T> handler) throws SQLException {
        try (Connection conn = acquireConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return handler.handle(rs);
            }
        }
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
            log.info("Connection pool '{}' closed.", connectionName);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final DBConnection otherConnection = (DBConnection) obj;
        return this.connectionName.equals(otherConnection.getConnectionName());
    }

    @Override
    public int hashCode() {
        return connectionName.hashCode();
    }

    /**
     * A {@link Statement} wrapper that closes the underlying pooled
     * {@link Connection} (returning it to the pool) when the statement is closed.
     */
    private static final class PooledStatement implements Statement {

        private final Statement delegate;
        private final Connection connection;

        private PooledStatement(final Statement delegate, final Connection connection) {
            this.delegate = delegate;
            this.connection = connection;
        }

        @Override
        public void close() throws SQLException {
            try {
                delegate.close();
            } finally {
                connection.close();
            }
        }

        // ── Delegation ────────────────────────────────────────────────────────

        @Override public ResultSet executeQuery(String sql) throws SQLException { return delegate.executeQuery(sql); }
        @Override public int executeUpdate(String sql) throws SQLException { return delegate.executeUpdate(sql); }
        @Override public int getMaxFieldSize() throws SQLException { return delegate.getMaxFieldSize(); }
        @Override public void setMaxFieldSize(int max) throws SQLException { delegate.setMaxFieldSize(max); }
        @Override public int getMaxRows() throws SQLException { return delegate.getMaxRows(); }
        @Override public void setMaxRows(int max) throws SQLException { delegate.setMaxRows(max); }
        @Override public void setEscapeProcessing(boolean enable) throws SQLException { delegate.setEscapeProcessing(enable); }
        @Override public int getQueryTimeout() throws SQLException { return delegate.getQueryTimeout(); }
        @Override public void setQueryTimeout(int seconds) throws SQLException { delegate.setQueryTimeout(seconds); }
        @Override public void cancel() throws SQLException { delegate.cancel(); }
        @Override public SQLWarning getWarnings() throws SQLException { return delegate.getWarnings(); }
        @Override public void clearWarnings() throws SQLException { delegate.clearWarnings(); }
        @Override public void setCursorName(String name) throws SQLException { delegate.setCursorName(name); }
        @Override public boolean execute(String sql) throws SQLException { return delegate.execute(sql); }
        @Override public ResultSet getResultSet() throws SQLException { return delegate.getResultSet(); }
        @Override public int getUpdateCount() throws SQLException { return delegate.getUpdateCount(); }
        @Override public boolean getMoreResults() throws SQLException { return delegate.getMoreResults(); }
        @Override public void setFetchDirection(int direction) throws SQLException { delegate.setFetchDirection(direction); }
        @Override public int getFetchDirection() throws SQLException { return delegate.getFetchDirection(); }
        @Override public void setFetchSize(int rows) throws SQLException { delegate.setFetchSize(rows); }
        @Override public int getFetchSize() throws SQLException { return delegate.getFetchSize(); }
        @Override public int getResultSetConcurrency() throws SQLException { return delegate.getResultSetConcurrency(); }
        @Override public int getResultSetType() throws SQLException { return delegate.getResultSetType(); }
        @Override public void addBatch(String sql) throws SQLException { delegate.addBatch(sql); }
        @Override public void clearBatch() throws SQLException { delegate.clearBatch(); }
        @Override public int[] executeBatch() throws SQLException { return delegate.executeBatch(); }
        @Override public Connection getConnection() throws SQLException { return delegate.getConnection(); }
        @Override public boolean getMoreResults(int current) throws SQLException { return delegate.getMoreResults(current); }
        @Override public ResultSet getGeneratedKeys() throws SQLException { return delegate.getGeneratedKeys(); }
        @Override public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException { return delegate.executeUpdate(sql, autoGeneratedKeys); }
        @Override public int executeUpdate(String sql, int[] columnIndexes) throws SQLException { return delegate.executeUpdate(sql, columnIndexes); }
        @Override public int executeUpdate(String sql, String[] columnNames) throws SQLException { return delegate.executeUpdate(sql, columnNames); }
        @Override public boolean execute(String sql, int autoGeneratedKeys) throws SQLException { return delegate.execute(sql, autoGeneratedKeys); }
        @Override public boolean execute(String sql, int[] columnIndexes) throws SQLException { return delegate.execute(sql, columnIndexes); }
        @Override public boolean execute(String sql, String[] columnNames) throws SQLException { return delegate.execute(sql, columnNames); }
        @Override public int getResultSetHoldability() throws SQLException { return delegate.getResultSetHoldability(); }
        @Override public boolean isClosed() throws SQLException { return delegate.isClosed(); }
        @Override public void setPoolable(boolean poolable) throws SQLException { delegate.setPoolable(poolable); }
        @Override public boolean isPoolable() throws SQLException { return delegate.isPoolable(); }
        @Override public void closeOnCompletion() throws SQLException { delegate.closeOnCompletion(); }
        @Override public boolean isCloseOnCompletion() throws SQLException { return delegate.isCloseOnCompletion(); }
        @Override public <T> T unwrap(Class<T> iface) throws SQLException { return delegate.unwrap(iface); }
        @Override public boolean isWrapperFor(Class<?> iface) throws SQLException { return delegate.isWrapperFor(iface); }
    }
}
