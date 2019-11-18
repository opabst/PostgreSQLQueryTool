package de.oliverpabst.PQT.controller;

import de.oliverpabst.PQT.db.ConnectionStore;
import de.oliverpabst.PQT.db.DBConnection;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class WelcomeScreenController {

    @FXML
    private Accordion connectionAccordion;

    @FXML
    private Button addConnection;

    public WelcomeScreenController() {
        ConnectionStore.getInstance();
    }

    @FXML
    public void initialize() {
        ConnectionStore.getInstance().getConnections().addListener(new ListChangeListener<DBConnection>() {
            @Override
            public void onChanged(Change<? extends DBConnection> c) {
                while (c.next()) {
                    if(c.wasPermutated()) {
                        System.out.println("Was permutated!");
                    } else if (c.wasUpdated()) {
                        System.out.println("Was updated!");
                    } else if(c.wasAdded()) {
                        for(DBConnection con: c.getAddedSubList()) {
                            addConnectionTitledPane(con);
                        }
                    } else if(c.wasRemoved()) {
                        ArrayList<TitledPane> deletedPanes = new ArrayList<>();
                        for(DBConnection con: c.getRemoved()) {
                            for(TitledPane pane: connectionAccordion.getPanes()) {
                                if(pane.getText().equals(con.getConnectionname())) {
                                    deletedPanes.add(pane);
                                }
                            }
                        }
                        connectionAccordion.getPanes().removeAll(deletedPanes);
                    }
                }
            }
        });
        ConnectionStore.getInstance().readCredentialsFromDisk();

        ResourceBundle resBundle = ResourceBundle.getBundle("de.oliverpabst.PQT.lang_properties.guistrings");
        addConnection.setText(resBundle.getString("connection_add_connection"));
    }


    public void addConnectionTitledPane(DBConnection _con) {
        ResourceBundle resBundle = ResourceBundle.getBundle("de.oliverpabst.PQT.lang_properties.guistrings");
        TitledPane tp = new TitledPane();

        GridPane grid = new GridPane();

        // Größenconstraints
        grid.setVgap(4);
        grid.setHgap(4);
        grid.getColumnConstraints().add(new ColumnConstraints(150));
        grid.getColumnConstraints().add(new ColumnConstraints(175));
        grid.getColumnConstraints().add(new ColumnConstraints(75));
        grid.getColumnConstraints().add(new ColumnConstraints(150));
        grid.setPadding(new Insets(5, 5, 5, 5));

        // Reihe 0
        Label connectionNameLabel = new Label(resBundle.getString("connection_name_label"));
        grid.add(connectionNameLabel, 0, 0);
        GridPane.setHalignment(connectionNameLabel, HPos.RIGHT);

        TextField connectionnameTextfield = new TextField(_con.getConnectionname());
        connectionnameTextfield.setDisable(true);
        grid.add(connectionnameTextfield, 1, 0);

        // Reihe 1
        Label hostnameLabel = new Label(resBundle.getString("connection_hostname_label"));
        grid.add(hostnameLabel, 0, 1);
        GridPane.setHalignment(hostnameLabel, HPos.RIGHT);

        TextField hostnameTextField = new TextField(_con.getHostname());
        hostnameTextField.setDisable(true);
        grid.add(hostnameTextField, 1, 1);


        Label portLabel = new Label(resBundle.getString("connection_port_label"));
        grid.add(portLabel, 2, 1);
        GridPane.setHalignment(portLabel, HPos.RIGHT);

        TextField portTextField = new TextField(_con.getPort());
        portTextField.setDisable(true);
        grid.add(portTextField, 3, 1);

        // Reihe 2
        Label usernameLabel = new Label(resBundle.getString("connection_username_label"));
        grid.add(usernameLabel, 0, 2);
        GridPane.setHalignment(usernameLabel, HPos.RIGHT);

        TextField usernameTextField = new TextField(_con.getUsername());
        usernameTextField.setDisable(true);
        grid.add(usernameTextField, 1, 2);


        Label passwordLabel = new Label(resBundle.getString("connection_password_label"));
        grid.add(passwordLabel, 2, 2);
        GridPane.setHalignment(passwordLabel, HPos.RIGHT);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(resBundle.getString("connection_password_helptext"));
        passwordField.setDisable(true);
        grid.add(passwordField, 3, 2);
        // Reihe 4
        Label databasenameLabel = new Label(resBundle.getString("connection_databasename_label"));
        grid.add(databasenameLabel, 0, 3);
        GridPane.setHalignment(databasenameLabel, HPos.RIGHT);

        TextField dbnameTextField = new TextField(_con.getDbname());
        dbnameTextField.setDisable(true);
        grid.add(dbnameTextField, 1, 3);

        Button deleteButton = new Button();
        deleteButton.setMinSize(100, 28);
        deleteButton.setPrefSize(100, 28);
        deleteButton.setPrefSize(100, 28);
        deleteButton.setText(resBundle.getString("connection_delete"));
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TitledPane pane = connectionAccordion.getExpandedPane();
                ConnectionStore.getInstance().removeConnection(pane.getText());
            }
        });
        grid.add(deleteButton,3, 4);
        GridPane.setHalignment(deleteButton, HPos.RIGHT);

        Button modifyButton = new Button();
        modifyButton.setMinSize(100, 28);
        modifyButton.setPrefSize(100, 28);
        modifyButton.setMaxSize(100, 28);
        modifyButton.setText(resBundle.getString("connection_modify"));
        modifyButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });
        grid.add(modifyButton, 3, 5);
        GridPane.setHalignment(modifyButton, HPos.RIGHT);

        Button connectButton = new Button();
        connectButton.setMinSize(100, 28);
        connectButton.setPrefSize(100, 28);
        connectButton.setMaxSize(100, 28);
        connectButton.setText(resBundle.getString("connection_connect"));
        connectButton.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                TitledPane currentPane = connectionAccordion.getExpandedPane();

                DBConnection con = ConnectionStore.getInstance().getConnection(currentPane.getText());

                Stage mainWindow = new Stage();
                Parent mainWindowPane = null;
                MainWindowController controller = null;
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("de/oliverpabst/PQT/views/MainWindow.fxml"));
                    mainWindowPane = loader.load();
                    controller = loader.<MainWindowController>getController();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                controller.setDbConnection(con);

                Scene scene = new Scene(mainWindowPane);
                mainWindow.setScene(scene);
                mainWindow.show();
            }
        });
        GridPane.setHalignment(connectButton, HPos.RIGHT);
        grid.add(connectButton, 3, 6);

        tp.setText(_con.getConnectionname());
        tp.setContent(grid);

        connectionAccordion.getPanes().add(tp);
    }

    @FXML
    public void addConnection(ActionEvent event) {

        Stage connectionWindow = new Stage();

        Parent connectionPane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().
                    getResource("de/oliverpabst/PQT/views/ConnectionWindow.fxml"));
            connectionPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(connectionPane);
        connectionWindow.setScene(scene);

        connectionWindow.initModality(Modality.APPLICATION_MODAL);
        connectionWindow.initOwner(((Button)event.getTarget()).getScene().getWindow());
        connectionWindow.showAndWait();
    }
}
