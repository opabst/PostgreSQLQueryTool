package de.oliverpabst.pqt.viewmodel;

import de.oliverpabst.pqt.db.DBConnection;
import de.oliverpabst.pqt.db.metadata.MetadataManager;
import de.oliverpabst.pqt.model.DBOutlineTreeItem;
import de.oliverpabst.pqt.model.OutlineComponentType;
import de.oliverpabst.pqt.model.QueryResult;
import de.oliverpabst.pqt.service.QueryService;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ResourceBundle;

/**
 * ViewModel for the main query window. Owns all state that drives
 * {@code MainWindowController}. No direct references to JavaFX Node types.
 */
public class MainViewModel {

    // ── Query input ──────────────────────────────────────────────────────────
    private final StringProperty queryText = new SimpleStringProperty("");
    public StringProperty queryTextProperty() { return queryText; }

    /** True when the query text area is empty — used to disable action buttons. */
    public final BooleanBinding queryEmptyBinding =
            queryText.isEmpty().or(queryText.isEqualTo(""));

    // ── Query results ─────────────────────────────────────────────────────────
    private final ObservableList<String> resultColumnNames =
            FXCollections.observableArrayList();
    public ObservableList<String> getResultColumnNames() { return resultColumnNames; }

    private final ObservableList<ObservableList<String>> resultRows =
            FXCollections.observableArrayList();
    public ObservableList<ObservableList<String>> getResultRows() { return resultRows; }

    // ── EXPLAIN / error output ────────────────────────────────────────────────
    private final StringProperty explainText = new SimpleStringProperty("");
    public StringProperty explainTextProperty() { return explainText; }

    private final StringProperty errorText = new SimpleStringProperty("");
    public StringProperty errorTextProperty() { return errorText; }

    // ── Selected result tab (0 = result, 1 = explain, 2 = errors) ────────────
    private final IntegerProperty selectedTab = new SimpleIntegerProperty(0);
    public IntegerProperty selectedTabProperty() { return selectedTab; }

    // ── Running state ─────────────────────────────────────────────────────────
    private final BooleanProperty queryRunning = new SimpleBooleanProperty(false);
    public BooleanProperty queryRunningProperty() { return queryRunning; }

    // ── Schema outline tree root ──────────────────────────────────────────────
    private final ObjectProperty<DBOutlineTreeItem> treeRoot =
            new SimpleObjectProperty<>();
    public ObjectProperty<DBOutlineTreeItem> treeRootProperty() { return treeRoot; }

    // ── Infrastructure ────────────────────────────────────────────────────────
    private final DBConnection dbConnection;
    private final MetadataManager metadataManager;
    private final ResourceBundle resBundle;

    public MainViewModel(final DBConnection dbConnection, final ResourceBundle resBundle) {
        this.dbConnection = dbConnection;
        this.resBundle = resBundle;
        this.metadataManager = new MetadataManager(dbConnection);

        final DBOutlineTreeItem root = new DBOutlineTreeItem(
                resBundle.getString("tree_view_root"),
                OutlineComponentType.ROOT,
                metadataManager);
        root.setExpanded(false);
        treeRoot.set(root);
    }

    // ── Commands ──────────────────────────────────────────────────────────────

    public void runQuery() {
        executeWith(QueryService.QueryMode.EXECUTE);
    }

    public void explainQuery() {
        executeWith(QueryService.QueryMode.EXPLAIN);
    }

    public void analyzeQuery() {
        executeWith(QueryService.QueryMode.EXPLAIN_ANALYZE);
    }

    private void executeWith(final QueryService.QueryMode mode) {
        final String sql = effectiveSql();
        if (sql.isBlank()) return;

        final QueryService service = new QueryService();
        service.setDbConnection(dbConnection);
        service.setSqlText(sql);
        service.setMode(mode);

        service.setOnRunning(e -> queryRunning.set(true));

        service.setOnSucceeded(e -> {
            queryRunning.set(false);
            final QueryResult result = service.getValue();
            applyResult(result, mode);
        });

        service.setOnFailed(e -> {
            queryRunning.set(false);
            final Throwable ex = service.getException();
            errorText.set(errorText.get() + "\n" + (ex != null ? ex.getMessage() : "Unknown error"));
            selectedTab.set(2);
        });

        service.start();
    }

    private void applyResult(final QueryResult result, final QueryService.QueryMode mode) {
        if (mode == QueryService.QueryMode.EXECUTE) {
            resultColumnNames.setAll(result.columnNames());
            resultRows.clear();
            for (final java.util.List<String> row : result.rows()) {
                resultRows.add(FXCollections.observableArrayList(row));
            }
            selectedTab.set(0);
        } else {
            // EXPLAIN / EXPLAIN ANALYZE: single-column text output
            final StringBuilder sb = new StringBuilder();
            for (final java.util.List<String> row : result.rows()) {
                sb.append(row.isEmpty() ? "" : row.get(0)).append('\n');
            }
            explainText.set(sb.toString().stripTrailing());
            selectedTab.set(1);
        }
    }

    /** Returns selected text if non-empty, otherwise the full query text area content. */
    private String effectiveSql() {
        return queryText.get();
    }
}
