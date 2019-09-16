package de.uni_hannover.dbs.PostgreSQL.db.metadata;

import de.uni_hannover.dbs.PostgreSQL.db.DBConnection;
import de.uni_hannover.dbs.PostgreSQL.db.metadata.model.*;
import de.uni_hannover.dbs.PostgreSQL.db.metadata.model.table.Column;
import de.uni_hannover.dbs.PostgreSQL.db.metadata.model.table.Constraint;
import de.uni_hannover.dbs.PostgreSQL.db.metadata.model.table.Index;
import de.uni_hannover.dbs.PostgreSQL.db.metadata.model.table.Trigger;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class MetadataStore {

    private static MetadataStore instance = null;

    private static HashMap<String, Schema> schemas;

    private MetadataStore() {
        schemas = new HashMap<>();
    }

    public static MetadataStore getInstance() {
        if(instance == null) {
            instance = new MetadataStore();
        }
        return instance;
    }

    public static Schema getSchema(String _key) {
        return schemas.get(_key);
    }

    public static ArrayList<Schema> getAllSchemas() {
        return new ArrayList<>(schemas.values());
    }

    public void populateMetadataForConnection(DBConnection _con) {
        try {
            ResultSet rs = _con.executeQuery("SELECT schema_name, schema_owner FROM information_schema.schemata " +
                    "WHERE schema_name NOT IN ('information_schema', 'pg_catalog')");

            while (rs.next()) {
                String schema_name = rs.getString("schema_name");
                String schema_owner = rs.getString("schema_owner");

                ArrayList<Sequence> sequences = getSequences(schema_name, _con);

                ArrayList<Function> functions = getFunctions(schema_name, _con);

                ArrayList<View> views = getViews(schema_name, _con);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Sequence> getSequences(String _schemaName, DBConnection _con) {
        ArrayList<Sequence> sequences = new ArrayList<>();

        try {
            ResultSet rs = _con.executeQuery("SELECT sequence_name, data_type, start_value, minimum_value, maximum_value, increment "
                    + " FROM information_schema.sequences WHERE sequence_schema = '" + _schemaName + "'");
            while(rs.next()) {
                String sequenceName = rs.getString("sequence_name");
                String dataType = rs.getString("data_type");
                Integer startValue = rs.getInt("start_value");
                Integer minValue = rs.getInt("minimum_value");
                Integer maxValue = rs.getInt("maximum_value");
                Integer increment = rs.getInt("increment");
                sequences.add(new Sequence(sequenceName, dataType, startValue, minValue, maxValue, increment));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sequences;
    }

    private static ArrayList<Function> getFunctions(String _schemaName, DBConnection _con) {
        ArrayList<Function> functions = new ArrayList<>();

        // information_schema.parameters

        try {
            ResultSet rs = _con.executeQuery("SELECT routine_name, data_type, routine_body, routine_definition FROM information_schema.routines");

            while (rs.next()) {
                String funcName = rs.getString("routine_name");
                String dataType = rs.getString("data_type");
                String funcBody = rs.getString("routine_body");
                String funcDef = rs.getString("routine_definition");

                functions.add(new Function(funcName, dataType, funcBody, funcDef));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return functions;
    }

    private static ArrayList<Table> getTables(String _schemaName, DBConnection _con) {
        ArrayList<Table> tables = new ArrayList<>();
        ArrayList<String> tableNames = new ArrayList<>();

        try {
            ResultSet rs = _con.executeQuery("SELECT table_name FROM information_schema WHERE table_schema = " + _schemaName);

            while (rs.next()) {
                String tableName = rs.getString("table_name");

                tableNames.add(tableName);
            }

            for(String tableName: tableNames) {
                ArrayList<Column> columns = getTableColumns(_schemaName, tableName, _con);
                ArrayList<Trigger> triggers = getTableTriggers(_schemaName, tableName, _con);
                ArrayList<Index> indizes = getTableIndizes(_schemaName, tableName, _con);
                ArrayList<Constraint> constraints = getTableConstraints(_schemaName, tableName, _con);

                tables.add(new Table(tableName, columns, constraints, indizes, triggers));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tables;
    }

    private static ArrayList<Column> getTableColumns(String _schemaName, String _tableName, DBConnection _con) {
        ArrayList<Column> columns = new ArrayList<>();

        try {
            ResultSet rs = _con.executeQuery("SELECT column_name, ordinal_position, data_type, is_nullable, character_maximum_length, numeric_precision," +
                    " numeric_precision_radix, numeric_scale, datetime_precision, interval_type " +
                    " FROM information_schema.columns WHERE schema_name = '" + _schemaName + "' AND table_name = '" + _tableName + "'");

            while(rs.next()) {
                // TODO: handle NULL-values!
                String columnName = rs.getString("columnName");
                Integer columnPos = rs.getInt("ordinal_position");
                String dataType = rs.getString("data_type");
                Boolean isNullable = rs.getBoolean("is_nullable");
                Integer maxStringLength = rs.getInt("character_maximum_length"); // if dataType is CHAR or bit string
                Integer numericPrecision = rs.getInt("numeric_precision"); // if numeric
                Integer numericPrecisionRadix = rs.getInt("numeric_precision_radix"); // if numeric determines the base of expression
                Integer numericScale = rs.getInt("numeric_scale"); // if numeric
                Integer datetimePrecision = rs.getInt("datetime_precision");
                String intervalType = rs.getString("interval_type");

                columns.add(new Column(columnPos, columnName, dataType, isNullable));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return columns;
    }

    private static ArrayList<Trigger> getTableTriggers(String _schemaName, String _tableName, DBConnection _con) {
        ArrayList<Trigger> triggers = new ArrayList<>();
        // information_schema.triggers
        // TODO: implement

        return triggers;
    }

    private static ArrayList<Index> getTableIndizes(String _schemaName, String _tableName, DBConnection _con) {
        ArrayList<Index> indizes = new ArrayList<>();


        // pg_indexes
        // TODO: implement

        return indizes;
    }

    private static ArrayList<Constraint> getTableConstraints(String _schemaName, String _tableName, DBConnection _con) {
        ArrayList<Constraint> constraints = new ArrayList<>();
        // information_schema.referential_constraints
        // information_schema.table_constraints
        // information_schema.check_constraints
        // information_schema.key_column_usage
        try {
            ResultSet rs = _con.executeQuery("SELECT column_name, constraint_schema, constraint_name " +
                    "FROM information_schema.constraint_column_usage WHERE table_schema = '" + _schemaName + "' AND " +
                    " table_name = '" + _tableName + "'");

            while (rs.next()) {
                String columnName = rs.getString("column_name");
                String constrainSchema = rs.getString("constraint_schema");
                String constraintName = rs.getString("constraint_name");

                constraints.add(new Constraint(constraintName, constrainSchema.concat(columnName)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return constraints;
    }

    private static ArrayList<View> getViews(String _schemaName, DBConnection _con) {
        ArrayList<View> views = new ArrayList<>();

        try {
            ResultSet rs = _con.executeQuery("SELECT table_name, view_definition " +
                    "FROM information_schema.views WHERE table_schema = '" + _schemaName + "'");
            while (rs.next()) {
                String viewName = rs.getString("table_name");
                String viewDefinition = rs.getString("view_definition");

                views.add(new View(viewName, viewDefinition));
            }

            // TODO: where to get information for materialized views?
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return views;
    }
}
