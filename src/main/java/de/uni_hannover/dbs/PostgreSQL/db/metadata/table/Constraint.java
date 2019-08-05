package de.uni_hannover.dbs.PostgreSQL.db.metadata.table;

public class Constraint {
    private final String objectName;
    private final String references;
    private final String matchType;
    private final String onChange;
    private final String onDelete;
    private final Boolean isDeferrable;
    private final Boolean isValid;

    public Constraint(String _objectname, String _references, String _matchType, String _onChange, String _onDelete, Boolean _isDeferrable, Boolean _isValid) {
        objectName = _objectname;
        references = _references;
        matchType = _matchType;
        onChange = _onChange;
        onDelete = _onDelete;
        isDeferrable = _isDeferrable;
        isValid = _isValid;
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

    public Boolean getDeferrable() {
        return isDeferrable;
    }

    public Boolean getValid() {
        return isValid;
    }
}
