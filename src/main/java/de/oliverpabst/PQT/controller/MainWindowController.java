package de.oliverpabst.PQT.controller;

import de.oliverpabst.PQT.model.DBOutlineTreeItem;
import de.oliverpabst.PQT.db.DBConnection;
import de.oliverpabst.PQT.db.metadata.MetadataStore;
import de.oliverpabst.PQT.db.metadata.model.Schema;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;



public class MainWindowController {

    @FXML
    private TreeView DatabaseObjectOutline;

    @FXML
    private TextArea MainWindowQueryTA;

    @FXML
    private TableView MainWindowResultTV;

    @FXML
    private TextArea MainWindowExplainPlanTA;

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

    @FXML
    private Menu fileMENU;

    @FXML
    private MenuItem fileCloseITM;

    @FXML
    private Menu editMENU;

    @FXML
    private MenuItem editDeleteITM;

    @FXML
    private Menu helpMENU;

    @FXML
    private MenuItem helpAboutITM;

    private DBConnection dbConnection;

    private MetadataStore metadata;

    public MainWindowController() {

    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            ResourceBundle resBundle = ResourceBundle.getBundle("de.oliverpabst.PQT.lang_properties.guistrings");

            resultTab.setText(resBundle.getString("result_tab"));
            queryplanTab.setText(resBundle.getString("query_plan_tab"));
            errormessageTab.setText(resBundle.getString("error_messages_tab"));

            runQueryBTN.setText(resBundle.getString("query_execute_button"));
            explainBTN.setText(resBundle.getString("query_explain_button"));
            analyzeBTN.setText(resBundle.getString("query_analyze_button"));

            fileMENU.setText(resBundle.getString("menu_file_submenu"));
            fileCloseITM.setText(resBundle.getString("menu_file_submenu_close"));

            editMENU.setText(resBundle.getString("menu_edit_submenu"));
            editDeleteITM.setText(resBundle.getString("menu_edit_submenu_delete"));

            helpMENU.setText(resBundle.getString("menu_help_submenu"));
            helpAboutITM.setText(resBundle.getString("menu_help_submenu_about"));

            runQueryBTN.setDisable(true);
            analyzeBTN.setDisable(true);
            explainBTN.setDisable(true);
            MainWindowQueryTA.setOnKeyTyped(event -> {
                if (MainWindowQueryTA.getText().length() == 0) {
                    runQueryBTN.setDisable(true);
                    explainBTN.setDisable(true);
                    analyzeBTN.setDisable(true);
                } else {
                    runQueryBTN.setDisable(false);
                    explainBTN.setDisable(false);
                    analyzeBTN.setDisable(false);
                }
            });


            metadata = new MetadataStore(dbConnection);
            metadata.populateMetadataForConnection();

            //DatabaseObjectOutline.setShowRoot(false);

            populateTreeView();
        });
    }

    @FXML
    public void close() {
        //Platform.exit();
    }

    @FXML
    public void runQuery() {
        ObservableList<ObservableList> tableData = FXCollections.observableArrayList();

        MainWindowResultTV.getColumns().removeAll(MainWindowResultTV.getColumns());

        String query = MainWindowQueryTA.getSelectedText();

        if (query.equals("")) {
            query = MainWindowQueryTA.getText();
        }

        query = query.replace("\n", " ");

        try {
            ResultSet result = dbConnection.executeQuery(query);

            ResultSetMetaData metaData = result.getMetaData();

            // Basierend auf https://blog.ngopal.com.np/2011/10/19/dyanmic-tableview-data-from-database/
            ArrayList<TableColumn> tabCols = new ArrayList<>();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(metaData.getColumnName(i + 1));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>)
                        param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                tabCols.add(col);
            }

            MainWindowResultTV.getColumns().setAll(tabCols);


            while (result.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String value = result.getString(i);
                    // Auf NULL-Werte testen und gegebenenfalls durch leeren String ersetzen
                    if (result.wasNull()) {
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
        MainWindowExplainPlanTA.setText("");

        String queryText = MainWindowQueryTA.getSelectedText();
        if (queryText.equals("")) {
            queryText = MainWindowQueryTA.getText();
        }

        String query = "EXPLAIN ANALYZE " + queryText;
        query = query.replace("\n", " ");

        try {
            ResultSet result = dbConnection.executeQuery(query);

            while (result.next()) {
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
        MainWindowExplainPlanTA.setText("");

        String queryText = MainWindowQueryTA.getSelectedText();
        if (queryText.equals("")) {
            queryText = MainWindowQueryTA.getText();
        }

        String query = "EXPLAIN " + queryText;
        query = query.replace("\n", " ");

        try {
            ResultSet result = dbConnection.executeQuery(query);

            while (result.next()) {
                String plan = result.getString(1);
                MainWindowExplainPlanTA.setText(MainWindowExplainPlanTA.getText() + "\n" + plan);
            }
            queryResultTabPanel.getSelectionModel().select(queryplanTab);
        } catch (SQLException e) {
            errorMessagesTA.setText(errorMessagesTA.getText() + "\n" + e.getMessage());
            queryResultTabPanel.getSelectionModel().select(errormessageTab);
        }
    }

    public void setDbConnection(DBConnection _con) {
        dbConnection = _con;
    }

    private void populateTreeView() {
        ResourceBundle resBundle = ResourceBundle.getBundle("de.oliverpabst.PQT.lang_properties.guistrings");
        DBOutlineTreeItem rootItem = new DBOutlineTreeItem(resBundle.getString("tree_view_root"), null);
        rootItem.setExpanded(true);
        DatabaseObjectOutline.setRoot(rootItem);

        for(Schema schema: metadata.getAllSchemas()) {
            DBOutlineTreeItem schemaItem = new DBOutlineTreeItem(schema.getObjectName(), schema);
            DatabaseObjectOutline.getRoot().getChildren().add(schemaItem);
            DatabaseObjectOutline.refresh();
        }

    }

}
