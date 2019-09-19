package de.oliverpabst.PQT.model;

import de.oliverpabst.PQT.db.metadata.model.DatabaseObject;
import javafx.scene.control.TreeItem;

public class DBOutlineTreeItem extends TreeItem<String> {
    private final String name;

    private final DatabaseObject dbObject;

    public DBOutlineTreeItem(String _name, DatabaseObject _object) {
        super(_name);
        name = _name;
        dbObject = _object;


    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public DatabaseObject getDbObject() {
        return dbObject;
    }
}
