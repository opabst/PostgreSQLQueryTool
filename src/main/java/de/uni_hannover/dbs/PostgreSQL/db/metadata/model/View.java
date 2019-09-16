package de.uni_hannover.dbs.PostgreSQL.db.metadata.model;

import de.uni_hannover.dbs.PostgreSQL.db.metadata.model.DatabaseObject;

public class View extends DatabaseObject {

    private final String viewDefinition;
    private final Boolean isMaterialized;

    public View(String _objectName, String _owner, String _acl, String _viewDefinition, Boolean _isMaterialized) {
        super(_objectName, _owner, _acl);

        viewDefinition = _viewDefinition;
        isMaterialized = _isMaterialized;
    }

    public View(String _objectName, String _viewDefinition) {
        super(_objectName, "", "");

        viewDefinition = _viewDefinition;
        isMaterialized = false;
    }

    public View(String _objectName, String _viewDefinition, Boolean _isMatrialized) {
        super(_objectName, "", "");

        viewDefinition = _viewDefinition;
        isMaterialized = _isMatrialized;
    }

    public String getViewDefinition() {
        return viewDefinition;
    }

    public Boolean getIsMaterialized() {
        return isMaterialized;
    }

    @Override
    public DatabaseObjectTypes getObjectType() {
        return DatabaseObjectTypes.VIEW;
    }
}
