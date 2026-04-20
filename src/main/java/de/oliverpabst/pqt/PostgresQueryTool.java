package de.oliverpabst.pqt;

import de.oliverpabst.pqt.controller.WelcomeScreenController;
import de.oliverpabst.pqt.db.ConnectionStore;
import de.oliverpabst.pqt.viewmodel.WelcomeViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

public class PostgresQueryTool extends Application {

    private static final Logger log = LoggerFactory.getLogger(PostgresQueryTool.class);

    @Override
    public void start(final Stage stage) throws Exception {
        final ResourceBundle resBundle = ResourceBundle.getBundle(
                "de.oliverpabst.pqt.lang_properties.guistrings");

        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader()
                .getResource("de/oliverpabst/pqt/views/WelcomeScreen.fxml"));
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
        try {
            ConnectionStore.getInstance().writeConnectionsToDisk();
        } catch (final java.io.IOException e) {
            log.error("Connection data could not be written", e);
        }
    }
}
