package de.oliverpabst.pqt.controller;

import de.oliverpabst.pqt.viewmodel.ConnectionViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class ConnectionWindowController {

    private ConnectionViewModel viewModel;

    @FXML private Label mainLabel;
    @FXML private Label connectionnameLBL;
    @FXML private TextField connectionnameTF;
    @FXML private Label hostnameLBL;
    @FXML private TextField hostnameTF;
    @FXML private Label portLBL;
    @FXML private TextField portTF;
    @FXML private Label databasenameLBL;
    @FXML private TextField databasenameTF;
    @FXML private Label usernameLBL;
    @FXML private TextField usernameTF;
    @FXML private Label passwordLBL;
    @FXML private PasswordField passwordTF;
    @FXML private Button testConnectionBTN;
    @FXML private Button rejectInputBTN;
    @FXML private Button saveAndCloseBTN;
    @FXML private Label conStatusLBL;

    public ConnectionWindowController() { }

    @FXML
    public void initialize() { }

    public void setViewModel(final ConnectionViewModel vm) {
        this.viewModel = vm;

        final ResourceBundle resBundle = ResourceBundle.getBundle(
                "de.oliverpabst.pqt.lang_properties.guistrings");

        mainLabel.setText(resBundle.getString("connection_window_title"));
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
        testConnectionBTN.setText(resBundle.getString("connection_test_button"));
        rejectInputBTN.setText(resBundle.getString("connection_reject_button"));
        saveAndCloseBTN.setText(resBundle.getString("connection_save_and_close_button"));

        conStatusLBL.textProperty().bind(vm.statusMessageProperty());

        connectionnameTF.textProperty().bindBidirectional(vm.connectionNameProperty());
        hostnameTF.textProperty().bindBidirectional(vm.hostNameProperty());
        portTF.textProperty().bindBidirectional(vm.portProperty());
        databasenameTF.textProperty().bindBidirectional(vm.databaseNameProperty());
        usernameTF.textProperty().bindBidirectional(vm.userNameProperty());
        passwordTF.textProperty().bindBidirectional(vm.passwordProperty());

        saveAndCloseBTN.disableProperty().bind(vm.testSuccessfulProperty().not());
        testConnectionBTN.disableProperty().bind(vm.testingProperty());
    }

    @FXML
    public void testConnection() {
        viewModel.testConnection();
    }

    @FXML
    public void reject() {
        final Stage stage = (Stage) rejectInputBTN.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void saveAndExit() {
        if (viewModel.save()) {
            final Stage stage = (Stage) saveAndCloseBTN.getScene().getWindow();
            stage.close();
        }
    }
}
