package de.oliverpabst.pqt;

import javafx.scene.image.Image;

public class ImageProvider {

    private static final int ICON_SIZE = 22;

    private final Image appIcon = loadIcon("PQT.png");
    private final Image constraintIcon = loadIcon("Constraint.png");
    private final Image functionIcon = loadIcon("Function.png");
    private final Image indexIcon = loadIcon("Index.png");
    private final Image schemaIcon = loadIcon("Schema.png");
    private final Image sequenceIcon = loadIcon("Sequence.png");
    private final Image tableIcon = loadIcon("Table.png");
    private final Image viewIcon = loadIcon("View.png");

    private static final ImageProvider instance = new ImageProvider();

    public Image getAppIcon() {
        return appIcon;
    }

    public Image getConstraintIcon() {
        return constraintIcon;
    }

    public Image getFunctionIcon() {
        return functionIcon;
    }

    public Image getIndexIcon() {
        return indexIcon;
    }

    public Image getSchemaIcon() {
        return schemaIcon;
    }

    public Image getSequenceIcon() {
        return sequenceIcon;
    }

    public Image getTableIcon() {
        return tableIcon;
    }

    public Image getViewIcon() {
        return viewIcon;
    }

    private ImageProvider() { }

    public static ImageProvider getInstance() {
        return instance;
    }

        private Image loadIcon(final String fileName) {
        return new Image(
            getClass().getClassLoader().getResourceAsStream("de/oliverpabst/pqt/icons/" + fileName),
            ICON_SIZE, ICON_SIZE, true, false);
    }
}
