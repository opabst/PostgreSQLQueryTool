package de.oliverpabst.pqt.service;

import de.oliverpabst.pqt.db.DBConnection;
import de.oliverpabst.pqt.model.QueryResult;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX {@link Service} that executes a SQL query on a background thread and
 * produces a {@link QueryResult}. Supports plain execution as well as
 * {@code EXPLAIN} and {@code EXPLAIN ANALYZE} modes via {@link QueryMode}.
 */
public class QueryService extends Service<QueryResult> {

    public enum QueryMode { EXECUTE, EXPLAIN, EXPLAIN_ANALYZE }

    private DBConnection dbConnection;
    private String sqlText;
    private QueryMode mode = QueryMode.EXECUTE;

    public void setDbConnection(final DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void setSqlText(final String sqlText) {
        this.sqlText = sqlText;
    }

    public void setMode(final QueryMode mode) {
        this.mode = mode;
    }

    @Override
    protected Task<QueryResult> createTask() {
        final DBConnection con = dbConnection;
        final String sql = buildSql(sqlText, mode);

        return new Task<>() {
            @Override
            protected QueryResult call() throws Exception {
                return executeRaw(con, sql);
            }
        };
    }

    private String buildSql(final String raw, final QueryMode m) {
        final String normalized = raw.replace('\n', ' ').strip();
        return switch (m) {
            case EXPLAIN -> "EXPLAIN " + normalized;
            case EXPLAIN_ANALYZE -> "EXPLAIN ANALYZE " + normalized;
            default -> normalized;
        };
    }

    /**
     * Runs a raw SQL string via a plain {@link Statement} (needed because the
     * query is user-supplied and must not be parameterised). Closes the
     * Statement after consuming the ResultSet.
     */
    private QueryResult executeRaw(final DBConnection con, final String sql)
            throws SQLException {
        try (Statement stmt = con.rawStatement()) {
            try (ResultSet rs = stmt.executeQuery(sql)) {
                final ResultSetMetaData meta = rs.getMetaData();
                final int colCount = meta.getColumnCount();

                final List<String> columnNames = new ArrayList<>(colCount);
                for (int i = 1; i <= colCount; i++) {
                    columnNames.add(meta.getColumnName(i));
                }

                final List<List<String>> rows = new ArrayList<>();
                while (rs.next()) {
                    final List<String> row = new ArrayList<>(colCount);
                    for (int i = 1; i <= colCount; i++) {
                        final String value = rs.getString(i);
                        row.add(rs.wasNull() ? "" : value);
                    }
                    rows.add(row);
                }

                return new QueryResult(columnNames, rows);
            }
        }
    }
}
