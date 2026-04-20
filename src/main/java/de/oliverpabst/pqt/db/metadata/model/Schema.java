package de.oliverpabst.pqt.db.metadata.model;

import java.util.Map;
import java.util.TreeMap;

public class Schema extends DatabaseObject {

    private final TreeMap<String, Table> tables;
    private final TreeMap<String, Sequence> sequences;
    private final TreeMap<String, Function> functions;
    private final TreeMap<String, View> views;

    public Schema(String schemaName, String schemaOwner) {
        super(schemaName, schemaOwner, "");

        tables = new TreeMap<>();
        sequences = new TreeMap<>();
        functions = new TreeMap<>();
        views = new TreeMap<>();
    }

    public void addTable(String tableName, Table table) {
        tables.put(tableName, table);
    }

    public void addSequence(String sequenceName, Sequence sequence) {
        sequences.put(sequenceName, sequence);
    }

    public void addFunction(String functionName, Function function) {
        functions.put(functionName, function);
    }

    public void addView(String viewName, View view) {
        views.put(viewName, view);
    }

    public Table getTable(String tableName) {
        return tables.get(tableName);
    }

    public Sequence getSequence(String sequenceName) {
        return sequences.get(sequenceName);
    }

    public Function getFunction(String functionName) {
        return functions.get(functionName);
    }

    public View getView(String viewName) {
        return views.get(viewName);
    }

    public Map<String, Table> getAllTables() {
        return tables;
    }

    public Map<String, Sequence> getAllSequences() {
        return sequences;
    }

    public Map<String, Function> getAllFunctions() {
        return functions;
    }

    public Map<String, View> getAllViews() {
        return views;
    }

    @Override
    public DatabaseObjectTypes getObjectType() {
        return DatabaseObjectTypes.SCHEMA;
    }
}
