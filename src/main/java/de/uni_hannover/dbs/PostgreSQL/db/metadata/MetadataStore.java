package de.uni_hannover.dbs.PostgreSQL.db.metadata;

import de.uni_hannover.dbs.PostgreSQL.db.DBConnection;
import de.uni_hannover.dbs.PostgreSQL.db.metadata.model.Function;
import de.uni_hannover.dbs.PostgreSQL.db.metadata.model.Schema;
import de.uni_hannover.dbs.PostgreSQL.db.metadata.model.Sequence;
import de.uni_hannover.dbs.PostgreSQL.db.metadata.model.Table;
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

    public static void populateMetadataForConnection(DBConnection _con) {
        try {
            ResultSet rs = _con.executeQuery("SELECT schema_name, schema_owner FROM information_schema.schemata WHERE schema_name NOT IN ('information_schema', 'pg_catalog')");

            while (rs.next()) {
                String schema_name = rs.getString("schema_name");
                String schema_owner = rs.getString("schema_owner");

                ArrayList<Sequence> sequences = getSequences(schema_name, _con);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Sequence> getSequences(String _schemaName, DBConnection _con) {
        ArrayList<Sequence> sequences = new ArrayList<>();

        try {
            ResultSet rs = _con.executeQuery("SELECT sequence_name, data_type, start_value, minimum_value, maximum_value, increment FROM information_schema.sequences WHERE sequence_schema = " + _schemaName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sequences;
    }

    private static ArrayList<Function> getFunctions(String _schemaName, DBConnection _con) {
        ArrayList<Function> functions = new ArrayList<>();

        try {
            ResultSet rs = _con.executeQuery("SELECT routine_name, data_type, routine_body, routine_definition FROM information_schema.routines");
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

            for(String table: tableNames) {
                ArrayList<Column> columns = new ArrayList<>();
                ArrayList<Trigger> triggers = new ArrayList<>();
                ArrayList<Index> indizes = new ArrayList<>();
                ArrayList<Constraint> constraints = new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tables;
    }
}
