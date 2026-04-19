package de.oliverpabst.pqt.service;

import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Background {@link Task} that tests a JDBC connection without blocking the
 * JavaFX Application Thread. Returns {@code true} on success, {@code false}
 * on failure. The exception message is available via {@link #getException()}
 * when the task fails.
 */
public class ConnectionTestService extends Task<Boolean> {

    private final String hostname;
    private final String port;
    private final String databaseName;
    private final String username;
    private final String password;

    public ConnectionTestService(final String hostname, final String port,
                                 final String databaseName, final String username,
                                 final String password) {
        this.hostname = hostname;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    @Override
    protected Boolean call() throws Exception {
        final String url = "jdbc:postgresql://" + hostname + ":" + port + "/" + databaseName;
        try (Connection con = DriverManager.getConnection(url, username, password)) {
            return con.isValid(5);
        } catch (final SQLException e) {
            throw e;
        }
    }
}
