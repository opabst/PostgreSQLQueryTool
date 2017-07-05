package de.uni_hannover.dbs.PostgreSQL.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Created by pabst on 05.07.17.
 */
public class ConnectionWindowController {

    @FXML
    private TextField ConWinConnectionNameTF;

    @FXML
    private TextField ConWinHostnameTF;

    @FXML
    private TextField ConWinDatabaseTF;

    @FXML
    private TextField ConWinUsernameTF;

    @FXML
    private PasswordField ConWinPasswordTF;

    public ConnectionWindowController() {

    }
}
