package de.oliverpabst.PQT.db.metadata.model;

import java.util.ArrayList;

public class Schema extends DatabaseObject {

    private final ArrayList<Table> tables;
    private final ArrayList<Sequence> sequences;
    private final ArrayList<Function> functions;
    private final ArrayList<View> views;

    public Schema(String _objectName, String _owner, String _acl, ArrayList<Table> _tables, ArrayList<Sequence> _sequences,
                  ArrayList<Function> _functions, ArrayList<View> _views) {
        super(_objectName, _owner, _acl);

        tables = _tables;
        sequences = _sequences;
        functions = _functions;
        views = _views;
    }

    public Schema(String _objectName, String _owner,ArrayList<Table> _tables, ArrayList<Sequence> _sequences,
                  ArrayList<Function> _functions, ArrayList<View> _views) {
        super(_objectName, _owner, "");

        tables = _tables;
        sequences = _sequences;
        functions = _functions;
        views = _views;
    }

    public ArrayList<Table> getTables() {
        return tables;
    }

    public ArrayList<Sequence> getSequences() {
        return sequences;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public ArrayList<View> getViews() {
        return views;
    }

    @Override
    public DatabaseObjectTypes getObjectType() {
        return DatabaseObjectTypes.SCHEMA;
    }
}
