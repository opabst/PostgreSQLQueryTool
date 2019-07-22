package de.uni_hannover.dbs.PostgreSQL.model;

import de.uni_hannover.dbs.PostgreSQL.PostgresQueryTool;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class TreeViewRootItem extends TreeItem {

    private ContextMenu contextMenu;

    private MenuItem addConnection;

    public TreeViewRootItem(String itemText) {
        super(itemText);
        contextMenu = new ContextMenu();

        addConnection = new MenuItem("Verbindung hinzufügen");
        contextMenu.getItems().add(addConnection);

        addConnection.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Event behandelt");
                if(event.equals(MouseEvent.MOUSE_CLICKED)) {
                    System.out.println("Irgendwas an Maus gedrückt");
                }
            }
        });



        addConnection.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton().equals(MouseButton.SECONDARY)) {
                    System.out.println("Rechte Maus-Taste gedrückt");
                }

                event.consume();
            }
        });

        addConnection.setOnAction(new EventHandler() {
            @Override
            public void handle(Event e) {
                Pane connectionPane = null;
                try {
                    connectionPane = FXMLLoader.load(getClass().getResource("views/ConnectionWindow.fxml"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Stage stage = new Stage();
                Scene scene = new Scene(connectionPane);
                stage.initOwner(PostgresQueryTool.getPrimaryStage());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);

                stage.showAndWait();

                //TODO: modalen Dialog zum Erzeugen einer neuen Verbindung aufrufen
            }
        });
    }

}
