package de.uni_hannover.dbs.PostgreSQL.db.metadata.model;

import de.uni_hannover.dbs.PostgreSQL.db.metadata.model.table.Column;
import de.uni_hannover.dbs.PostgreSQL.db.metadata.model.table.Constraint;
import de.uni_hannover.dbs.PostgreSQL.db.metadata.model.table.Index;
import de.uni_hannover.dbs.PostgreSQL.db.metadata.model.table.Trigger;

import java.util.ArrayList;

public class Table extends DatabaseObject {
    private final ArrayList<Column> columns;
    private final ArrayList<Constraint> constraints;
    private final ArrayList<Index> indizes;
    private final ArrayList<de.uni_hannover.dbs.PostgreSQL.db.metadata.model.table.Trigger> triggers;

    public Table(String _objectName, String _owner, String _acl, ArrayList<Column> _columns, ArrayList<Constraint> _constraints,
                 ArrayList<Index> _indices, ArrayList<Trigger> _triggers) {
        super(_objectName, _owner, _acl);
        columns = _columns;
        constraints = _constraints;
        indizes = _indices;
        triggers = _triggers;
    }

    public Table(String _objectName, ArrayList<Column> _columns, ArrayList<Constraint> _constraints,
                 ArrayList<Index> _indices, ArrayList<Trigger> _triggers) {
        super(_objectName, "", "");
        columns = _columns;
        constraints = _constraints;
        indizes = _indices;
        triggers = _triggers;
    }

    public ArrayList<Column> getColumns() {
        return columns;
    }

    public ArrayList<Constraint> getConstraints() {
        return constraints;
    }

    public ArrayList<Index> getIndizes() {
        return indizes;
    }

    public ArrayList<Trigger> getTriggers() {
        return triggers;
    }

    public DatabaseObjectTypes getObjectType() {
        return DatabaseObjectTypes.TABLE;
    }
}