package de.uni_hannover.dbs.PostgreSQL.db.metadata.table;

public class Index {
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
}
