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

    private boolean schemasLoaded = false;

    private final Map<String, Schema> schemas;

    // TODO: Lazy loading for non-current schemas
    // TODO: Implement indices and triggers retrieval

    private final DBConnection dbConnection;

    public MetadataManager(final DBConnection connection) {
        schemas = new HashMap<>();
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
        } catch (SQLException e) {
            log.error("Failed to load schemas", e);
        }
    }

    public void loadTablesForSchema(final String schemaName) {
        try {
            final List<String> tableNames = dbConnection.executeQuery(
                    "SELECT table_name FROM information_schema.tables WHERE table_schema = ?",
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
        } catch (SQLException e) {
            log.error("Failed to load tables for schema '{}'", schemaName, e);
        }
    }

    public Map<String, Table> getTablesForSchema(final String schemaName) {
        return schemas.get(schemaName).getAllTables();
    }

    public void loadFunctionsForSchema(final String schemaName) {
        try {
            dbConnection.executeQuery(
                    "SELECT routine_name, data_type, routine_body, routine_definition" +
                    " FROM information_schema.routines WHERE specific_schema = ?",
                    new Object[]{schemaName},
                    rs -> {
                        while (rs.next()) {
                            final String name = rs.getString("routine_name");
                            final String dataType = rs.getString("data_type");
                            final String body = rs.getString("routine_body");
                            final String def = rs.getString("routine_definition");
                            schemas.get(schemaName).addFunction(name, new Function(name, dataType, body, def));
                        }
                        return null;
                    });
        } catch (SQLException e) {
            log.error("Failed to load functions for schema '{}'", schemaName, e);
        }
    }

    public Map<String, Function> getFunctionsForSchema(final String schemaName) {
        return schemas.get(schemaName).getAllFunctions();
    }

    public void loadViewsForSchema(final String schemaName) {
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
        } catch (SQLException e) {
            log.error("Failed to load views for schema '{}'", schemaName, e);
        }
    }

    public Map<String, View> getViewsForSchema(final String schemaName) {
        return schemas.get(schemaName).getAllViews();
    }

    public void loadSequencesForSchema(final String schemaName) {
        try {
            dbConnection.executeQuery(
                    "SELECT sequence_name, data_type, start_value, minimum_value, maximum_value, increment" +
                    " FROM information_schema.sequences WHERE sequence_schema = ?",
                    new Object[]{schemaName},
                    rs -> {
                        while (rs.next()) {
                            final String name = rs.getString("sequence_name");
                            final String dataType = rs.getString("data_type");
                            final Long start = rs.getLong("start_value");
                            final Long min = rs.getLong("minimum_value");
                            final Long max = rs.getLong("maximum_value");
                            final Long inc = rs.getLong("increment");
                            schemas.get(schemaName).addSequence(name, new Sequence(name, dataType, start, min, max, inc));
                        }
                        return null;
                    });
        } catch (SQLException e) {
            log.error("Failed to load sequences for schema '{}'", schemaName, e);
        }
    }

    public Map<String, Sequence> getSequencesForSchema(final String schemaName) {
        return schemas.get(schemaName).getAllSequences();
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
                            final String dataType = rs.getString("data_type");
                            final boolean nullable = rs.getBoolean("is_nullable");
                            columns.add(new Column(pos, name, dataType, nullable));
                        }
                        return columns;
                    });
        } catch (SQLException e) {
            log.error("Failed to load columns for {}.{}", schemaName, tableName, e);
            return Collections.emptyList();
        }
    }

    private List<Trigger> getTableTriggers(final String schemaName, final String tableName) {
        // TODO: implement via information_schema.triggers
        return Collections.emptyList();
    }

    private List<Index> getTableIndizes(final String schemaName, final String tableName) {
        // TODO: implement via pg_indexes
        return Collections.emptyList();
    }

    private List<Constraint> getTableConstraints(final String schemaName, final String tableName) {
        try {
            return dbConnection.executeQuery(
                    "SELECT column_name, constraint_schema, constraint_name" +
                    " FROM information_schema.constraint_column_usage" +
                    " WHERE table_schema = ? AND table_name = ?",
                    new Object[]{schemaName, tableName},
                    rs -> {
                        final List<Constraint> constraints = new ArrayList<>();
                        while (rs.next()) {
                            final String columnName = rs.getString("column_name");
                            final String constraintSchema = rs.getString("constraint_schema");
                            final String constraintName = rs.getString("constraint_name");
                            constraints.add(new Constraint(constraintName, constraintSchema.concat(columnName)));
                        }
                        return constraints;
                    });
        } catch (SQLException e) {
            log.error("Failed to load constraints for {}.{}", schemaName, tableName, e);
            return Collections.emptyList();
        }
    }
}


