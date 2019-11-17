package de.oliverpabst.PQT.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;


public class AboutWindowController {

    @FXML
    private HTMLEditor aboutTextHTML;

    @FXML
    public void initialize() {
        String htmlMessage =
                "<html>" +
                        "<head>PostgreSQL-Query-Tool</head>" +
                        "<body>" +
                        "<b>Autor</b>: Oliver Pabst &#9400; 2019<br>" +
                        "<b>Lizenz</b>: <a href=&quot;https://www.gnu.org/licenses/gpl-3.0.en.html&quot;>GNU General Public License v3.0</a>" +
                        "</body>" +
                        "</html>";

        aboutTextHTML.setHtmlText(htmlMessage);
    }

    @FXML
    public void closeScreen(ActionEvent event) {
        Scene aboutWindowScene = ((Button)event.getTarget()).getScene(); // TODO: Stage laden
        Stage stage = new Stage();
        stage.setScene(aboutWindowScene);
        stage.close();
    }
}
