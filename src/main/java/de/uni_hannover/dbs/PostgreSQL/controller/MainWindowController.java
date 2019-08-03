package de.uni_hannover.dbs.PostgreSQL.controller;

import de.uni_hannover.dbs.PostgreSQL.db.ConnectionStore;
import de.uni_hannover.dbs.PostgreSQL.db.DBConnection;
import de.uni_hannover.dbs.PostgreSQL.model.TreeViewRootItem;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.ArrayList;

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
    private ComboBox<DBConnection> connectionCB;

    @FXML
    private Button runQueryBTN;

    @FXML
    private TextArea errorMessagesTA;

    @FXML
    private TabPane queryResultTabPanel;

    @FXML
    private Tab resultTab;

    @FXML
    private Tab queryplanTab;

    @FXML
    private Tab errormessageTab;

    @FXML
    private Button explainBTN;

    @FXML
    private Button analyzeBTN;

    public MainWindowController() {

    }

    @FXML
    public void initialize() {
        TreeViewRootItem rootItem = new TreeViewRootItem("Verbindungen");

        //rootItem.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> System.out.println("Maus gemacht"));

        DatabaseObjectOutline.setRoot(rootItem);

        connectionCB.setItems(ConnectionStore.getInstance().getConnections());

        connectionCB.getItems().addListener((ListChangeListener<DBConnection>) c -> {
            while (c.next()) {
                if(c.wasAdded()) {
                    int cbItems = connectionCB.getItems().size();
                    connectionCB.getSelectionModel().select(cbItems-1);
                }
            }
        });

        connectionCB.getSelectionModel().select(0);

        runQueryBTN.setDisable(true);
        analyzeBTN.setDisable(true);
        explainBTN.setDisable(true);
        MainWindowQueryTA.setOnKeyTyped(event -> {
            if(MainWindowQueryTA.getText().length() == 0) {
                runQueryBTN.setDisable(true);
                explainBTN.setDisable(true);
                analyzeBTN.setDisable(true);
            } else {
                runQueryBTN.setDisable(false);
                explainBTN.setDisable(false);
                analyzeBTN.setDisable(false);
            }
        });


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

        ObservableList<ObservableList> tableData = FXCollections.observableArrayList();

        MainWindowResultTV.getColumns().removeAll(MainWindowResultTV.getColumns());

        String query = MainWindowQueryTA.getSelectedText();

        if(query.equals("")) {
            query = MainWindowQueryTA.getText();
        }

        query = query.replace("\n", " ");

        try {
            ResultSet result = con.executeQuery(query);

            ResultSetMetaData metaData = result.getMetaData();

            // Basierend auf https://blog.ngopal.com.np/2011/10/19/dyanmic-tableview-data-from-database/
            ArrayList<TableColumn> tabCols = new ArrayList<>();
            for(int i = 0; i < metaData.getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(metaData.getColumnName(i+1));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>)
                        param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                tabCols.add(col);
            }

            MainWindowResultTV.getColumns().setAll(tabCols);


            while (result.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i = 1; i <= metaData.getColumnCount(); i++) {
                    String value = result.getString(i);
                    // Auf NULL-Werte testen und gegebenenfalls durch leeren String ersetzen
                    if(result.wasNull()) {
                        value = "";
                    }
                    row.add(value);
                }

                tableData.add(row);
            }

            queryResultTabPanel.getSelectionModel().select(resultTab);
        } catch (SQLException e) {
            errorMessagesTA.setText(errorMessagesTA.getText() + "\n" + e.getMessage());
            queryResultTabPanel.getSelectionModel().select(errormessageTab);
        }

        MainWindowResultTV.setItems(tableData);

    }

    @FXML
    public void analyzeQuery() {
        DBConnection con = connectionCB.getValue();

        MainWindowExplainPlanTA.setText("");

        String queryText = MainWindowQueryTA.getSelectedText();
        if(queryText.equals("")) {
            queryText = MainWindowQueryTA.getText();
        }

        String query = "EXPLAIN ANALYZE " + queryText;
        query = query.replace("\n", " ");

        try {
            ResultSet result = con.executeQuery(query);

            while(result.next()) {
                String plan = result.getString(1);
                MainWindowExplainPlanTA.setText(MainWindowExplainPlanTA.getText() + "\n" + plan);

            }
            queryResultTabPanel.getSelectionModel().select(queryplanTab);
        } catch (SQLException e) {
            errorMessagesTA.setText(errorMessagesTA.getText() + "\n" + e.getMessage());
            queryResultTabPanel.getSelectionModel().select(errormessageTab);
        }
    }

    @FXML
    public void explainQuery() {
        DBConnection con = connectionCB.getValue();

        MainWindowExplainPlanTA.setText("");

        String queryText = MainWindowQueryTA.getSelectedText();
        if(queryText.equals("")) {
            queryText = MainWindowQueryTA.getText();
        }

        String query = "EXPLAIN " + queryText;
        query = query.replace("\n", " ");

        try {
            ResultSet result = con.executeQuery(query);

            while(result.next()) {
                String plan = result.getString(1);
                MainWindowExplainPlanTA.setText(MainWindowExplainPlanTA.getText() + "\n" + plan);
            }
            queryResultTabPanel.getSelectionModel().select(queryplanTab);
        } catch (SQLException e) {
            errorMessagesTA.setText(errorMessagesTA.getText() + "\n" + e.getMessage());
            queryResultTabPanel.getSelectionModel().select(errormessageTab);
        }
    }

}
