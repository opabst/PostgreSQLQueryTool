package de.oliverpabst.pqt.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.ResourceBundle;


public class AboutWindowController {

    @FXML
    private WebView aboutWebView;

    @FXML
    private Button aboutCloseButton;

    @FXML
    public void initialize() {
        ResourceBundle resBundle = ResourceBundle.getBundle("de.oliverpabst.PQT.lang_properties.guistrings");
        aboutCloseButton.setText(resBundle.getString("about_close_button"));

        String htmlMessage =
                "<p><h1><b>PostgreSQL-Query-Tool</b></h1></p>\n" +
                "<br/>\n" +
                "<p><b>Autor</b>: Oliver Pabst &#9400; 2019<br></p>\n" +
                "<p><b>Lizenz</b>: <a href=&quot;https://www.gnu.org/licenses/gpl-3.0.en.html&quot;>GNU General Public License v3.0</a></p>\n";

        WebEngine engine = aboutWebView.getEngine();
        engine.loadContent(htmlMessage);
    }

    @FXML
    public void closeScreen(ActionEvent event) {
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
