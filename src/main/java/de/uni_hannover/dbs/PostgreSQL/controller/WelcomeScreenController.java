package de.uni_hannover.dbs.PostgreSQL.controller;

import de.uni_hannover.dbs.PostgreSQL.db.ConnectionStore;
import de.uni_hannover.dbs.PostgreSQL.db.DBConnection;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class WelcomeScreenController {

    @FXML
    private Accordion connectionAccordion;

    @FXML
    private Button addConnection;

    @FXML
    public void initialize() {
        // TODO: Listener testen -> funktioniert nicht
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

        for(DBConnection con: ConnectionStore.getInstance().getConnections()) {
            addConnectionTitledPane(con);
        }

        ResourceBundle resBundle = ResourceBundle.getBundle("de.uni_hannover.dbs.PostgreSQL.lang_properties.guistrings");
        addConnection.setText(resBundle.getString("connection_add_connection"));
    }


    public void addConnectionTitledPane(DBConnection _con) {
        ResourceBundle resBundle = ResourceBundle.getBundle("de.uni_hannover.dbs.PostgreSQL.lang_properties.guistrings");
        TitledPane tp = new TitledPane();

        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setHgap(4);
        grid.getColumnConstraints().add(new ColumnConstraints(150));
        grid.getColumnConstraints().add(new ColumnConstraints(175));
        grid.getColumnConstraints().add(new ColumnConstraints(75));
        grid.getColumnConstraints().add(new ColumnConstraints(100));
        grid.setPadding(new Insets(5, 5, 5, 5));
        // Reihe 0
        grid.add(new Label(resBundle.getString("connection_name_label")), 0, 0);
        grid.add(new TextField(_con.getConnectionname()), 1, 0);
        // Reihe 1
        grid.add(new Label(resBundle.getString("connection_hostname_label")), 0, 1);
        grid.add(new TextField(_con.getHostname()), 1, 1);
        grid.add(new Label(resBundle.getString("connection_port_label")), 2, 1);
        grid.add(new TextField(_con.getPort()), 3, 1);
        // Reihe 2
        grid.add(new Label(resBundle.getString("connection_username_label")), 0, 2);
        grid.add(new TextField(_con.getUsername()), 1, 2);
        grid.add(new Label(resBundle.getString("connection_password_label")), 2, 2);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(resBundle.getString("connection_password_helptext"));
        grid.add(passwordField, 3, 2);
        // Reihe 4
        grid.add(new Label(resBundle.getString("connection_databasename_label")), 0, 3);
        grid.add(new TextField(_con.getDbname()), 1, 3);
        Button connectButton = new Button();
        connectButton.setText(resBundle.getString("connection_connect"));
        connectButton.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                TitledPane currentPane = connectionAccordion.getExpandedPane();

                DBConnection con = ConnectionStore.getInstance().getConnection(currentPane.getText());

                // TODO: start the mainwindowcontroller with currentPane as a parameter
            }
        });
        grid.add(connectButton, 3, 4);

        tp.setText(_con.getConnectionname());
        tp.setContent(grid);

        connectionAccordion.getPanes().add(tp);
    }

    @FXML
    public void addConnection(ActionEvent event) {
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
        connectionWindow.initOwner(((Button)event.getTarget()).getScene().getWindow());
        connectionWindow.showAndWait();
    }
}
