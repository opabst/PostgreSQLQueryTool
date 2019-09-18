package de.uni_hannover.dbs.PostgreSQL;

import de.uni_hannover.dbs.PostgreSQL.db.ConnectionStore;
import de.uni_hannover.dbs.PostgreSQL.db.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class PostgresQueryTool extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().
                getResource("de/uni_hannover/dbs/PostgreSQL/views/WelcomeScreen.fxml"));
        Parent root = loader.load();
        primaryStage = stage;

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void stop() {
        ConnectionStore.getInstance().closeAllConnections();
        if(!ConnectionStore.getInstance().writeCredentialsToDisk()) {
            System.err.println("Verbindungsdaten konnten nicht geschrieben werden!");
        }
    }
}
