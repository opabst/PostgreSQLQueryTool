package de.uni_hannover.dbs.PostgreSQL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
 * This Java source file was auto generated by running 'gradle buildInit --type java-library'
 * by 'pabst' at '27.06.17 10:25' with Gradle 3.2.1
 *
 * @author pabst, @date 27.06.17 10:25
 */
public class PostgresQueryTool extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("views/MainWindow.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
