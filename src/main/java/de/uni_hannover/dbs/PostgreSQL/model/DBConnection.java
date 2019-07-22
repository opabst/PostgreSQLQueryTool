package de.uni_hannover.dbs.PostgreSQL.model;

public class DBConnection {

    private String url;
    private String username;
    private String password;
    private String port;
    private String dbname;

    public DBConnection(String _url, String _username, String _password, String _port, String _dbname) {
        url = _url;
        username = _username;
        password = _password;
        port = _port;
        dbname = _dbname;
    }

    public String getUrl() {
        return url;
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
}
