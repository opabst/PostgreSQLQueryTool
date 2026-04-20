package de.oliverpabst.pqt.db.metadata.model;

public class View extends DatabaseObject {

    private final String viewDefinition;
    private final boolean isMaterialized;

    public View(String objectName, String owner, String acl, String viewDefinition, boolean isMaterialized) {
        super(objectName, owner, acl);

        this.viewDefinition = viewDefinition;
        this.isMaterialized = isMaterialized;
    }

    public View(String objectName, String viewDefinition) {
        super(objectName, "", "");

        this.viewDefinition = viewDefinition;
        isMaterialized = false;
    }

    public View(String objectName, String viewDefinition, boolean isMatrialized) {
        super(objectName, "", "");

        this.viewDefinition = viewDefinition;
        isMaterialized = isMatrialized;
    }

    public String getViewDefinition() {
        return viewDefinition;
    }

    public boolean getIsMaterialized() {
        return isMaterialized;
    }

    @Override
    public DatabaseObjectTypes getObjectType() {
        return DatabaseObjectTypes.VIEW;
    }
}
