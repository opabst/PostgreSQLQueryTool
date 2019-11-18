package de.oliverpabst.PQT;

import javafx.scene.image.Image;

public class ImageProvider {

    private Image appIcon;
    private Image constraintIcon;
    private Image functionIcon;
    private Image indexIcon;
    private Image schemaIcon;
    private Image sequenceIcon;
    private Image tableIcon;
    private Image viewIcon;

    private static ImageProvider instance;

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
        if(instance == null) {
            instance = new ImageProvider();
        }
        return instance;
    }

    private void loadAllImages() {
        appIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/PQT/icons/PQT.png"),
                22, 22, true, false);

        constraintIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/PQT/icons/Constraint.png"),
                22, 22, true, false);

        functionIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/PQT/icons/Function.png"),
                22, 22, true, false);

        indexIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/PQT/icons/Index.png"),
                22, 22, true, false);

        schemaIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/PQT/icons/Schema.png"),
                22, 22, true, false);

        sequenceIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/PQT/icons/Sequence.png"),
                22, 22, true, false);

        tableIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/PQT/icons/Table.png"),
                22, 22, true, false);

        viewIcon = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/PQT/icons/View.png"),
                22, 22, true, false);
    }
}
