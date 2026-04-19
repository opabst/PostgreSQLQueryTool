package de.oliverpabst.pqt.controller;

import de.oliverpabst.pqt.db.ConnectionStore;
import de.oliverpabst.pqt.db.DBConnection;
import de.oliverpabst.pqt.viewmodel.WelcomeViewModel;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class WelcomeScreenController {

    @FXML private Accordion connectionAccordion;
    @FXML private TitledPane connectionsTitledPane;
    @FXML private Button addConnection;

    private WelcomeViewModel viewModel;

    public WelcomeScreenController() { }

    @FXML
    public void initialize() { }

    public void setViewModel(final WelcomeViewModel vm) {
        this.viewModel = vm;

        final ResourceBundle resBundle = ResourceBundle.getBundle(
                "de.oliverpabst.PQT.lang_properties.guistrings");

        addConnection.setText(resBundle.getString("connection_add_connection"));
        connectionsTitledPane.setText(resBundle.getString("welcome_connections_title"));

        vm.getConnections().addListener((ListChangeListener<DBConnection>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (final DBConnection con : c.getAddedSubList()) {
                        addConnectionTitledPane(con);
                    }
                } else if (c.wasRemoved()) {
                    final ArrayList<TitledPane> toRemove = new ArrayList<>();
                    for (final DBConnection con : c.getRemoved()) {
                        for (final TitledPane pane : connectionAccordion.getPanes()) {
                            if (pane.getText().equals(con.getConnectionName())) {
                                toRemove.add(pane);
                            }
                        }
                    }
                    connectionAccordion.getPanes().removeAll(toRemove);
                }
            }
        });

        // Populate accordion with connections that were loaded at startup
        for (final DBConnection con : vm.getConnections()) {
            addConnectionTitledPane(con);
        }
    }

    public void setStage(final Stage stage) {
        if (viewModel != null) {
            viewModel.setPrimaryStage(stage);
        }
    }

    private void addConnectionTitledPane(final DBConnection con) {
        final ResourceBundle resBundle = ResourceBundle.getBundle(
                "de.oliverpabst.PQT.lang_properties.guistrings");

        final GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setHgap(4);
        grid.getColumnConstraints().add(new ColumnConstraints(150));
        grid.getColumnConstraints().add(new ColumnConstraints(175));
        grid.getColumnConstraints().add(new ColumnConstraints(75));
        grid.getColumnConstraints().add(new ColumnConstraints(150));
        grid.setPadding(new Insets(5, 5, 5, 5));

        // Row 0 — connection name
        final Label connectionNameLabel = new Label(resBundle.getString("connection_name_label"));
        grid.add(connectionNameLabel, 0, 0);
        GridPane.setHalignment(connectionNameLabel, HPos.RIGHT);
        final TextField connectionnameTF = new TextField(con.getConnectionName());
        connectionnameTF.setDisable(true);
        grid.add(connectionnameTF, 1, 0);

        // Row 1 — host / port
        final Label hostnameLabel = new Label(resBundle.getString("connection_hostname_label"));
        grid.add(hostnameLabel, 0, 1);
        GridPane.setHalignment(hostnameLabel, HPos.RIGHT);
        final TextField hostnameTF = new TextField(con.getHostName());
        hostnameTF.setDisable(true);
        grid.add(hostnameTF, 1, 1);

        final Label portLabel = new Label(resBundle.getString("connection_port_label"));
        grid.add(portLabel, 2, 1);
        GridPane.setHalignment(portLabel, HPos.RIGHT);
        final TextField portTF = new TextField(con.getPort());
        portTF.setDisable(true);
        grid.add(portTF, 3, 1);

        // Row 2 — username / password prompt
        final Label usernameLabel = new Label(resBundle.getString("connection_username_label"));
        grid.add(usernameLabel, 0, 2);
        GridPane.setHalignment(usernameLabel, HPos.RIGHT);
        final TextField usernameTF = new TextField(con.getUserName());
        usernameTF.setDisable(true);
        grid.add(usernameTF, 1, 2);

        final Label passwordLabel = new Label(resBundle.getString("connection_password_label"));
        grid.add(passwordLabel, 2, 2);
        GridPane.setHalignment(passwordLabel, HPos.RIGHT);
        final PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(resBundle.getString("connection_password_helptext"));
        grid.add(passwordField, 3, 2);

        // Row 3 — database name
        final Label dbnameLabel = new Label(resBundle.getString("connection_databasename_label"));
        grid.add(dbnameLabel, 0, 3);
        GridPane.setHalignment(dbnameLabel, HPos.RIGHT);
        final TextField dbnameTF = new TextField(con.getDatabaseName());
        dbnameTF.setDisable(true);
        grid.add(dbnameTF, 1, 3);

        // Buttons
        final Button deleteButton = new Button(resBundle.getString("connection_delete"));
        deleteButton.setMinSize(100, 28);
        deleteButton.setPrefSize(100, 28);
        deleteButton.setOnAction((ActionEvent e) -> {
            final TitledPane pane = connectionAccordion.getExpandedPane();
            ConnectionStore.getInstance().removeConnection(pane.getText());
        });
        grid.add(deleteButton, 3, 4);
        GridPane.setHalignment(deleteButton, HPos.RIGHT);

        final Button connectButton = new Button(resBundle.getString("connection_connect"));
        connectButton.setMinSize(100, 28);
        connectButton.setPrefSize(100, 28);
        connectButton.setMaxSize(100, 28);
        connectButton.setOnAction((javafx.event.ActionEvent e) -> {
            final TitledPane currentPane = connectionAccordion.getExpandedPane();
            final DBConnection selectedCon = ConnectionStore.getInstance()
                    .getConnection(currentPane.getText());
            // Inject password entered by the user on this screen
            selectedCon.setPassword(passwordField.getText());
            viewModel.openMainWindow(selectedCon, connectButton.getScene().getWindow());
        });
        grid.add(connectButton, 3, 5);
        GridPane.setHalignment(connectButton, HPos.RIGHT);

        final TitledPane tp = new TitledPane();
        tp.setText(con.getConnectionName());
        tp.setContent(grid);
        connectionAccordion.getPanes().add(tp);
    }

    @FXML
    public void addConnection(final ActionEvent event) {
        viewModel.openAddConnectionWindow(addConnection.getScene().getWindow());
    }
}

