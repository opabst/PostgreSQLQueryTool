package de.oliverpabst.pqt.db;

import java.io.Serializable;
import java.sql.*;

public class DBConnection implements Serializable {

    private transient Connection connection = null;

    private final String connectionName;
    private final String hostName;
    private final String userName;
    private final String password;
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

    public ResultSet executeQuery(final String sqlQuery) throws SQLException {
        if (connection == null) {
            connect();
        }

        Statement stmt = connection.createStatement();
        // dynamisch laden funktioniert so nicht
        //stmt.setFetchSize(100);

        return stmt.executeQuery(sqlQuery);
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
