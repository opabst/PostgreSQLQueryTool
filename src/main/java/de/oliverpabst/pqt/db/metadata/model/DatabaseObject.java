package de.oliverpabst.pqt.db.metadata.model;

public abstract class DatabaseObject {
    private final String objectName;
    private final String owner;
    private final String accessControlList;

    public DatabaseObject(final String _objectName, final String _owner, final String _acl) {
        objectName = _objectName;
        owner = _owner;
        accessControlList = _acl;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getOwner() {
        return owner;
    }

    public String getAccessControlList() {
        return accessControlList;
    }

    public abstract DatabaseObjectTypes getObjectType();
}
