package de.uni_hannover.dbs.PostgreSQL.db.metadata;

public class DatabaseObject {
    private final String objectName;
    private final String owner;
    private final String accessControlList;

    public DatabaseObject(String _objectName, String _owner, String _acl) {
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
}
