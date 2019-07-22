package de.uni_hannover.dbs.PostgreSQL.model;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class RootTreeCell extends TreeCell<String> {
    private Label label;

    public RootTreeCell(String rootLabelText) {
        label = new Label(rootLabelText);

        label.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton().equals(MouseButton.PRIMARY)) {
                    System.out.println("Linke Taste gedrückt");
                } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                    System.out.println("Rechte Taste gedrückt");
                }
            }
        });
    }
}
