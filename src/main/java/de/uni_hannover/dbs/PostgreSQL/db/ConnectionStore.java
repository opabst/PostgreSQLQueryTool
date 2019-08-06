package de.uni_hannover.dbs.PostgreSQL.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;

public class ConnectionStore {
    private static ConnectionStore _instance;

    private static final String CONFIG_PATH = "~/.pqt/";
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
        FileOutputStream fileOut = null;
        ObjectOutputStream objOut = null;
        try {
            File path = new File(CONFIG_PATH);
            File file = new File(CONFIG_PATH + CRED_FILE);
            if(!path.exists()) {
                path.mkdirs();
            }
            fileOut = new FileOutputStream(file);
            objOut = new ObjectOutputStream(fileOut);

            ArrayList<DBConnection> connections = new ArrayList<>(conList);

            objOut.writeObject(connections);

        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
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
        FileInputStream fileIn = null;
        ObjectInputStream objIn = null;
        try {
            File path = new File(CONFIG_PATH + CRED_FILE);
            fileIn = new FileInputStream(path);
            objIn = new ObjectInputStream(fileIn);

            ArrayList<DBConnection> connections = (ArrayList<DBConnection>)objIn.readObject();
            conList = FXCollections.observableArrayList(connections);

        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (ClassNotFoundException e) {
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
