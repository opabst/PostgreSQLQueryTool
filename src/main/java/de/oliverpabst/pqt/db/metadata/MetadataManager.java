package de.oliverpabst.pqt.db.metadata;

import de.oliverpabst.pqt.db.DBConnection;
import de.oliverpabst.pqt.db.metadata.model.*;
import de.oliverpabst.pqt.db.metadata.model.table.Column;
import de.oliverpabst.pqt.db.metadata.model.table.Constraint;
import de.oliverpabst.pqt.db.metadata.model.table.Index;
import de.oliverpabst.pqt.db.metadata.model.table.Trigger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MetadataManager {

    private boolean schemasLoaded = false;

    private final Map<String, Schema> schemas; // HashMap aus Schemas, die wiederrum die Datenbankobjekte enthält

    // TODO: Listen von Datenbankobjekten als sortierte Priority Queues vorhalten

    // TODO: Lazy Loading für Metadaten außerhalb des eigenen Schemas implementieren

    private final DBConnection dbConnection;

    public MetadataManager(final DBConnection connection) {
        schemas = new HashMap<>();
        dbConnection = connection;
    }

    /**
     * Gibt alle Schemas der ausgewählten Datenbank in aufsteigend sortierter Reihenfolge zurück
     * @return
     */
    public List<String> getSchemaNames() {
        if (!schemasLoaded) {
            loadAllSchemas();
        }

        final List<String> schemaNames = new ArrayList<>(schemas.keySet());
        final Comparator<String> c = Comparator.comparing((String x) -> x);
        schemaNames.sort(c);

        return schemaNames;
    }

    public Schema getSchema(final String key) {
        return schemas.get(key);
    }

    public List<Schema> getAllSchemas() {
        return new ArrayList<>(schemas.values());
    }

    private void loadAllSchemas() {

        try {

            final ResultSet rs = dbConnection.executeQuery("SELECT schema_name, schema_owner FROM information_schema.schemata " +
                    "WHERE schema_name NOT IN ('information_schema', 'pg_catalog')");

            while (rs.next()) {
                final String schema_name = rs.getString("schema_name");
                final String schema_owner = rs.getString("schema_owner");

                Schema s = new Schema(schema_name, schema_owner);
                schemas.put(schema_name, s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadTablesForSchema(final String schemaName) {

        ArrayList<String> tableNames = new ArrayList<>();

        try {
            final ResultSet rs = dbConnection.executeQuery("SELECT table_name FROM information_schema.tables " +
                    "WHERE table_schema = '" + schemaName + "'");

            while (rs.next()) {
                final String tableName = rs.getString("table_name");

                tableNames.add(tableName);
            }

            for (final String tableName : tableNames) {
                final List<Column> columns = getTableColumns(schemaName, tableName);
                final List<Trigger> triggers = getTableTriggers(schemaName, tableName);
                final List<Index> indices = getTableIndizes(schemaName, tableName);
                final List<Constraint> constraints = getTableConstraints(schemaName, tableName);

                schemas.get(schemaName).addTable(tableName, new Table(tableName, columns, constraints, indices, triggers));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Table> getTablesForSchema(final String schemaName) {
        return schemas.get(schemaName).getAllTables();
    }

    public void loadFunctionsForSchema(final String schemaName) {
        try {
            final ResultSet rs = dbConnection.executeQuery("SELECT routine_name, data_type, routine_body, routine_definition " +
                    " FROM information_schema.routines " +
                    " WHERE specific_schema = '" + schemaName + "'");

            while (rs.next()) {
                final String funcName = rs.getString("routine_name");
                final String dataType = rs.getString("data_type");
                final String funcBody = rs.getString("routine_body");
                final String funcDef = rs.getString("routine_definition");

                schemas.get(schemaName).addFunction(funcName, new Function(funcName, dataType, funcBody, funcDef));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Function> getFunctionsForSchema(final String schemaName) {
        return schemas.get(schemaName).getAllFunctions();
    }

    public void loadViewsForSchema(final String schemaName) {

        try {
            final ResultSet rs = dbConnection.executeQuery("SELECT table_name, view_definition " +
                    "FROM information_schema.views WHERE table_schema = '" + schemaName + "'");
            while (rs.next()) {
                final String viewName = rs.getString("table_name");
                final String viewDefinition = rs.getString("view_definition");

                schemas.get(schemaName).addView(viewName, new View(viewName, viewDefinition));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, View> getViewsForSchema(final String schemaName) {
        return schemas.get(schemaName).getAllViews();
    }

    public void loadSequencesForSchema(final String schemaName) {

        try {
            final ResultSet rs = dbConnection.executeQuery("SELECT sequence_name, data_type, start_value, minimum_value, maximum_value, increment "
                    + " FROM information_schema.sequences WHERE sequence_schema = '" + schemaName + "'");
            while (rs.next()) {
                final String sequenceName = rs.getString("sequence_name");
                final String dataType = rs.getString("data_type");
                final Long startValue = rs.getLong("start_value");
                final Long minValue = rs.getLong("minimum_value");
                final Long maxValue = rs.getLong("maximum_value");
                final Long increment = rs.getLong("increment");

                schemas.get(schemaName).addSequence(sequenceName, new Sequence(sequenceName, dataType, startValue, minValue, maxValue, increment));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Sequence> getSequencesForSchema(final String schemaName) {
        return schemas.get(schemaName).getAllSequences();
    }

    private List<Sequence> getSequences(final String schemaName) {
        final List<Sequence> sequences = new ArrayList<>();

        try {
            final ResultSet rs = dbConnection.executeQuery("SELECT sequence_name, data_type, start_value, minimum_value, maximum_value, increment "
                    + " FROM information_schema.sequences WHERE sequence_schema = '" + schemaName + "'");
            while (rs.next()) {
                final String sequenceName = rs.getString("sequence_name");
                final String dataType = rs.getString("data_type");
                final Long startValue = rs.getLong("start_value");
                final Long minValue = rs.getLong("minimum_value");
                final Long maxValue = rs.getLong("maximum_value");
                final Long increment = rs.getLong("increment");
                sequences.add(new Sequence(sequenceName, dataType, startValue, minValue, maxValue, increment));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sequences;
    }

    private List<Function> getFunctions(final String schemaName) {
        final List<Function> functions = new ArrayList<>();

        // information_schema.parameters

        try {
            final ResultSet rs = dbConnection.executeQuery("SELECT routine_name, data_type, routine_body, routine_definition " +
                    " FROM information_schema.routines " +
                    " WHERE specific_schema = '" + schemaName + "'");

            while (rs.next()) {
                final String funcName = rs.getString("routine_name");
                final String dataType = rs.getString("data_type");
                final String funcBody = rs.getString("routine_body");
                final String funcDef = rs.getString("routine_definition");

                functions.add(new Function(funcName, dataType, funcBody, funcDef));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return functions;
    }

    private List<Table> getTables(final String schemaName) {
        final List<Table> tables = new ArrayList<>();
        final List<String> tableNames = new ArrayList<>();

        try {
            final ResultSet rs = dbConnection.executeQuery("SELECT table_name FROM information_schema.tables " +
                    "WHERE table_schema = '" + schemaName + "'");

            while (rs.next()) {
                final String tableName = rs.getString("table_name");

                tableNames.add(tableName);
            }

            for (final String tableName: tableNames) {
                final List<Column> columns = getTableColumns(schemaName, tableName);
                final List<Trigger> triggers = getTableTriggers(schemaName, tableName);
                final List<Index> indizes = getTableIndizes(schemaName, tableName);
                final List<Constraint> constraints = getTableConstraints(schemaName, tableName);

                tables.add(new Table(tableName, columns, constraints, indizes, triggers));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tables;
    }

    private List<Column> getTableColumns(final String schemaName, final String tableName) {
        final List<Column> columns = new ArrayList<>();

        try {
            final ResultSet rs = dbConnection.executeQuery("SELECT column_name, ordinal_position, data_type, is_nullable, character_maximum_length, numeric_precision," +
                    " numeric_precision_radix, numeric_scale, datetime_precision, interval_type " +
                    " FROM information_schema.columns WHERE table_schema = '" + schemaName + "' AND table_name = '" + tableName + "'");

            while (rs.next()) {
                // TODO: handle NULL-values!
                final String columnName = rs.getString("column_name");
                final Integer columnPos = rs.getInt("ordinal_position");
                final String dataType = rs.getString("data_type");
                final Boolean isNullable = rs.getBoolean("is_nullable");
                final Integer maxStringLength = rs.getInt("character_maximum_length"); // if dataType is CHAR or bit string
                final Integer numericPrecision = rs.getInt("numeric_precision"); // if numeric
                final Integer numericPrecisionRadix = rs.getInt("numeric_precision_radix"); // if numeric determines the base of expression
                final Integer numericScale = rs.getInt("numeric_scale"); // if numeric
                final Integer datetimePrecision = rs.getInt("datetime_precision");
                final String intervalType = rs.getString("interval_type");

                columns.add(new Column(columnPos, columnName, dataType, isNullable));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return columns;
    }

    private List<Trigger> getTableTriggers(final String schemaName, final String tableName) {
        final List<Trigger> triggers = new ArrayList<>();
        // information_schema.triggers
        // TODO: implement

        return triggers;
    }

    private List<Index> getTableIndizes(final String schemaName, final String tableName) {
        final List<Index> indizes = new ArrayList<>();


        // pg_indexes
        // TODO: implement

        return indizes;
    }

    private List<Constraint> getTableConstraints(final String schemaName, final String tableName) {
        final List<Constraint> constraints = new ArrayList<>();
        // information_schema.referential_constraints
        // information_schema.table_constraints
        // information_schema.check_constraints
        // information_schema.key_column_usage
        try {
            final ResultSet rs = dbConnection.executeQuery("SELECT column_name, constraint_schema, constraint_name " +
                    "FROM information_schema.constraint_column_usage WHERE table_schema = '" + schemaName + "' AND " +
                    " table_name = '" + tableName + "'");

            while (rs.next()) {
                final String columnName = rs.getString("column_name");
                final String constrainSchema = rs.getString("constraint_schema");
                final String constraintName = rs.getString("constraint_name");

                constraints.add(new Constraint(constraintName, constrainSchema.concat(columnName)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return constraints;
    }

    private List<View> getViews(final String schemaName) {
        final List<View> views = new ArrayList<>();

        try {
            final ResultSet rs = dbConnection.executeQuery("SELECT table_name, view_definition " +
                    "FROM information_schema.views WHERE table_schema = '" + schemaName + "'");
            while (rs.next()) {
                final String viewName = rs.getString("table_name");
                final String viewDefinition = rs.getString("view_definition");

                views.add(new View(viewName, viewDefinition));
            }

            // TODO: where to get information for materialized views?
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return views;
    }

    private void createSchemaSkeleton() {

    }
}
