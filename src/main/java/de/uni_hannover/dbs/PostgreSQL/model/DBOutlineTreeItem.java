package de.uni_hannover.dbs.PostgreSQL.model;

import javafx.scene.control.TreeItem;

public class DBOutlineTreeItem extends TreeItem<String> {
    private final String name;
    private final TreeItemType type;

    public DBOutlineTreeItem(String _name, TreeItemType _type) {
        super(_name);
        name = _name;
        type = _type;


    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" - ");
        sb.append(type.toString());
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public TreeItemType getItemType() {
        return getItemType();
    }
}
