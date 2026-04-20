package de.oliverpabst.pqt.viewmodel;

import de.oliverpabst.pqt.db.ConnectionStore;
import de.oliverpabst.pqt.db.DBConnection;
import de.oliverpabst.pqt.service.ConnectionTestService;
import javafx.beans.property.*;
import javafx.concurrent.Worker;

import java.util.ResourceBundle;

/**
 * ViewModel for the "Add Connection" dialog. Drives
 * {@code ConnectionWindowController}.
 */
public class ConnectionViewModel {

    private final StringProperty connectionName = new SimpleStringProperty("");
    private final StringProperty hostName = new SimpleStringProperty("");
    private final StringProperty port = new SimpleStringProperty("");
    private final StringProperty databaseName = new SimpleStringProperty("");
    private final StringProperty userName = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");

    private final BooleanProperty testSuccessful = new SimpleBooleanProperty(false);
    private final BooleanProperty testing = new SimpleBooleanProperty(false);
    private final StringProperty statusMessage = new SimpleStringProperty("");

    public StringProperty connectionNameProperty() { return connectionName; }
    public StringProperty hostNameProperty()       { return hostName; }
    public StringProperty portProperty()           { return port; }
    public StringProperty databaseNameProperty()   { return databaseName; }
    public StringProperty userNameProperty()       { return userName; }
    public StringProperty passwordProperty()       { return password; }

    public BooleanProperty testSuccessfulProperty() { return testSuccessful; }
    public BooleanProperty testingProperty()         { return testing; }
    public StringProperty statusMessageProperty()    { return statusMessage; }

    private final ResourceBundle resBundle;

    public ConnectionViewModel(final ResourceBundle resBundle) {
        this.resBundle = resBundle;
        statusMessage.set(resBundle.getString("connection_status_unknown"));
    }

    // ── Commands ──────────────────────────────────────────────────────────────

    /**
     * Starts a background {@link ConnectionTestService}. Updates
     * {@code testSuccessful} and {@code statusMessage} when done.
     */
    public void testConnection() {
        testSuccessful.set(false);
        testing.set(true);
        statusMessage.set(resBundle.getString("connection_status_unknown"));

        final ConnectionTestService task = new ConnectionTestService(
                hostName.get(), port.get(), databaseName.get(),
                userName.get(), password.get());

        task.stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                testing.set(false);
                testSuccessful.set(true);
                statusMessage.set(resBundle.getString("connection_status_success"));
            } else if (newState == Worker.State.FAILED) {
                testing.set(false);
                testSuccessful.set(false);
                statusMessage.set(resBundle.getString("connection_status_error"));
            }
        });

        final Thread t = Thread.ofVirtual().name("connection-test").unstarted(task);
        t.start();
    }

    /**
     * Creates and saves the {@link DBConnection} to {@link ConnectionStore}.
     *
     * @return {@code true} if the connection was added successfully;
     *         {@code false} if a connection with the same name already exists
     */
    public boolean save() {
        final DBConnection dbc = new DBConnection(
                connectionName.get(),
                hostName.get(),
                port.get(),
                databaseName.get(),
                userName.get(),
                password.get());

        if (ConnectionStore.getInstance().addConnection(dbc)) {
            return true;
        } else {
            testSuccessful.set(false);
            statusMessage.set(resBundle.getString("connection_status_already_exists"));
            return false;
        }
    }
}
