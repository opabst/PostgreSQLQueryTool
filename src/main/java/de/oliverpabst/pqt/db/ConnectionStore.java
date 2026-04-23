package de.oliverpabst.pqt.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConnectionStore {
    private static final ConnectionStore instance = new ConnectionStore();

    private static final String CONFIG_DIR = ".pqt";
    private static final String CONN_FILE = "connections.json";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ObservableList<DBConnection> connectionList;

    private ConnectionStore() {
        connectionList = FXCollections.observableArrayList();
    }

    public static ConnectionStore getInstance() {
        return instance;
    }

    public boolean addConnection(final DBConnection connection) {
        final Iterator<DBConnection> connectionIterator = connectionList.iterator();
        while (connectionIterator.hasNext()) {
            final DBConnection existingCon = connectionIterator.next();
            if (existingCon.equals(connection)) {
                return false;
            }
        }
        connectionList.add(connection);
        return true;
    }

    public DBConnection getConnection(final String connectionName) {
        for (final DBConnection dbConnection : connectionList) {
            if (dbConnection.getConnectionName().equals(connectionName)) {
                return dbConnection;
            }
        }
        return null;
    }

    public boolean removeConnection(final String connectionName) {
        for (final DBConnection connection : connectionList) {
            if (connection.getConnectionName().equals(connectionName)) {
                return connectionList.removeAll(connection);
            }
        }
        return false;
    }

    public ObservableList<DBConnection> getConnections() {
        return connectionList;
    }

    public boolean closeAllConnections() {
        for (final DBConnection connection : connectionList) {
            connection.disconnect();
        }
        return true;
    }

    /**
     * Persists all connections (without passwords) to ~/.pqt/connections.json.
     */
    public void writeConnectionsToDisk() throws IOException {
        final File dir = configDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        final List<ConnectionRecord> records = new ArrayList<>();
        for (final DBConnection c : connectionList) {
            records.add(new ConnectionRecord(
                    c.getConnectionName(),
                    c.getHostName(),
                    c.getPort(),
                    c.getDatabaseName(),
                    c.getUserName()
            ));
        }

        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(dir, CONN_FILE), records);
        } catch (final IOException e) {
            throw e;
        }
    }

    /**
     * Reads connections from ~/.pqt/connections.json. Passwords are never stored,
     * so DBConnection objects are created without a password; the user must enter
     * the password in the UI before connecting.
     * If the file does not exist (first launch), this method returns silently.
     */
    public void readConnectionsFromDisk() throws IOException {
        final File file = new File(configDir(), CONN_FILE);
        if (!file.exists()) {
            return;
        }

        try {
            final List<ConnectionRecord> records = objectMapper.readValue(
                    file, new TypeReference<List<ConnectionRecord>>() {});
            for (final ConnectionRecord r : records) {
                connectionList.add(new DBConnection(
                        r.connectionName(),
                        r.hostName(),
                        r.port(),
                        r.databaseName(),
                        r.userName(),
                        ""
                ));
            }
        } catch (final IOException e) {
            throw e;
        }
    }

    private File configDir() {
        return new File(System.getProperty("user.home"), CONFIG_DIR);
    }
}
