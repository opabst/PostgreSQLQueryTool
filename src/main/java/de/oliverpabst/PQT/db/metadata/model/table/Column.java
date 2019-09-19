package de.oliverpabst.PQT.db.metadata.model.table;

public class Column extends TableObject{
    private final Integer columnNumber;
    private final String columnName;
    private final String dataType;
    private final Boolean isNullable;

    public Column(Integer _colNum, String _colName, String _dataType, Boolean _isNullable) {
        columnNumber = _colNum;
        columnName = _colName;
        dataType = _dataType;
        isNullable = _isNullable;
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

    public Boolean getIsNullable() {
        return isNullable;
    }

    @Override
    public String getTableObjectName() {
        return columnName;
    }

    @Override
    public TableObjectTypes getTableObjectType() {
        return TableObjectTypes.COLUMN;
    }
}
