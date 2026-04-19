package de.oliverpabst.pqt.db;

import java.sql.*;

public class DBConnection {

    private transient Connection connection = null;

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

    public String getPassword() {
        return password;
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

    public String toString() {
        return connectionName + ": " + databaseName;
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://" + hostName + ":" + port + "/" + databaseName, userName, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a raw {@link java.sql.Statement} for executing user-supplied SQL.
     * The caller is responsible for closing it (preferably via try-with-resources).
     * Prefer {@link #executeQuery(String, Object[], ResultSetHandler)} for all
     * internal, parameterisable queries.
     */
    public java.sql.Statement rawStatement() throws SQLException {
        if (connection == null) {
            connect();
        }
        return connection.createStatement();
    }

    /**
     * Executes a raw SQL query and returns the live ResultSet.
     * The caller is responsible for consuming and NOT closing the ResultSet;
     * the Statement is intentionally left open to keep the ResultSet valid.
     * Used only for the interactive SQL runner — prefer
     * {@link #executeQuery(String, Object[], ResultSetHandler)} for internal queries.
     */
    public ResultSet executeQuery(final String sqlQuery) throws SQLException {
        if (connection == null) {
            connect();
        }

        Statement stmt = connection.createStatement();
        return stmt.executeQuery(sqlQuery);
    }

    /**
     * Executes a parameterised query using a {@link PreparedStatement}, passes the
     * {@link ResultSet} to {@code handler}, closes all JDBC resources, and returns
     * the handler's result. Parameters are set positionally via
     * {@link PreparedStatement#setObject(int, Object)}.
     */
    public <T> T executeQuery(final String sql, final Object[] params,
                              final ResultSetHandler<T> handler) throws SQLException {
        if (connection == null) {
            connect();
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        try {
            if(connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

        final DBConnection otherConnection = (DBConnection)obj;

        return this.connectionName.equals(otherConnection.getConnectionName());
    }

    @Override
    public int hashCode() {
        final int hash1 = 41;
        final int hash2 = 37;
        int hashValue = hash1 * (connectionName.hashCode() * hash2);
        return super.hashCode() * hashValue;
    }
}
