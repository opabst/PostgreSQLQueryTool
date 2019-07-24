package de.uni_hannover.dbs.PostgreSQL.db;

// TODO: Ausnahmen behandeln
import java.sql.*;

public class DBConnection {

    Connection con = null;

    private String connectionname;
    private String hostname;
    private String username;
    private String password;
    private String port;
    private String dbname;

    public DBConnection(String _connectionname, String _hostname, String _port,  String _dbname, String _username, String _password) {
        connectionname = _connectionname;
        hostname = _hostname;
        username = _username;
        password = _password;
        port = _port;
        dbname = _dbname;
    }

    public String getConnectionname() {
        return connectionname;
    }

    public String getHostname() {
        return hostname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPort() {
        return port;
    }

    public String getDbname() {
        return dbname;
    }

    public String toString() {
        return connectionname + ": " + dbname;
    }

    private void connect() {
        try {
            con = DriverManager.getConnection("jdbc:postgresql://" + hostname + ":" + port + "/" + dbname , username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String _sqlQuery) throws SQLException {
        if (con == null) {
            connect();
        }
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(_sqlQuery);

        return rs;
    }
}
