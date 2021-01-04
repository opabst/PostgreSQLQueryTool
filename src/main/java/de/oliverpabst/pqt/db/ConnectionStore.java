package de.oliverpabst.pqt.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class ConnectionStore {
    private static ConnectionStore instance;

    private static final String CONFIG_PATH = ".pqt/";
    private static final String CRED_FILE = "pqt_credentials.ser";

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

            if(existingCon.equals(connection)) {
                return false;
            }
        }
        connectionList.add(connection);
        return true;
    }

    public DBConnection getConnection(final String connectionName) {
        DBConnection connection = null;
        for(final DBConnection dbConnection: connectionList) {
            if(dbConnection.getConnectionName().equals(connectionName)) {
                connection = dbConnection;
                break;
            }
        }

        return connection;
    }

    public boolean removeConnection(final String connectionName) {
        for (final DBConnection connection: connectionList) {
            if (connection.getConnectionName().equals(connectionName)) {
                return connectionList.removeAll(connection);
            }
        }

        return false;
    }

    public ObservableList<DBConnection> getConnections() {
        return connectionList;
    }

    public Boolean closeAllConnections() {
        for (final DBConnection connection: connectionList) {
            connection.disconnect();
        }
        return true;
    }

    public Boolean writeCredentialsToDisk() {
        String homeDirectory = System.getProperty("user.home");
        String filePath = homeDirectory + "/" + CONFIG_PATH + CRED_FILE;
        String fileDir = homeDirectory + "/" + CONFIG_PATH;
        FileOutputStream fileOut = null;
        ObjectOutputStream objOut = null;
        try {
            File path = new File(fileDir);
            File file = new File(filePath);
            if (!path.exists()) {
                path.mkdirs();
            }
            fileOut = new FileOutputStream(file, false);
            objOut = new ObjectOutputStream(fileOut);

            ArrayList<DBConnection> connections = new ArrayList<>(connectionList);
            closeAllConnections();

            objOut.writeObject(connections);

        } catch (final FileNotFoundException e) {
            // The config file does not exist yet. This is not an error!
            return true;
        } catch (final IOException e) {
            final Alert ioeAlert = new Alert(Alert.AlertType.ERROR);
            ioeAlert.setHeaderText("IO-Fehler");
            ioeAlert.setContentText("Zugriff auf Datei " + filePath + " fehlgeschlagen!" + "\n" + e.toString());
            ioeAlert.show();
            return false;
        } finally {
            if (objOut != null) {
                try {
                    objOut.close();
                } catch (final IOException e) {
                    return false;
                }
            }
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (final IOException e) {
                    return false;
                }
            }
        }
        return true;
    }

    public Boolean readCredentialsFromDisk() {
        String homeDirectory = System.getProperty("user.home");
        String filePath = homeDirectory + "/" + CONFIG_PATH + CRED_FILE;
        String fileDir = homeDirectory + "/" + CONFIG_PATH;
        FileInputStream fileIn = null;
        ObjectInputStream objIn = null;
        try {
            File path = new File(filePath);
            fileIn = new FileInputStream(path);
            objIn = new ObjectInputStream(fileIn);

            final ArrayList<DBConnection> connections = (ArrayList<DBConnection>)objIn.readObject();
            connectionList.addAll(connections);

        } catch (final FileNotFoundException e) {
            // TODO: Evtl. annehmen, dass Anwendung noch nicht gestartet wurde? Also keinen Fehler werfen?
            final Alert fnfeAlert = new Alert(Alert.AlertType.ERROR);
            fnfeAlert.setHeaderText("Lesen der Zugangsdaten fehlgeschlagen!");
            fnfeAlert.setContentText("Datei konnte " + fileDir + "nicht gefunden werden!");
            fnfeAlert.show();
            return false;
        } catch (final IOException e) {
            final Alert ioeAlert = new Alert(Alert.AlertType.ERROR);
            ioeAlert.setHeaderText("IO-Fehler");
            ioeAlert.setContentText("Zugriff auf Datei " + filePath + " fehlgeschlagen!");
            ioeAlert.show();
            return false;
        } catch (final ClassNotFoundException e) {
            final Alert cnfeAlert = new Alert(Alert.AlertType.ERROR);
            cnfeAlert.setHeaderText("Klasse konnte nicht gefunden werden!");
            cnfeAlert.setContentText("Klasse ArrayList<DBConnection> konnte nicht gefunden werden!");
            cnfeAlert.show();
            e.printStackTrace();
        } finally {
            if (objIn != null) {
                try {
                    objIn.close();
                } catch (final IOException e) {
                    return false;
                }
            }
            if (fileIn != null) {
                try {
                    fileIn.close();
                } catch (final IOException e) {
                    return false;
                }
            }
        }
        return true;
    }
}
