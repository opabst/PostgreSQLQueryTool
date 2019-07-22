package de.uni_hannover.dbs.PostgreSQL.controller;

import com.sun.glass.ui.Application;
import de.uni_hannover.dbs.PostgreSQL.model.TreeViewRootItem;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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


    }

}
