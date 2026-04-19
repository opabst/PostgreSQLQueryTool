package de.oliverpabst.pqt.model;

import java.util.List;

/**
 * Immutable result of a SQL query execution.
 *
 * @param columnNames ordered list of column names from the ResultSetMetaData
 * @param rows        each row is an ordered list of cell values (nulls replaced with empty string)
 */
public record QueryResult(List<String> columnNames, List<List<String>> rows) {
}
