package de.oliverpabst.pqt.db.metadata.model;

import java.util.HashMap;

public class Schema extends DatabaseObject {

    private final HashMap<String, Table> tables;
    private final HashMap<String, Sequence> sequences;
    private final HashMap<String, Function> functions;
    private final HashMap<String, View> views;

    public Schema(String _schemaName, String _schemaOwner) {
        super(_schemaName, _schemaOwner, "");

        tables = new HashMap<>();
        sequences = new HashMap<>();
        functions = new HashMap<>();
        views = new HashMap<>();
    }

    public void addTable(String _tableName, Table _table) {
        tables.put(_tableName, _table);
    }

    public void addSequence(String _sequenceName, Sequence _sequence) {
        sequences.put(_sequenceName, _sequence);
    }

    public void addFunction(String _functionName, Function _function) {
        functions.put(_functionName, _function);
    }

    public void addView(String _viewName, View _view) {
        views.put(_viewName, _view);
    }

    public Table getTable(String _tableName) {
        return tables.get(_tableName);
    }

    public Sequence getSequence(String _sequenceName) {
        return sequences.get(_sequenceName);
    }

    public Function getFunction(String _functionName) {
        return functions.get(_functionName);
    }

    public View getView(String _viewName) {
        return views.get(_viewName);
    }

    public HashMap<String, Table> getAllTables() {
        return tables;
    }

    public HashMap<String, Sequence> getAllSequences() {
        return sequences;
    }

    public HashMap<String, Function> getAllFunctions() {
        return functions;
    }

    public HashMap<String, View> getAllViews() {
        return views;
    }

    @Override
    public DatabaseObjectTypes getObjectType() {
        return DatabaseObjectTypes.SCHEMA;
    }
}
