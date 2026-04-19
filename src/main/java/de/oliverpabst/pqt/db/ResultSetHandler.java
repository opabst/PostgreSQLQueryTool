package de.oliverpabst.pqt.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Callback for consuming a {@link ResultSet} and producing a result.
 * The ResultSet (and its Statement) will be closed by the caller after this
 * method returns, so the implementation must not hold references to the ResultSet.
 */
@FunctionalInterface
public interface ResultSetHandler<T> {
    T handle(ResultSet rs) throws SQLException;
}
