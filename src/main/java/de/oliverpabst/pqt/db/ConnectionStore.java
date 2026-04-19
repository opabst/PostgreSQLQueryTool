package de.oliverpabst.pqt.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConnectionStore {
    private static ConnectionStore instance;

    private static final String CONFIG_DIR = ".pqt";
    private static final String CONN_FILE = "connections.json";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ObservableList<DBConnection> connectionList;

    private ConnectionStore() {
        connectionList = FXCollections.observableArrayList();
    }

    public static ConnectionStore getInstance() {
        if (instance == null) {
            instance = new ConnectionStore();
        }
        return instance;
    }

    public boolean addConnection(final DBConnection connection) {
        final Iterator<DBConnection> connectionIterator = connectionList.iterator();
        while (connectionIterator.hasNext()) {
            DBConnection existingCon = connectionIterator.next();
            if (existingCon.equals(connection)) {
                return false;
            }
        }
        connectionList.add(connection);
        return true;
    }

    public DBConnection getConnection(final String connectionName) {
        DBConnection connection = null;
        for (final DBConnection dbConnection : connectionList) {
            if (dbConnection.getConnectionName().equals(connectionName)) {
                connection = dbConnection;
                break;
            }
        }
        return connection;
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
    public boolean writeConnectionsToDisk() {
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
            return true;
        } catch (final IOException e) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("IO Error");
            alert.setContentText("Could not write connections file: " + e.getMessage());
            alert.show();
            return false;
        }
    }

    /**
     * Reads connections from ~/.pqt/connections.json. Passwords are never stored,
     * so DBConnection objects are created without a password; the user must enter
     * the password in the UI before connecting.
     * If the file does not exist (first launch), this method returns true silently.
     */
    public boolean readConnectionsFromDisk() {
        final File file = new File(configDir(), CONN_FILE);
        if (!file.exists()) {
            return true;
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
            return true;
        } catch (final IOException e) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("IO Error");
            alert.setContentText("Could not read connections file: " + e.getMessage());
            alert.show();
            return false;
        }
    }

    private File configDir() {
        return new File(System.getProperty("user.home"), CONFIG_DIR);
    }
}
