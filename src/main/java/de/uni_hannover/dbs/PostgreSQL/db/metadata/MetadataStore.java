package de.uni_hannover.dbs.PostgreSQL.db.metadata;

import de.uni_hannover.dbs.PostgreSQL.db.DBConnection;
import de.uni_hannover.dbs.PostgreSQL.db.metadata.model.Schema;

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

    }
}
