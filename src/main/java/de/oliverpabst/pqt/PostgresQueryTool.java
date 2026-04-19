package de.oliverpabst.pqt;

import de.oliverpabst.pqt.controller.WelcomeScreenController;
import de.oliverpabst.pqt.db.ConnectionStore;
import de.oliverpabst.pqt.viewmodel.WelcomeViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class PostgresQueryTool extends Application {

    @Override
    public void start(final Stage stage) throws Exception {
        final ResourceBundle resBundle = ResourceBundle.getBundle(
                "de.oliverpabst.PQT.lang_properties.guistrings");

        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader()
                .getResource("de/oliverpabst/PQT/views/WelcomeScreen.fxml"));
        final Parent root = loader.load();
        final WelcomeScreenController controller = loader.getController();

        final WelcomeViewModel vm = new WelcomeViewModel(resBundle);
        vm.setPrimaryStage(stage);
        controller.setViewModel(vm);
        controller.setStage(stage);

        final Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.getIcons().add(ImageProvider.getInstance().getAppIcon());
        stage.show();
    }

    @Override
    public void stop() {
        ConnectionStore.getInstance().closeAllConnections();
        if (!ConnectionStore.getInstance().writeConnectionsToDisk()) {
            System.err.println("Connection data could not be written!");
        }
    }
}
