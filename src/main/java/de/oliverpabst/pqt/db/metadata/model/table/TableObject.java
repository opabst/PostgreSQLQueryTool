package de.oliverpabst.pqt.db.metadata.model.table;

public abstract class TableObject {

    public TableObject() {

    }

    public abstract String getTableObjectName();

    public abstract TableObjectTypes getTableObjectType();
}
