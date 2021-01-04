package de.oliverpabst.pqt.model;

import de.oliverpabst.pqt.db.metadata.model.Schema;

import java.util.Comparator;

public class SchemaComparator implements Comparator<Schema> {
    @Override
    public int compare(Schema s1, Schema s2) {
        return s1.getObjectName().compareToIgnoreCase(s2.getObjectName());
    }
}
