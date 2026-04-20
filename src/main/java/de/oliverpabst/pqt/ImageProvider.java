package de.oliverpabst.pqt;

import javafx.scene.image.Image;

public class ImageProvider {

    private static final int ICON_SIZE = 22;

    private Image appIcon;
    private Image constraintIcon;
    private Image functionIcon;
    private Image indexIcon;
    private Image schemaIcon;
    private Image sequenceIcon;
    private Image tableIcon;
    private Image viewIcon;

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

    private ImageProvider() {
        loadAllImages();
    }

    public static ImageProvider getInstance() {
        return instance;
    }

    private void loadAllImages() {
        appIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/pqt/icons/PQT.png"),
                ICON_SIZE, ICON_SIZE, true, false);

        constraintIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/pqt/icons/Constraint.png"),
                ICON_SIZE, ICON_SIZE, true, false);

        functionIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/pqt/icons/Function.png"),
                ICON_SIZE, ICON_SIZE, true, false);

        indexIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/pqt/icons/Index.png"),
                ICON_SIZE, ICON_SIZE, true, false);

        schemaIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/pqt/icons/Schema.png"),
                ICON_SIZE, ICON_SIZE, true, false);

        sequenceIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/pqt/icons/Sequence.png"),
                ICON_SIZE, ICON_SIZE, true, false);

        tableIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/pqt/icons/Table.png"),
                ICON_SIZE, ICON_SIZE, true, false);

        viewIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/pqt/icons/View.png"),
                ICON_SIZE, ICON_SIZE, true, false);
    }
}
