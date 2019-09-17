package de.uni_hannover.dbs.PostgreSQL.controller;

import de.uni_hannover.dbs.PostgreSQL.db.DBConnection;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class WelcomeScreenController {

    @FXML
    private Accordion connectionAccordion;

    @FXML
    private Button addConnection;


    public void addConnectionTitledPane(DBConnection _con) {
        TitledPane tp = new TitledPane();

        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setPadding(new Insets(5, 5, 5, 5));
        // Reihe 0
        grid.add(new Label("Name"), 0, 0);
        grid.add(new TextField(_con.getConnectionname()), 0, 1);
        // Reihe 1
        grid.add(new Label("Host"), 1, 0);
        grid.add(new TextField(_con.getHostname()), 1, 1);
        grid.add(new Label("Port"), 1, 2);
        grid.add(new TextField(_con.getPort()), 1, 3);
        // Reihe 2
        grid.add(new Label("Benutzer"), 2, 0);
        grid.add(new TextField(_con.getUsername()), 2, 1);
        grid.add(new Label("Passwort"), 2, 2);
        grid.add(new TextField(_con.getPassword()), 2, 3);
        // Reihe 4
        grid.add(new Button("Verbinden"), 3, 3);

        tp.setText(_con.getConnectionname());
        tp.setContent(grid);

        connectionAccordion.getPanes().add(tp);
    }
}
