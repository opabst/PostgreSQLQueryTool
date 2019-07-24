package de.uni_hannover.dbs.PostgreSQL.controller;

import de.uni_hannover.dbs.PostgreSQL.db.ConnectionStore;
import de.uni_hannover.dbs.PostgreSQL.model.DBConnection;
import de.uni_hannover.dbs.PostgreSQL.model.TreeViewRootItem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

//TODO: TreeItems für jede Art von Baumobjekt erstellen

/**
 * Created by pabst on 05.07.17.
 */
public class MainWindowController {

    @FXML
    private TreeView<TreeViewRootItem> DatabaseObjectOutline;

    @FXML
    private TextArea MainWindowQueryTA;

    @FXML
    private TableView MainWindowResultTV;

    @FXML
    private TextArea MainWindowExplainPlanTA;

    @FXML
    private MenuItem fileClose;

    @FXML
    private ComboBox<DBConnection> connectionCB;

    public MainWindowController() {

    }

    @FXML
    public void initialize() {
        TreeViewRootItem rootItem = new TreeViewRootItem("Verbindungen");

        rootItem.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler() {
            @Override
            public void handle(Event event) {
                System.out.println("Maus gemacht");
            }
        });

        DatabaseObjectOutline.setRoot(rootItem);

        connectionCB.setItems(ConnectionStore.getInstance().getConnections());


    }

    @FXML
    public void close() {
        // TODO: eventuell überdenken; vielleicht ist hier aufräumen erforderlich (Verbindung schließen, Quelltext speichern,...)
        Platform.exit();
    }

    @FXML
    public void openConnectionWindow(ActionEvent event) {
        Stage connectionWindow = new Stage();

        Parent connectionPane = null;
        try {
            connectionPane = FXMLLoader.load(getClass().getResource("/de/uni_hannover/dbs/PostgreSQL/views/ConnectionWindow.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(connectionPane);
        connectionWindow.setScene(scene);

        connectionWindow.initModality(Modality.APPLICATION_MODAL);
        connectionWindow.initOwner(((MenuItem)event.getTarget()).getParentPopup().getOwnerWindow());
        connectionWindow.showAndWait();
    }

}
