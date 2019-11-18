package de.oliverpabst.PQT.model;

import de.oliverpabst.PQT.ImageProvider;
import de.oliverpabst.PQT.db.metadata.MetadataManager;
import de.oliverpabst.PQT.db.metadata.model.View;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Comparator;
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

    public DBOutlineTreeItem(String _name, OutlineComponentType _compType, MetadataManager _mm, Node _image) {
        super(_name, _image);
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
            DBOutlineTreeItem table = new DBOutlineTreeItem(resBundle.getString("tree_view_tables"), OutlineComponentType.TABLE,
                    metadataManager, new ImageView(ImageProvider.getInstance().getTableImage()));
            children.add(table);

            DBOutlineTreeItem view = new DBOutlineTreeItem(resBundle.getString("tree_view_views"), OutlineComponentType.VIEW, metadataManager);
            children.add(view);

            DBOutlineTreeItem function = new DBOutlineTreeItem(resBundle.getString("tree_view_functions"), OutlineComponentType.FUNCTION, metadataManager);
            children.add(function);

            DBOutlineTreeItem sequence = new DBOutlineTreeItem(resBundle.getString("tree_view_sequences"), OutlineComponentType.SEQUENCE, metadataManager);
            children.add(sequence);

        } else if (parent.getComponentType() == OutlineComponentType.TABLE) {
            String schemaName = parent.getParent().getValue();
            metadataManager.loadTablesForSchema(schemaName); // TODO: Reparieren -> sollte nicht manuell geladen werden

            ArrayList<String> tableNames = new ArrayList<>(metadataManager.getTablesForSchema(schemaName).keySet());
            Comparator<String> c = Comparator.comparing((String x) -> x);
            tableNames.sort(c);

            for(String s: tableNames) {
                DBOutlineTreeItem item = new DBOutlineTreeItem(s, OutlineComponentType.TABLE_OBJECT, metadataManager);
                children.add(item);
            }
        } else if (parent.getComponentType() == OutlineComponentType.FUNCTION) {
            String schemaName = parent.getParent().getValue();
            metadataManager.loadFunctionsForSchema(schemaName); // TODO: Reparieren -> sollte nicht manuell geladen werden

            ArrayList<String> functionNames = new ArrayList<>(metadataManager.getFunctionsForSchema(schemaName).keySet());
            Comparator<String> c = Comparator.comparing((String x) -> x);
            functionNames.sort(c);

            for(String s: functionNames) {
                DBOutlineTreeItem item = new DBOutlineTreeItem(s, OutlineComponentType.DB_OBJECT, metadataManager);
                children.add(item);
            }
        } else if (parent.getComponentType() == OutlineComponentType.VIEW) {
            String schemaName = parent.getParent().getValue();
            metadataManager.loadViewsForSchema(schemaName); // TODO: Reparieren -> sollte nicht manuell geladen werden
            HashMap<String, View> views = metadataManager.getViewsForSchema(schemaName);

            ArrayList<String> viewNames = new ArrayList<>(metadataManager.getViewsForSchema(schemaName).keySet());
            Comparator<String> c = Comparator.comparing((String x) -> x);
            viewNames.sort(c);

            for(String s: viewNames) {
                DBOutlineTreeItem item = new DBOutlineTreeItem(s, OutlineComponentType.DB_OBJECT, metadataManager);
                children.add(item);
            }
        } else if (parent.getComponentType() == OutlineComponentType.SEQUENCE) {
            String schemaName = parent.getParent().getValue();
            metadataManager.loadSequencesForSchema(schemaName);

            ArrayList<String> sequenceNames = new ArrayList<>(metadataManager.getSequencesForSchema(schemaName).keySet());
            Comparator<String> c = Comparator.comparing((String x) -> x);
            sequenceNames.sort(c);

            for(String s: sequenceNames) {
                DBOutlineTreeItem item = new DBOutlineTreeItem(s, OutlineComponentType.DB_OBJECT, metadataManager);
                children.add(item);
            }
        } else if (parent.getComponentType() == OutlineComponentType.TABLE_OBJECT) {
            // Spalten
            DBOutlineTreeItem columns = new DBOutlineTreeItem(resBundle.getString("tree_view_table_columns"),
                    OutlineComponentType.TABLE_COLUMN, metadataManager);
            children.add(columns);
            // Constraints
            DBOutlineTreeItem constraints = new DBOutlineTreeItem(resBundle.getString("tree_view_table_constraints"),
                    OutlineComponentType.TABLE_CONSTRAINT, metadataManager);
            children.add(constraints);
            // Indices
            DBOutlineTreeItem indices = new DBOutlineTreeItem(resBundle.getString("tree_view_table_indices"),
                    OutlineComponentType.TABLE_INDEX, metadataManager);
            children.add(indices);
            // Triggers
            DBOutlineTreeItem triggers = new DBOutlineTreeItem(resBundle.getString("tree_view_table_triggers"),
                    OutlineComponentType.TABLE_TRIGGER, metadataManager);
            children.add(triggers);

        }// Tabelle und Einzelteile der Tabelle laden

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
