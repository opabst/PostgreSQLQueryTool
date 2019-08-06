package de.uni_hannover.dbs.PostgreSQL;

import de.uni_hannover.dbs.PostgreSQL.db.ConnectionStore;
import de.uni_hannover.dbs.PostgreSQL.db.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
 * @author pabst, @date 27.06.17 10:25
 */
public class PostgresQueryTool extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("views/MainWindow.fxml"));

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
        ConnectionStore.getInstance().writeCredentialsToDisk();
    }
}
