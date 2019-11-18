package de.oliverpabst.PQT;

import javafx.scene.image.Image;

public class ImageProvider {

    private Image tableImage;

    private static ImageProvider instance;

    public Image getTableImage() {
        return tableImage;
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
        tableImage = new Image(getClass().getClassLoader().getResourceAsStream("de/oliverpabst/PQT/icons/Table.png"),
                22, 22, true, false);
    }
}
