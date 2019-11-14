package de.oliverpabst.PQT.model;

import de.oliverpabst.PQT.db.metadata.MetadataManager;
import de.oliverpabst.PQT.db.metadata.model.Function;
import de.oliverpabst.PQT.db.metadata.model.Schema;
import de.oliverpabst.PQT.db.metadata.model.Table;
import de.oliverpabst.PQT.db.metadata.model.View;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

// TODO: TreeItems mit Icons versehen
// TODO: alle OutlineObjects alphabetisch aufsteigend sortieren

public class DBOutlineTreeItem extends TreeItem<String> {
    private final String schemaCompName;

    private OutlineComponentType compType;

    private MetadataManager metadataManager;

    private boolean hasLoadedChildren = false;

    private boolean hasBeenTested = false;

    public DBOutlineTreeItem(String _name, OutlineComponentType _compType, MetadataManager _mm) {
        super(_name);
        schemaCompName = _name;
        compType = _compType;
        metadataManager = _mm;

    }

    @Override
    public ObservableList<TreeItem<String>> getChildren() {
        if(hasLoadedChildren && !isExpanded()) {
            return super.getChildren();
        } else if (hasLoadedChildren == false) {
            loadChildren();
        }
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        // Der Wurzelknoten und ein Schemaknoten sind nie ein Blatt
        if(compType == OutlineComponentType.ROOT || compType == OutlineComponentType.SCHEMA) {
            return false;
        } else {
            if(!hasLoadedChildren) {
                loadChildren();
            }
            return getChildren().isEmpty();
        }
        /*if(this.getComponentType() != OutlineComponentType.DB_OBJECT && !hasLoadedChildren) {
            // Interne Repräsentanten sind intial nie Blätter
            if(hasLoadedChildren == false) {
                loadChildren();
            }
            if(super.getChildren().isEmpty()) {
                return true;
            } else {
                return false;
            }
        } else {
            return super.getChildren().isEmpty();
        }*/


        /*if(hasLoadedChildren == false) {
            loadChildren();
        }
        return super.getChildren().isEmpty();*/
    }

    private void loadChildren() {

        hasLoadedChildren = true;

        ResourceBundle resBundle = ResourceBundle.getBundle("de.oliverpabst.PQT.lang_properties.guistrings");


        ArrayList<DBOutlineTreeItem> children = new ArrayList<>();

        DBOutlineTreeItem parent = (DBOutlineTreeItem) this;
        if(this.getComponentType() == OutlineComponentType.ROOT) {
            // Elemente der obersten Hierarchie auf Datenbankebene laden

            // Schemata laden
            ArrayList<String> schemaNames = metadataManager.getSchemaNames();

            for(String s: schemaNames) {
                DBOutlineTreeItem item = new DBOutlineTreeItem(s, OutlineComponentType.SCHEMA, metadataManager);
                children.add(item);
            }
        } else if (parent.getComponentType() == OutlineComponentType.SCHEMA) {
            DBOutlineTreeItem table = new DBOutlineTreeItem(resBundle.getString("tree_view_tables"), OutlineComponentType.TABLE, metadataManager);
            children.add(table);
            DBOutlineTreeItem view = new DBOutlineTreeItem(resBundle.getString("tree_view_views"), OutlineComponentType.VIEW, metadataManager);
            children.add(view);
            DBOutlineTreeItem function = new DBOutlineTreeItem(resBundle.getString("tree_view_functions"), OutlineComponentType.FUNCTION, metadataManager);
            children.add(function);

            // TODO: fehlende Schemateile laden
        } else if (parent.getComponentType() == OutlineComponentType.TABLE) {
            String schemaName = parent.getParent().getValue();
            metadataManager.loadTablesForSchema(schemaName); // TODO: Reparieren -> sollte nicht manuell geladen werden
            HashMap<String, Table> tables = metadataManager.getTablesForSchema(schemaName);

            for(String s: tables.keySet()) {
                DBOutlineTreeItem item = new DBOutlineTreeItem(s, OutlineComponentType.DB_OBJECT, metadataManager);
                children.add(item);
            }
        } else if (parent.getComponentType() == OutlineComponentType.FUNCTION) {
            String schemaName = parent.getParent().getValue();
            metadataManager.loadFunctionsForSchema(schemaName); // TODO: Reparieren -> sollte nicht manuell geladen werden
            HashMap<String, Function> functions = metadataManager.getFunctionsForSchema(schemaName);

            for(String s: functions.keySet()) {
                DBOutlineTreeItem item = new DBOutlineTreeItem(s, OutlineComponentType.DB_OBJECT, metadataManager);
                children.add(item);
            }
        } else if (parent.getComponentType() == OutlineComponentType.VIEW) {
            String schemaName = parent.getParent().getValue();
            metadataManager.loadViewsForSchema(schemaName); // TODO: Reparieren -> sollte nicht manuell geladen werden
            HashMap<String, View> views = metadataManager.getViewsForSchema(schemaName);

            for(String s: views.keySet()) {
                DBOutlineTreeItem item = new DBOutlineTreeItem(s, OutlineComponentType.DB_OBJECT, metadataManager);
                children.add(item);
            }
        }

        super.getChildren().setAll(children);
    }

    public String toString() {
        return schemaCompName;
    }

    public String getSchemaCompName() {
        return schemaCompName;
    }

    public OutlineComponentType getComponentType() {
        return compType;
    }
}
