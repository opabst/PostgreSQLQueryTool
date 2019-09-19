package de.oliverpabst.PQT.db.metadata.model.table;

public abstract class TableObject {

    public TableObject() {

    }

    public abstract String getTableObjectName();

    public abstract TableObjectTypes getTableObjectType();
}
