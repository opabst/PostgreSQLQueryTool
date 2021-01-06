package de.oliverpabst.pqt.db.metadata.model;

import de.oliverpabst.pqt.db.metadata.model.table.Column;
import de.oliverpabst.pqt.db.metadata.model.table.Constraint;
import de.oliverpabst.pqt.db.metadata.model.table.Index;
import de.oliverpabst.pqt.db.metadata.model.table.Trigger;

import java.util.ArrayList;
import java.util.List;

public class Table extends DatabaseObject {
    private final List<Column> columns;
    private final List<Constraint> constraints;
    private final List<Index> indices;
    private final List<Trigger> triggers;

    public Table(final String _objectName, final String _owner, final String _acl, final List<Column> _columns, final List<Constraint> _constraints,
                 final List<Index> _indices, final List<Trigger> _triggers) {
        super(_objectName, _owner, _acl);
        columns = _columns;
        constraints = _constraints;
        indices = _indices;
        triggers = _triggers;
    }

    public Table(final String _objectName, final List<Column> _columns, final List<Constraint> _constraints,
                 final List<Index> _indices, final List<Trigger> _triggers) {
        super(_objectName, "", "");
        columns = _columns;
        constraints = _constraints;
        indices = _indices;
        triggers = _triggers;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public List<Index> getIndices() {
        return indices;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public DatabaseObjectTypes getObjectType() {
        return DatabaseObjectTypes.TABLE;
    }
}
