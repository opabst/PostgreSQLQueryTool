package de.uni_hannover.dbs.PostgreSQL.db;

import de.uni_hannover.dbs.PostgreSQL.model.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ConnectionStore {
    private static ConnectionStore _instance;

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
}
