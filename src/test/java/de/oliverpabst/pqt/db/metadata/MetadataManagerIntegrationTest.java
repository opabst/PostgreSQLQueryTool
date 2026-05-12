package de.oliverpabst.pqt.db.metadata;

import de.oliverpabst.pqt.db.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
class MetadataManagerIntegrationTest {

    private static final String HOST = "localhost";
    private static final String PORT = "5440";
    private static final String DATABASE = "pqt_shop";
    private static final String USER = "pqt_admin";
    private static final String PASSWORD = "pqt_admin_pw";

    private DBConnection connection;

    @AfterEach
    void tearDown() {
        if (connection != null) {
            connection.disconnect();
        }
    }

    @Test
    void loadsInvAndCustSchemasAndMetadata() {
        Assumptions.assumeTrue(isDockerDbReachable(),
            "Docker PostgreSQL test database is not available on localhost:5440");

        connection = new DBConnection("integration-test", HOST, PORT, DATABASE, USER, PASSWORD);

        final MetadataManager metadataManager = new MetadataManager(connection);
        final List<String> schemas = metadataManager.getSchemaNames();

        Assumptions.assumeTrue(schemas.contains("inv") && schemas.contains("cust"),
            "Expected inv and cust schemas were not found in docker database");

        assertSchemaHasMetadata(metadataManager, "inv");
        assertSchemaHasMetadata(metadataManager, "cust");
    }

    private boolean isDockerDbReachable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(HOST, Integer.parseInt(PORT)), 500);
            return true;
        } catch (final Exception ignored) {
            return false;
        }
    }

    private void assertSchemaHasMetadata(final MetadataManager metadataManager, final String schemaName) {
        metadataManager.loadTablesForSchema(schemaName);
        metadataManager.loadSequencesForSchema(schemaName);
        metadataManager.loadFunctionsForSchema(schemaName);
        metadataManager.loadViewsForSchema(schemaName);

        assertTrue(!metadataManager.getTablesForSchema(schemaName).isEmpty(),
                () -> "Expected tables for schema: " + schemaName);
        assertTrue(!metadataManager.getSequencesForSchema(schemaName).isEmpty(),
                () -> "Expected sequences for schema: " + schemaName);
        assertTrue(!metadataManager.getFunctionsForSchema(schemaName).isEmpty(),
                () -> "Expected functions for schema: " + schemaName);

        // Views are optional in current migrations; ensure map access and load path work.
        metadataManager.getViewsForSchema(schemaName).size();
    }
}
