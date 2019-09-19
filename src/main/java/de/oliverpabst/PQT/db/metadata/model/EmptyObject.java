package de.oliverpabst.PQT.db.metadata.model;

public class EmptyObject extends DatabaseObject {
    public EmptyObject(String _objectName, String _owner, String _acl) {
        super(_objectName, _owner, _acl);
    }

    @Override
    public DatabaseObjectTypes getObjectType() {
        return DatabaseObjectTypes.NULL;
    }
}
