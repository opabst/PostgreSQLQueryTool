package de.oliverpabst.pqt.db.metadata.model.table;

public class Index extends TableObject {
    private final String indexName;
    private final String indexType;
    private final String indexSize;

    public Index(String _indexName, String _indexType, String _indexSize) {
        indexName = _indexName;
        indexType = _indexType;
        indexSize = _indexSize;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getIndexType() {
        return indexType;
    }

    public String getIndexSize() {
        return indexSize;
    }

    @Override
    public String getTableObjectName() {
        return indexName;
    }

    @Override
    public TableObjectTypes getTableObjectType() {
        return TableObjectTypes.INDEX;
    }
}
