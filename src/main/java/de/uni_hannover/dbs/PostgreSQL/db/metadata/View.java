package de.uni_hannover.dbs.PostgreSQL.db.metadata;

public class View extends DatabaseObject {

    private final String viewDefinition;
    private final Boolean isMaterialized;

    public View(String _objectName, String _owner, String _acl, String _viewDefinition, Boolean _isMaterialized) {
        super(_objectName, _owner, _acl);

        viewDefinition = _viewDefinition;
        isMaterialized = _isMaterialized;
    }

    public String getViewDefinition() {
        return viewDefinition;
    }

    public Boolean getIsMaterialized() {
        return isMaterialized;
    }
}
