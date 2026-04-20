package de.oliverpabst.pqt.db.metadata.model.table;

public class Column extends TableObject{
    private final int columnNumber;
    private final String columnName;
    private final String dataType;
    private final boolean isNullable;

    public Column(int colNum, String colName, String dataType, boolean isNullable) {
        columnNumber = colNum;
        columnName = colName;
        this.dataType = dataType;
        this.isNullable = isNullable;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public boolean getIsNullable() {
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
