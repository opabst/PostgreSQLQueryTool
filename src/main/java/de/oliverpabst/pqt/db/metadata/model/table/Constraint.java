package de.oliverpabst.pqt.db.metadata.model.table;

public class Constraint extends TableObject {
    private final String objectName;
    private final String references;
    private final String matchType;
    private final String onChange;
    private final String onDelete;
    private final boolean isDeferrable;
    private final boolean isValid;

    public Constraint(String objectName, String references, String matchType, String onChange, String onDelete, boolean isDeferrable, boolean isValid) {
        this.objectName = objectName;
        this.references = references;
        this.matchType = matchType;
        this.onChange = onChange;
        this.onDelete = onDelete;
        this.isDeferrable = isDeferrable;
        this.isValid = isValid;
    }

    public Constraint(String objectName, String references) {
        this.objectName = objectName;
        this.references = references;
        matchType = "";
        onChange = "";
        onDelete = "";
        isDeferrable = false;
        isValid = false;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getReferences() {
        return references;
    }

    public String getMatchType() {
        return matchType;
    }

    public String getOnChange() {
        return onChange;
    }

    public String getOnDelete() {
        return onDelete;
    }

    public boolean getDeferrable() {
        return isDeferrable;
    }

    public boolean getValid() {
        return isValid;
    }

    @Override
    public String getTableObjectName() {
        return objectName;
    }

    @Override
    public TableObjectTypes getTableObjectType() {
        return TableObjectTypes.CONSTRAINT;
    }
}
