package de.oliverpabst.pqt.viewmodel;

import de.oliverpabst.pqt.ImageProvider;
import de.oliverpabst.pqt.controller.ConnectionWindowController;
import de.oliverpabst.pqt.controller.MainWindowController;
import de.oliverpabst.pqt.db.ConnectionStore;
import de.oliverpabst.pqt.db.DBConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * ViewModel for the WelcomeScreen. Owns the connection list and commands for
 * opening child windows.
 */
public class WelcomeViewModel {

    private static final Logger log = LoggerFactory.getLogger(WelcomeViewModel.class);
    private final ResourceBundle resBundle;
    private Stage primaryStage;
    private final StringProperty loadError = new SimpleStringProperty("");

    public WelcomeViewModel(final ResourceBundle resBundle) {
        this.resBundle = resBundle;
        try {
            ConnectionStore.getInstance().readConnectionsFromDisk();
        } catch (final IOException e) {
            log.error("Failed to read connections from disk", e);
            loadError.set(e.getMessage());
        }
    }

    public StringProperty loadErrorProperty() { return loadError; }

    public void setPrimaryStage(final Stage stage) {
        this.primaryStage = stage;
    }

    public ObservableList<DBConnection> getConnections() {
        return ConnectionStore.getInstance().getConnections();
    }

    /**
     * Opens the "Add Connection" modal dialog owned by {@code owner}.
     */
    public void openAddConnectionWindow(final Window owner) {
        try {
            final FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader().getResource(
                            "de/oliverpabst/pqt/views/ConnectionWindow.fxml"));
            final Parent pane = loader.load();
            final ConnectionWindowController ctrl = loader.getController();

            final ConnectionViewModel vm = new ConnectionViewModel(resBundle);
            ctrl.setViewModel(vm);

            final Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.getIcons().add(ImageProvider.getInstance().getAppIcon());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(owner);
            stage.showAndWait();
        } catch (final IOException e) {
            log.error("Failed to open Add Connection window", e);
        }
    }

    /**
     * Opens the main query window for the given connection.
     */    public void openMainWindow(final DBConnection connection, final Window owner) {
        try {
            final FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader().getResource(
                            "de/oliverpabst/pqt/views/MainWindow.fxml"));
            final Parent pane = loader.load();
            final MainWindowController ctrl = loader.getController();

            final MainViewModel vm = new MainViewModel(connection, resBundle);
            ctrl.setViewModel(vm);

            final Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.getIcons().add(ImageProvider.getInstance().getAppIcon());
            stage.show();
        } catch (final IOException e) {
            log.error("Failed to open main window", e);
        }
    }
}
