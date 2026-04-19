package de.oliverpabst.pqt.controller;

import de.oliverpabst.pqt.ImageProvider;
import de.oliverpabst.pqt.viewmodel.MainViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindowController {

    @FXML private TreeView<String> DatabaseObjectOutline;
    @FXML private TextArea MainWindowQueryTA;
    @FXML private TableView<ObservableList<String>> MainWindowResultTV;
    @FXML private TextArea MainWindowExplainPlanTA;
    @FXML private Button runQueryBTN;
    @FXML private TextArea errorMessagesTA;
    @FXML private TabPane queryResultTabPanel;
    @FXML private Tab resultTab;
    @FXML private Tab queryplanTab;
    @FXML private Tab errormessageTab;
    @FXML private Button explainBTN;
    @FXML private Button analyzeBTN;
    @FXML private Menu fileMENU;
    @FXML private MenuItem fileSettingsITM;
    @FXML private MenuItem fileCloseITM;
    @FXML private Menu editMENU;
    @FXML private MenuItem editDeleteITM;
    @FXML private Menu helpMENU;
    @FXML private MenuItem helpAboutITM;
    @FXML private MenuBar mainMenuBar;

    private MainViewModel viewModel;

    @FXML
    public void initialize() { }

    public void setViewModel(final MainViewModel vm) {
        this.viewModel = vm;

        final var resBundle = java.util.ResourceBundle.getBundle(
                "de.oliverpabst.PQT.lang_properties.guistrings");

        resultTab.setText(resBundle.getString("result_tab"));
        queryplanTab.setText(resBundle.getString("query_plan_tab"));
        errormessageTab.setText(resBundle.getString("error_messages_tab"));

        runQueryBTN.setText(resBundle.getString("query_execute_button"));
        explainBTN.setText(resBundle.getString("query_explain_button"));
        analyzeBTN.setText(resBundle.getString("query_analyze_button"));

        fileMENU.setText(resBundle.getString("menu_file_submenu"));
        fileSettingsITM.setText(resBundle.getString("menu_file_submenu_settings"));
        fileCloseITM.setText(resBundle.getString("menu_file_submenu_close"));
        editMENU.setText(resBundle.getString("menu_edit_submenu"));
        editDeleteITM.setText(resBundle.getString("menu_edit_submenu_delete"));
        helpMENU.setText(resBundle.getString("menu_help_submenu"));
        helpAboutITM.setText(resBundle.getString("menu_help_submenu_about"));

        MainWindowQueryTA.textProperty().bindBidirectional(vm.queryTextProperty());

        runQueryBTN.disableProperty().bind(vm.queryEmptyBinding);
        explainBTN.disableProperty().bind(vm.queryEmptyBinding);
        analyzeBTN.disableProperty().bind(vm.queryEmptyBinding);

        MainWindowExplainPlanTA.textProperty().bind(vm.explainTextProperty());
        errorMessagesTA.textProperty().bind(vm.errorTextProperty());

        vm.selectedTabProperty().addListener((obs, oldVal, newVal) ->
                queryResultTabPanel.getSelectionModel().select(newVal.intValue()));

        vm.getResultColumnNames().addListener(
                (javafx.collections.ListChangeListener<String>) change -> rebuildColumns());

        MainWindowResultTV.setItems(vm.getResultRows());

        DatabaseObjectOutline.setRoot(vm.treeRootProperty().get());
        DatabaseObjectOutline.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML public void runQuery()     { viewModel.runQuery(); }
    @FXML public void explainQuery() { viewModel.explainQuery(); }
    @FXML public void analyzeQuery() { viewModel.analyzeQuery(); }
    @FXML public void close()        { }

    @FXML
    public void openAboutScreen(final ActionEvent event) {
        openModal("de/oliverpabst/PQT/views/About.fxml");
    }

    @FXML
    public void openSettings(final ActionEvent event) {
        openModal("de/oliverpabst/PQT/views/Settings.fxml");
    }

    private void openModal(final String fxmlPath) {
        try {
            final FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader().getResource(fxmlPath));
            final Parent pane = loader.load();
            final Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.getIcons().add(ImageProvider.getInstance().getAppIcon());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(mainMenuBar.getScene().getWindow());
            stage.showAndWait();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void rebuildColumns() {
        MainWindowResultTV.getColumns().clear();
        final java.util.List<String> names = viewModel.getResultColumnNames();
        for (int i = 0; i < names.size(); i++) {
            final int colIndex = i;
            final TableColumn<ObservableList<String>, String> col =
                    new TableColumn<>(names.get(colIndex));
            col.setCellValueFactory(param ->
                    new SimpleStringProperty(param.getValue().get(colIndex)));
            MainWindowResultTV.getColumns().add(col);
        }
    }
}
