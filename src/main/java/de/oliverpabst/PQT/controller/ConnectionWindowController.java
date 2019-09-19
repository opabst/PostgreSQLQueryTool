package de.oliverpabst.PQT.controller;

import de.oliverpabst.PQT.db.ConnectionStore;
import de.oliverpabst.PQT.db.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.ResourceBundle;
// TODO: Wenn Felder außer Verbindungsname geändert werden, saveAndExitBTN wieder ausgrauen
/**
 * Created by pabst on 05.07.17.
 */
public class ConnectionWindowController {

    @FXML
    private Label mainLabel;

    @FXML
    private Label connectionnameLBL;

    @FXML
    private TextField connectionnameTF;

    @FXML
    private Label hostnameLBL;

    @FXML
    private TextField hostnameTF;

    @FXML
    private Label portLBL;

    @FXML
    private TextField portTF;

    @FXML
    private Label databasenameLBL;

    @FXML
    private TextField databasenameTF;

    @FXML
    private Label usernameLBL;

    @FXML
    private TextField usernameTF;

    @FXML
    private Label passwordLBL;

    @FXML
    private PasswordField passwordTF;

    @FXML
    private Button testConnectionBTN;

    @FXML
    private Button rejectInputBTN;

    @FXML
    private Button saveAndCloseBTN;

    @FXML
    private Label conStatusLBL;

    public ConnectionWindowController() {

    }

    @FXML
    public void initialize() {
        ResourceBundle resBundle = ResourceBundle.getBundle("de.oliverpabst.PQT.lang_properties.guistrings");

        // Oberflächenelemente lokalisieren
        mainLabel.setText(resBundle.getString("connection_main_label"));

        connectionnameLBL.setText(resBundle.getString("connection_name_label"));
        connectionnameTF.setTooltip(new Tooltip(resBundle.getString("connection_name_helptext")));

        hostnameLBL.setText(resBundle.getString("connection_hostname_label"));
        hostnameTF.setTooltip(new Tooltip(resBundle.getString("connection_hostname_helptext")));

        portLBL.setText(resBundle.getString("connection_port_label"));
        portTF.setTooltip(new Tooltip(resBundle.getString("connection_port_helptext")));

        databasenameLBL.setText(resBundle.getString("connection_databasename_label"));
        databasenameTF.setTooltip(new Tooltip(resBundle.getString("connection_databasename_helptext")));

        usernameLBL.setText(resBundle.getString("connection_username_label"));
        usernameTF.setTooltip(new Tooltip(resBundle.getString("connection_username_helptext")));

        passwordLBL.setText(resBundle.getString("connection_password_label"));
        passwordTF.setTooltip(new Tooltip(resBundle.getString("connection_password_helptext")));

        conStatusLBL.setText(resBundle.getString("connection_status_unknown"));

        testConnectionBTN.setText(resBundle.getString("connection_test_button"));
        rejectInputBTN.setText(resBundle.getString("connection_reject_button"));
        saveAndCloseBTN.setText(resBundle.getString("connection_save_and_close_button"));
    }

    @FXML
    public void testConnection() {
        // TODO: teste auf bereits vorhandene Verbindungen

        ResourceBundle resBundle = ResourceBundle.getBundle("de.oliverpabst.PQT.lang_properties.guistrings");

        String hostname = hostnameTF.getText();
        String port = portTF.getText();
        String databaseName = databasenameTF.getText();
        String username = usernameTF.getText();
        String password = passwordTF.getText();

        try {
            Connection testCon = DriverManager.getConnection("jdbc:postgresql://" + hostname + ":" + port + "/" + databaseName, username, password);
            testCon.close();
            saveAndCloseBTN.setDisable(false);
            conStatusLBL.setText(resBundle.getString("connection_status_success"));
            conStatusLBL.setStyle("-fx-text-fill: green;");
        } catch (SQLException e) {
            conStatusLBL.setText(resBundle.getString("connection_status_error"));
            conStatusLBL.setStyle("-fx-text-fill: red;");
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
        Stage stage = (Stage) saveAndCloseBTN.getScene().getWindow();
        stage.close();
    }
}
