package de.oliverpabst.pqt.db.metadata;

import de.oliverpabst.pqt.db.DBConnection;
import de.oliverpabst.pqt.db.metadata.model.*;
import de.oliverpabst.pqt.db.metadata.model.table.Column;
import de.oliverpabst.pqt.db.metadata.model.table.Constraint;
import de.oliverpabst.pqt.db.metadata.model.table.Index;
import de.oliverpabst.pqt.db.metadata.model.table.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

public class MetadataManager {

    private static final Logger log = LoggerFactory.getLogger(MetadataManager.class);
    private static final String COL_DATA_TYPE = "data_type";

    private boolean schemasLoaded = false;

    private final Map<String, Schema> schemas;
    private final Set<String> tablesLoadedSchemas;
    private final Set<String> functionsLoadedSchemas;
    private final Set<String> viewsLoadedSchemas;
    private final Set<String> sequencesLoadedSchemas;

    private final DBConnection dbConnection;

    public MetadataManager(final DBConnection connection) {
        schemas = new HashMap<>();
        tablesLoadedSchemas = new HashSet<>();
        functionsLoadedSchemas = new HashSet<>();
        viewsLoadedSchemas = new HashSet<>();
        sequencesLoadedSchemas = new HashSet<>();
        dbConnection = connection;
    }

    public List<String> getSchemaNames() {
        if (!schemasLoaded) {
            loadAllSchemas();
        }

        final List<String> schemaNames = new ArrayList<>(schemas.keySet());
        schemaNames.sort(Comparator.naturalOrder());

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
            schemas.clear();
            tablesLoadedSchemas.clear();
            functionsLoadedSchemas.clear();
            viewsLoadedSchemas.clear();
            sequencesLoadedSchemas.clear();
            dbConnection.executeQuery(
                    "SELECT schema_name, schema_owner FROM information_schema.schemata " +
                    "WHERE schema_name NOT IN ('information_schema', 'pg_catalog')",
                    null,
                    rs -> {
                        while (rs.next()) {
                            final String name = rs.getString("schema_name");
                            final String owner = rs.getString("schema_owner");
                            schemas.put(name, new Schema(name, owner));
                        }
                        schemasLoaded = true;
                        return null;
                    });
        } catch (final SQLException e) {
            log.error("Failed to load schemas", e);
        } catch (final RuntimeException e) {
            log.error("Failed to load schemas (connection error)", e);
        }
    }

    public void loadTablesForSchema(final String schemaName) {
        if (!schemas.containsKey(schemaName) || tablesLoadedSchemas.contains(schemaName)) {
            return;
        }
        try {
            final List<String> tableNames = dbConnection.executeQuery(
                    "SELECT table_name FROM information_schema.tables " +
                    "WHERE table_schema = ? AND table_type = 'BASE TABLE'",
                    new Object[]{schemaName},
                    rs -> {
                        final List<String> names = new ArrayList<>();
                        while (rs.next()) {
                            names.add(rs.getString("table_name"));
                        }
                        return names;
                    });

            for (final String tableName : tableNames) {
                final List<Column> columns = getTableColumns(schemaName, tableName);
                final List<Trigger> triggers = getTableTriggers(schemaName, tableName);
                final List<Index> indices = getTableIndizes(schemaName, tableName);
                final List<Constraint> constraints = getTableConstraints(schemaName, tableName);

                schemas.get(schemaName).addTable(tableName,
                        new Table(tableName, columns, constraints, indices, triggers));
            }
            tablesLoadedSchemas.add(schemaName);
        } catch (final SQLException e) {
            log.error("Failed to load tables for schema '{}'", schemaName, e);
        }
    }

    public Map<String, Table> getTablesForSchema(final String schemaName) {
        final Schema schema = schemas.get(schemaName);
        return schema != null ? schema.getAllTables() : Collections.emptyMap();
    }

    public void loadFunctionsForSchema(final String schemaName) {
        if (!schemas.containsKey(schemaName) || functionsLoadedSchemas.contains(schemaName)) {
            return;
        }
        try {
            dbConnection.executeQuery(
                    "SELECT routine_name, data_type, routine_body, routine_definition" +
                    " FROM information_schema.routines WHERE specific_schema = ?",
                    new Object[]{schemaName},
                    rs -> {
                        while (rs.next()) {
                            final String name = rs.getString("routine_name");
                            final String dataType = rs.getString(COL_DATA_TYPE);
                            final String body = rs.getString("routine_body");
                            final String def = rs.getString("routine_definition");
                            schemas.get(schemaName).addFunction(name, new Function(name, dataType, body, def));
                        }
                        return null;
                    });
            functionsLoadedSchemas.add(schemaName);
        } catch (final SQLException e) {
            log.error("Failed to load functions for schema '{}'", schemaName, e);
        }
    }

    public Map<String, Function> getFunctionsForSchema(final String schemaName) {
        final Schema schema = schemas.get(schemaName);
        return schema != null ? schema.getAllFunctions() : Collections.emptyMap();
    }

    public void loadViewsForSchema(final String schemaName) {
        if (!schemas.containsKey(schemaName) || viewsLoadedSchemas.contains(schemaName)) {
            return;
        }
        try {
            dbConnection.executeQuery(
                    "SELECT table_name, view_definition FROM information_schema.views WHERE table_schema = ?",
                    new Object[]{schemaName},
                    rs -> {
                        while (rs.next()) {
                            final String name = rs.getString("table_name");
                            final String def = rs.getString("view_definition");
                            schemas.get(schemaName).addView(name, new View(name, def));
                        }
                        return null;
                    });
            viewsLoadedSchemas.add(schemaName);
        } catch (final SQLException e) {
            log.error("Failed to load views for schema '{}'", schemaName, e);
        }
    }

    public Map<String, View> getViewsForSchema(final String schemaName) {
        final Schema schema = schemas.get(schemaName);
        return schema != null ? schema.getAllViews() : Collections.emptyMap();
    }

    public void loadSequencesForSchema(final String schemaName) {
        if (!schemas.containsKey(schemaName) || sequencesLoadedSchemas.contains(schemaName)) {
            return;
        }
        try {
            dbConnection.executeQuery(
                    "SELECT sequence_name, data_type, start_value, minimum_value, maximum_value, increment" +
                    " FROM information_schema.sequences WHERE sequence_schema = ?",
                    new Object[]{schemaName},
                    rs -> {
                        while (rs.next()) {
                            final String name = rs.getString("sequence_name");
                            final String dataType = rs.getString(COL_DATA_TYPE);
                            final Long start = rs.getLong("start_value");
                            final Long min = rs.getLong("minimum_value");
                            final Long max = rs.getLong("maximum_value");
                            final Long inc = rs.getLong("increment");
                            schemas.get(schemaName).addSequence(name, new Sequence(name, dataType, start, min, max, inc));
                        }
                        return null;
                    });
            sequencesLoadedSchemas.add(schemaName);
        } catch (final SQLException e) {
            log.error("Failed to load sequences for schema '{}'", schemaName, e);
        }
    }

    public Map<String, Sequence> getSequencesForSchema(final String schemaName) {
        final Schema schema = schemas.get(schemaName);
        return schema != null ? schema.getAllSequences() : Collections.emptyMap();
    }

    private List<Column> getTableColumns(final String schemaName, final String tableName) {
        try {
            return dbConnection.executeQuery(
                    "SELECT column_name, ordinal_position, data_type, is_nullable" +
                    " FROM information_schema.columns WHERE table_schema = ? AND table_name = ?" +
                    " ORDER BY ordinal_position",
                    new Object[]{schemaName, tableName},
                    rs -> {
                        final List<Column> columns = new ArrayList<>();
                        while (rs.next()) {
                            final String name = rs.getString("column_name");
                            final int pos = rs.getInt("ordinal_position");
                            final String dataType = rs.getString(COL_DATA_TYPE);
                            final boolean nullable = "YES".equalsIgnoreCase(rs.getString("is_nullable"));
                            columns.add(new Column(pos, name, dataType, nullable));
                        }
                        return columns;
                    });
        } catch (final SQLException e) {
            log.error("Failed to load columns for {}.{}", schemaName, tableName, e);
            return Collections.emptyList();
        }
    }

    private List<Trigger> getTableTriggers(final String schemaName, final String tableName) {
        try {
            return dbConnection.executeQuery(
                    "SELECT trigger_name, action_timing, event_manipulation, action_orientation, " +
                    "action_statement FROM information_schema.triggers " +
                    "WHERE trigger_schema = ? AND event_object_table = ? " +
                    "ORDER BY trigger_name",
                    new Object[]{schemaName, tableName},
                    rs -> {
                        final List<Trigger> triggers = new ArrayList<>();
                        while (rs.next()) {
                            final String triggerName = rs.getString("trigger_name");
                            final String timing = rs.getString("action_timing");
                            final String event = rs.getString("event_manipulation");
                            final String orientation = rs.getString("action_orientation");
                            final String function = rs.getString("action_statement");

                            triggers.add(new Trigger(
                                    triggerName,
                                    false,
                                    timing,
                                    event,
                                    orientation,
                                    function,
                                    true));
                        }
                        return triggers;
                    });
        } catch (final SQLException e) {
            log.error("Failed to load triggers for {}.{}", schemaName, tableName, e);
            return Collections.emptyList();
        }
    }

    private List<Index> getTableIndizes(final String schemaName, final String tableName) {
        try {
            return dbConnection.executeQuery(
                    "SELECT indexname, indexdef FROM pg_indexes " +
                    "WHERE schemaname = ? AND tablename = ? " +
                    "ORDER BY indexname",
                    new Object[]{schemaName, tableName},
                    rs -> {
                        final List<Index> indexes = new ArrayList<>();
                        while (rs.next()) {
                            final String indexName = rs.getString("indexname");
                            final String indexDef = rs.getString("indexdef");
                            indexes.add(new Index(indexName, "btree", indexDef));
                        }
                        return indexes;
                    });
        } catch (final SQLException e) {
            log.error("Failed to load indexes for {}.{}", schemaName, tableName, e);
            return Collections.emptyList();
        }
    }

    private List<Constraint> getTableConstraints(final String schemaName, final String tableName) {
        try {
            return dbConnection.executeQuery(
                    "SELECT ccu.column_name, tc.constraint_schema, tc.constraint_name, tc.constraint_type " +
                    "FROM information_schema.table_constraints tc " +
                    "JOIN information_schema.constraint_column_usage ccu " +
                    "ON ccu.constraint_schema = tc.constraint_schema " +
                    "AND ccu.constraint_name = tc.constraint_name " +
                    "WHERE tc.table_schema = ? AND tc.table_name = ? " +
                    "ORDER BY tc.constraint_name",
                    new Object[]{schemaName, tableName},
                    rs -> {
                        final List<Constraint> constraints = new ArrayList<>();
                        while (rs.next()) {
                            final String columnName = rs.getString("column_name");
                            final String constraintSchema = rs.getString("constraint_schema");
                            final String constraintName = rs.getString("constraint_name");
                            final String constraintType = rs.getString("constraint_type");
                            constraints.add(new Constraint(constraintName, constraintType + " (" + constraintSchema + "." + columnName + ")"));
                        }
                        return constraints;
                    });
        } catch (final SQLException e) {
            log.error("Failed to load constraints for {}.{}", schemaName, tableName, e);
            return Collections.emptyList();
        }
    }
}


