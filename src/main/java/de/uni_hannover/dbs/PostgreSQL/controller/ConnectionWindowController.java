package de.uni_hannover.dbs.PostgreSQL.controller;

import de.uni_hannover.dbs.PostgreSQL.db.ConnectionStore;
import de.uni_hannover.dbs.PostgreSQL.db.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
// TODO: Wenn Felder außer Verbindungsname geändert werden, saveAndExitBTN wieder ausgrauen
/**
 * Created by pabst on 05.07.17.
 */
public class ConnectionWindowController {

    @FXML
    private TextField connectionnameTF;

    @FXML
    private TextField hostnameTF;

    @FXML
    private TextField databasenameTF;

    @FXML
    private TextField usernameTF;

    @FXML
    private PasswordField passwordTF;

    @FXML
    private TextField portTF;

    @FXML
    private Button testConnectionBTN;

    @FXML
    private Button rejectInputBTN;

    @FXML
    private Button saveAndExitBTN;

    public ConnectionWindowController() {

    }

    @FXML
    public void testConnection() {
        String hostname = hostnameTF.getText();
        String port = portTF.getText();
        String databaseName = databasenameTF.getText();
        String username = usernameTF.getText();
        String password = passwordTF.getText();

        Connection testCon = null;
        try {
            testCon = DriverManager.getConnection("jdbc:postgresql://" + hostname + ":" + port + "/" + databaseName, username, password);
            saveAndExitBTN.setDisable(false);
        } catch (SQLException e) {
            System.err.print(e.getMessage());
        }
    }

    @FXML
    public void reject() {
        Stage stage = (Stage)rejectInputBTN.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void saveAndExit() {
        DBConnection dbc = new DBConnection(connectionnameTF.getText(),
                                            hostnameTF.getText(),
                                            portTF.getText(),
                                            databasenameTF.getText(),
                                            usernameTF.getText(),
                                            passwordTF.getText());

        ConnectionStore.getInstance().addConnection(dbc);
        Stage stage = (Stage)saveAndExitBTN.getScene().getWindow();
        stage.close();
    }
}
