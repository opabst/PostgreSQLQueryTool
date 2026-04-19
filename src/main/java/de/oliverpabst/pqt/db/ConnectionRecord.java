package de.oliverpabst.pqt.db;

/**
 * Persistence DTO for a saved connection. Passwords are never persisted.
 */
public record ConnectionRecord(
        String connectionName,
        String hostName,
        String port,
        String databaseName,
        String userName
) {
    /** No-arg constructor required by Jackson for deserialization. */
    public ConnectionRecord() {
        this(null, null, null, null, null);
    }
}
