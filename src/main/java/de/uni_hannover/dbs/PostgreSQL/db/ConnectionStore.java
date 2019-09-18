package de.uni_hannover.dbs.PostgreSQL.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.io.*;
import java.util.ArrayList;

public class ConnectionStore {
    private static ConnectionStore _instance;

    private static final String CONFIG_PATH = ".pqt/";
    private static final String CRED_FILE = "pqt_credentials.ser";

    private ObservableList<DBConnection> conList;

    private ConnectionStore() {
        conList = FXCollections.observableArrayList();
    }

    public static ConnectionStore getInstance() {
        if(_instance == null) {
            _instance = new ConnectionStore();
        }

        return _instance;
    }

    public void addConnection(DBConnection _con) {
        conList.add(_con);
    }

    public DBConnection getConnection(String _connectionName) {
        DBConnection connection = null;
        for(DBConnection con: conList) {
            if(con.getConnectionname().equals(_connectionName)) {
                connection = con;
                break;
            }
        }

        return connection;
    }

    public boolean removeConnection(String _name) {
        for(DBConnection con: conList) {
            if(con.getConnectionname().equals(_name)) {
                return conList.removeAll(con);
            }
        }
        return false;
    }

    public ObservableList<DBConnection> getConnections() {
        return conList;
    }

    public Boolean closeAllConnections() {
        for(DBConnection con: conList) {
            con.disconnect();
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
            if(!path.exists()) {
                path.mkdirs();
            }
            fileOut = new FileOutputStream(file, false);
            objOut = new ObjectOutputStream(fileOut);

            ArrayList<DBConnection> connections = new ArrayList<>(conList);

            objOut.writeObject(connections);

        } catch (FileNotFoundException e) {
            // Nichts tun. Kein Fehler
            return true;
        } catch (IOException e) {
            Alert ioeAlert = new Alert(Alert.AlertType.ERROR);
            ioeAlert.setHeaderText("IO-Fehler");
            ioeAlert.setContentText("Zugriff auf Datei " + filePath + " fehlgeschlagen!");
            ioeAlert.show();
            return false;
        } finally {
            if(objOut != null) {
                try {
                    objOut.close();
                } catch (IOException e) {
                    return false;
                }
            }
            if(fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
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

            ArrayList<DBConnection> connections = (ArrayList<DBConnection>)objIn.readObject();
            conList.addAll(connections);

        } catch (FileNotFoundException e) {
            // TODO: Evtl. annehmen, dass Anwendung noch nicht gestartet wurde? Also keinen Fehler werfen?
            Alert fnfeAlert = new Alert(Alert.AlertType.ERROR);
            fnfeAlert.setHeaderText("Lesen der Zugangsdaten fehlgeschlagen!");
            fnfeAlert.setContentText("Datei konnte " + fileDir + "nicht gefunden werden!");
            fnfeAlert.show();
            return false;
        } catch (IOException e) {
            Alert ioeAlert = new Alert(Alert.AlertType.ERROR);
            ioeAlert.setHeaderText("IO-Fehler");
            ioeAlert.setContentText("Zugriff auf Datei " + filePath + " fehlgeschlagen!");
            ioeAlert.show();
            return false;
        } catch (ClassNotFoundException e) {
            Alert cnfeAlert = new Alert(Alert.AlertType.ERROR);
            cnfeAlert.setHeaderText("Klasse konnte nicht gefunden werden!");
            cnfeAlert.setContentText("Klasse ArrayList<DBConnection> konnte nicht gefunden werden!");
            cnfeAlert.show();
            e.printStackTrace();
        } finally {
            if (objIn != null) {
                try {
                    objIn.close();
                } catch (IOException e) {
                    return false;
                }
            }
            if(fileIn != null) {
                try {
                    fileIn.close();
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return true;
    }
}
