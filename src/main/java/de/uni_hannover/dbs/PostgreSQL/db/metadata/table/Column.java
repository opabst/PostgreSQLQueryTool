package de.uni_hannover.dbs.PostgreSQL.db.metadata.table;

public class Column {
    private final Integer columnNumber;
    private final String columnName;
    private final String dataType;
    private final Boolean isPrimaryKey;
    private final Boolean isForeignKey;

    public Column(Integer _colNum, String _colName, String _dataType, Boolean _isPrimaryKey, Boolean _isForeignKey) {
        columnNumber = _colNum;
        columnName = _colName;
        dataType = _dataType;
        isPrimaryKey = _isPrimaryKey;
        isForeignKey = _isForeignKey;
    }

    public Integer getColumnNumber() {
        return columnNumber;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public Boolean getPrimaryKey() {
        return isPrimaryKey;
    }

    public Boolean getForeignKey() {
        return isForeignKey;
    }
}
