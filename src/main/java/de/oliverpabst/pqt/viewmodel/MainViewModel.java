package de.oliverpabst.pqt.viewmodel;

import de.oliverpabst.pqt.db.DBConnection;
import de.oliverpabst.pqt.db.metadata.MetadataManager;
import de.oliverpabst.pqt.db.metadata.model.Function;
import de.oliverpabst.pqt.db.metadata.model.Sequence;
import de.oliverpabst.pqt.db.metadata.model.Table;
import de.oliverpabst.pqt.db.metadata.model.View;
import de.oliverpabst.pqt.model.DBOutlineTreeItem;
import de.oliverpabst.pqt.model.OutlineComponentType;
import de.oliverpabst.pqt.model.QueryResult;
import de.oliverpabst.pqt.service.QueryService;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.ResourceBundle;

/**
 * ViewModel for the main query window. Owns all state that drives
 * {@code MainWindowController}. No direct references to JavaFX Node types.
 */
public class MainViewModel {

    private static final String METADATA_DEFAULT_TEXT_KEY = "metadata_default_text";

    // ── Query input ──────────────────────────────────────────────────────────
    private final StringProperty queryText = new SimpleStringProperty("");
    public StringProperty queryTextProperty() { return queryText; }

    private final StringProperty selectedQueryText = new SimpleStringProperty("");
    public StringProperty selectedQueryTextProperty() { return selectedQueryText; }

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

    private final StringProperty metadataText = new SimpleStringProperty("");
    public StringProperty metadataTextProperty() { return metadataText; }

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
        root.setExpanded(true);
        treeRoot.set(root);

        setMetadataDefaultText();
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

    /** Returns selected text when non-blank, otherwise the full query text area content. */
    private String effectiveSql() {
        final String selection = selectedQueryText.get();
        return selection.isBlank() ? queryText.get() : selection;
    }

    public void onTreeItemSelected(final TreeItem<String> selectedItem) {
        if (!(selectedItem instanceof DBOutlineTreeItem selected)) {
            setMetadataDefaultText();
            return;
        }

        final OutlineComponentType type = selected.getComponentType();
        if (type == OutlineComponentType.TABLE_OBJECT) {
            handleSelectedTable(selected);
            return;
        }

        if (type != OutlineComponentType.DB_OBJECT) {
            setMetadataDefaultText();
            return;
        }

        final DBOutlineTreeItem parent = parentAsOutline(selected);
        if (parent == null) {
            setMetadataDefaultText();
            return;
        }

        final String objectName = selected.getValue();
        final String schemaName = parent.getParent() != null ? parent.getParent().getValue() : null;
        if (schemaName == null) {
            setMetadataDefaultText();
            return;
        }

        handleSelectedDatabaseObject(parent, schemaName, objectName);
    }

    private void handleSelectedTable(final DBOutlineTreeItem selected) {
        final String schemaName = schemaNameFromTableObject(selected);
        final String tableName = selected.getValue();
        if (schemaName == null) {
            setMetadataDefaultText();
            return;
        }
        metadataManager.loadTablesForSchema(schemaName);
        final Table table = metadataManager.getTablesForSchema(schemaName).get(tableName);
        if (table == null) {
            setMetadataDefaultText();
            return;
        }
        metadataText.set(formatTableMetadata(schemaName, tableName, table));
    }

    private void handleSelectedDatabaseObject(final DBOutlineTreeItem parent, final String schemaName,
                                              final String objectName) {
        switch (parent.getComponentType()) {
            case VIEW -> handleSelectedView(schemaName, objectName);
            case FUNCTION -> handleSelectedFunction(schemaName, objectName);
            case SEQUENCE -> handleSelectedSequence(schemaName, objectName);
            default -> setMetadataDefaultText();
        }
    }

    private void handleSelectedView(final String schemaName, final String objectName) {
        metadataManager.loadViewsForSchema(schemaName);
        final View view = metadataManager.getViewsForSchema(schemaName).get(objectName);
        metadataText.set(view != null ? formatViewMetadata(schemaName, view) : defaultMetadataText());
    }

    private void handleSelectedFunction(final String schemaName, final String objectName) {
        metadataManager.loadFunctionsForSchema(schemaName);
        final Function function = metadataManager.getFunctionsForSchema(schemaName).get(objectName);
        metadataText.set(function != null ? formatFunctionMetadata(schemaName, function) : defaultMetadataText());
    }

    private void handleSelectedSequence(final String schemaName, final String objectName) {
        metadataManager.loadSequencesForSchema(schemaName);
        final Sequence sequence = metadataManager.getSequencesForSchema(schemaName).get(objectName);
        metadataText.set(sequence != null ? formatSequenceMetadata(schemaName, sequence) : defaultMetadataText());
    }

    private DBOutlineTreeItem parentAsOutline(final DBOutlineTreeItem selected) {
        if (selected.getParent() instanceof DBOutlineTreeItem parent) {
            return parent;
        }
        return null;
    }

    private String schemaNameFromTableObject(final DBOutlineTreeItem tableObjectItem) {
        final TreeItem<String> tableGroup = tableObjectItem.getParent();
        final TreeItem<String> schemaItem = tableGroup != null ? tableGroup.getParent() : null;
        return schemaItem != null ? schemaItem.getValue() : null;
    }

    private String formatTableMetadata(final String schemaName, final String tableName, final Table table) {
        return "Table: " + schemaName + "." + tableName + "\n" +
               "Columns: " + table.getColumns().size() + "\n" +
               "Constraints: " + table.getConstraints().size() + "\n" +
               "Indexes: " + table.getIndices().size() + "\n" +
               "Triggers: " + table.getTriggers().size();
    }

    private String formatViewMetadata(final String schemaName, final View view) {
        return "View: " + schemaName + "." + view.getObjectName() + "\n" +
               "Materialized: " + view.getIsMaterialized() + "\n\n" +
               "Definition:\n" + safe(view.getViewDefinition());
    }

    private String formatFunctionMetadata(final String schemaName, final Function function) {
        return "Function: " + schemaName + "." + function.getObjectName() + "\n" +
               "Return type: " + safe(function.getReturnType()) + "\n" +
               "Language: " + safe(function.getLanguage()) + "\n\n" +
               "Definition:\n" + safe(function.getFunctionDefinition());
    }

    private String formatSequenceMetadata(final String schemaName, final Sequence sequence) {
        return "Sequence: " + schemaName + "." + sequence.getObjectName() + "\n" +
               "Data type: " + safe(sequence.getDataType()) + "\n" +
               "Current value: " + safe(sequence.getCurrentValue()) + "\n" +
               "Next value: " + safe(sequence.getNextValue()) + "\n" +
               "Minimum value: " + safe(sequence.getMinimumValue()) + "\n" +
               "Maximum value: " + safe(sequence.getMaximumValue()) + "\n" +
               "Increment: " + safe(sequence.getIncrementValue()) + "\n" +
               "Is cyclic: " + safe(sequence.getIsCyclic()) + "\n" +
               "Was called: " + safe(sequence.getWasCalled());
    }

    private String safe(final Object value) {
        return value == null ? "" : value.toString();
    }

    private void setMetadataDefaultText() {
        metadataText.set(defaultMetadataText());
    }

    private String defaultMetadataText() {
        return resBundle.getString(METADATA_DEFAULT_TEXT_KEY);
    }
}
