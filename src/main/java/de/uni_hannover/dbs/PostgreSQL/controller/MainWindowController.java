package de.uni_hannover.dbs.PostgreSQL.controller;

import de.uni_hannover.dbs.PostgreSQL.db.ConnectionStore;
import de.uni_hannover.dbs.PostgreSQL.db.DBConnection;
import de.uni_hannover.dbs.PostgreSQL.model.TreeViewRootItem;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

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

    @FXML
    private Button runQueryBTN;

    @FXML
    private TextArea errorMessagesTA;

    private ObservableList<ObservableList> tableData;

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

        connectionCB.getSelectionModel().select(0);


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

    @FXML
    public void runQuery() {
        DBConnection con = connectionCB.getValue();

        MainWindowResultTV.getColumns().removeAll();

        tableData = FXCollections.observableArrayList();

        ResultSet result = null;
        try {
            result = con.executeQuery(MainWindowQueryTA.getText());
        } catch (SQLException e) {
            errorMessagesTA.setText(errorMessagesTA.getText() + "n" + e.getMessage());
        }

        // Basierend auf https://blog.ngopal.com.np/2011/10/19/dyanmic-tableview-data-from-database/

        ResultSetMetaData metaData = null;
        try {
            metaData = result.getMetaData();

            // Aus Metadaten Ergebnistabelle konstruieren -> HEADER
            for(int i = 0; i < metaData.getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(metaData.getColumnName(i+1));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>)
                        param -> new SimpleStringProperty(param.getValue().get(j).toString()));

                MainWindowResultTV.getColumns().addAll(col);
            }

        } catch (SQLException e) {
            errorMessagesTA.setText(errorMessagesTA.getText() + "n" + e.getMessage());
        }

        try {
            int columnCount = metaData.getColumnCount();
            while (result.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i = 1; i <= columnCount; i++) {

                    // Auf NULL-Werte testen und gegebenenfalls durch leeren String ersetzen
                    String tmp = result.getString(i);
                    if(result.wasNull()) {
                        row.add("");
                    } else {
                        row.add(result.getString(i));
                    }
                }
                tableData.add(row);
            }
        } catch (SQLException e) {
            errorMessagesTA.setText(errorMessagesTA.getText() + "n" + e.getMessage());
        }

        MainWindowResultTV.setItems(tableData);

    }

}
