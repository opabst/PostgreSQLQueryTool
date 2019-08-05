package de.uni_hannover.dbs.PostgreSQL.db.metadata;

import java.util.ArrayList;

public class Schema extends DatabaseObject {

    private final ArrayList<Table> tables;
    private final ArrayList<Sequence> sequences;
    private final ArrayList<Function> functions;
    private final ArrayList<Trigger> triggers;
    private final ArrayList<View> views;

    public Schema(String _objectName, String _owner, String _acl, ArrayList<Table> _tables, ArrayList<Sequence> _sequences,
                  ArrayList<Function> _functions, ArrayList<Trigger> _triggers, ArrayList<View> _views) {
        super(_objectName, _owner, _acl);

        tables = _tables;
        sequences = _sequences;
        functions = _functions;
        triggers = _triggers;
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

    public ArrayList<Trigger> getTriggers() {
        return triggers;
    }

    public ArrayList<View> getViews() {
        return views;
    }
}
