# PostgreSQL Query Tool Backlog

**Planning horizon:** 6–12 months  
**Primary audience:** Developers and database engineers exploring, querying, and maintaining PostgreSQL databases  
**Product direction:** A fast, safe, inspectable PostgreSQL workbench that stays approachable for everyday SQL work

## Prioritisation

| Priority | Meaning | Planning intent |
|---|---|---|
| P0 | Practical product backlog | Core workflows that materially improve daily use; target these first |
| P1 | Demo / portfolio impact | Distinctive capabilities that make the application compelling to evaluate |
| P2 | Production readiness | Reliability, security, diagnostics, and distribution work |
| P3 | Broad ideation | Interesting possibilities to revisit after the core product is strong |

Effort is relative: `S` is a few days, `M` is roughly one to two weeks, `L` is multiple weeks, and `XL` is a substantial project. Estimates include implementation and focused tests, but not extensive polish or release administration.

## Recommended Delivery Sequence

### Phase 1: Make daily querying dependable

1. Query tabs and saved SQL documents
2. Query history and search
3. Query cancellation and execution state
4. Result export and better result navigation
5. Transaction controls and destructive-query safeguards
6. Metadata refresh and outline usability

### Phase 2: Make the database understandable

1. Table and object detail panes
2. Relationship and dependency navigation
3. Schema search and filtering
4. SQL generation from metadata
5. Query plan visualization and comparison
6. Explainable error messages and diagnostics

### Phase 3: Create portfolio-level differentiation

1. Schema diff and migration draft generation
2. Visual query plan explorer
3. Data editor with safe write workflow
4. PostgreSQL performance dashboard
5. Interactive SQL completion and diagnostics
6. Database documentation workspace

### Phase 4: Harden and distribute

1. Credential and secret handling review
2. Connection resilience and observability
3. Automated test database matrix
4. Packaging, update, and release automation
5. Accessibility and internationalisation completeness

## P0: Practical Product Backlog

### P0-01: Multi-tab query workspace

- **Value:** Keep several investigations open without losing SQL, results, or connection context.
- **Effort:** `L`
- **Dependencies:** Query execution state; connection lifecycle.
- **Likely ownership:** `MainViewModel`, query-tab model, query editor view/controller, `QueryService`.
- **Definition of done:** Users can create, close, rename, reorder, and duplicate tabs; each tab retains its SQL, selected connection, result state, and modified indicator; closing modified tabs prompts before discarding changes.
- **Acceptance notes:** A failed query affects only its tab. Switching tabs never cancels or replaces another tab's visible state unexpectedly.

### P0-02: SQL files and named query snippets

- **Value:** Turn the editor into a reusable workspace rather than a transient input box.
- **Effort:** `M`
- **Dependencies:** Multi-tab workspace; settings and file-dialog conventions.
- **Likely ownership:** Query document model and `MainViewModel`; persistence service.
- **Definition of done:** Users can open, save, save as, and reopen `.sql` files; unsaved changes are tracked; recent files are available; encoding and filesystem errors are actionable.
- **Acceptance notes:** Passwords and connection secrets never enter saved SQL files.

### P0-03: Query history with search and rerun

- **Value:** Recover useful work and understand what was run against a database.
- **Effort:** `M`
- **Dependencies:** Query execution result model; privacy settings.
- **Likely ownership:** History repository, `MainViewModel`, history view.
- **Definition of done:** Each execution records timestamp, connection profile name, duration, outcome, and SQL; users can search, copy, insert, rerun, and delete entries; history retention is configurable.
- **Acceptance notes:** History is clearly marked as local. It must not store passwords or result data by default.

### P0-04: Query cancellation and execution lifecycle

- **Value:** Prevent a mistaken or expensive query from blocking the workbench.
- **Effort:** `M`
- **Dependencies:** `QueryService`; JDBC cancellation support.
- **Likely ownership:** `QueryService`, query ViewModel, connection wrapper.
- **Definition of done:** Running queries expose running, cancelling, succeeded, failed, and cancelled states; users can request cancellation; the UI remains responsive; cancellation is logged distinctly from failure.
- **Acceptance notes:** Cancellation must not leave stale results presented as if they belong to the latest execution.

### P0-05: Result export and copy workflows

- **Value:** Move query results into reports, spreadsheets, scripts, and bug reports.
- **Effort:** `M`
- **Dependencies:** Result table model.
- **Likely ownership:** Result ViewModel and export service.
- **Definition of done:** Users can copy selected cells, copy rows as TSV/CSV, and export visible or complete results to CSV and JSON; NULL values and escaping are unambiguous; large exports show progress or a clear limitation.
- **Acceptance notes:** Export must not freeze the JavaFX application thread.

### P0-06: Result-table usability

- **Value:** Make large and wide results practical to inspect.
- **Effort:** `M`
- **Dependencies:** Existing sortable result table.
- **Likely ownership:** Result model and result view.
- **Definition of done:** Columns can be resized, reordered, hidden, and reset; NULL and binary values are distinguishable; long values can be inspected without destroying table layout; row count and truncation state are visible.

### P0-07: Transaction and write-safety controls

- **Value:** Let users work with write SQL without making accidental changes invisible.
- **Effort:** `L`
- **Dependencies:** Query execution lifecycle; connection/session state.
- **Likely ownership:** `DBConnection`, query ViewModel, transaction toolbar.
- **Definition of done:** The UI shows transaction status and supports commit, rollback, and rollback-on-close policy; write statements can require confirmation or a read-only connection mode; autocommit state is visible; destructive SQL warnings identify the target statement.
- **Acceptance notes:** Safety controls must be explicit and bypassable only through a deliberate user setting.

### P0-08: Metadata refresh and outline state

- **Value:** Keep the database tree trustworthy after DDL changes.
- **Effort:** `M`
- **Dependencies:** `MetadataManager` cache; `DBOutlineTreeItem` lazy loading.
- **Likely ownership:** `MetadataManager`, outline ViewModel/model.
- **Definition of done:** Users can refresh a schema, object group, or entire connection; refresh invalidates the correct cache entries; expanded state is preserved where possible; loading and failure states are visible.
- **Acceptance notes:** Refresh must not eagerly load every object in a large database.

### P0-09: Outline filtering and object search

- **Value:** Find one object quickly in databases with many schemas and tables.
- **Effort:** `M`
- **Dependencies:** Metadata cache; outline model.
- **Likely ownership:** Outline ViewModel and `DBOutlineTreeItem` or a filtered tree adapter.
- **Definition of done:** Users can search by schema, object, and object type; results identify the full qualified name; clearing the filter restores normal lazy browsing; search does not trigger unbounded metadata loading.

### P0-10: Table and object detail inspector

- **Value:** Inspect metadata without manually writing catalog queries.
- **Effort:** `L`
- **Dependencies:** Existing metadata models and outline navigation.
- **Likely ownership:** Metadata detail ViewModels and views; `MetadataManager` extensions.
- **Definition of done:** Selecting a table shows columns, types, nullability, defaults, keys, indexes, row estimates, and comments where available; functions and views show definitions and signatures; details can be copied as SQL or text.

### P0-11: Generate SQL from selected metadata

- **Value:** Accelerate exploration and reduce repetitive catalog-query work.
- **Effort:** `M`
- **Dependencies:** Object detail inspector; SQL editor insertion API.
- **Likely ownership:** SQL generation service and query ViewModel.
- **Definition of done:** Users can generate `SELECT`, `INSERT` template, `CREATE`, and metadata inspection SQL for supported objects; generated identifiers are correctly quoted; generated SQL opens in a new tab or inserts at the cursor by explicit choice.

### P0-12: Connection session dashboard

- **Value:** Make the active database, user, transaction, and server context obvious.
- **Effort:** `M`
- **Dependencies:** Connection manager and metadata connection.
- **Likely ownership:** Connection/session ViewModel.
- **Definition of done:** The active session shows host, port, database, user, PostgreSQL version, server time, read-only state, and connection health; profile names are not treated as authoritative server identity.

## P1: Demo and Portfolio Impact

### P1-01: Interactive visual query-plan explorer

- **Value:** Provide a memorable, genuinely useful view of `EXPLAIN (ANALYZE, BUFFERS, FORMAT JSON)` output.
- **Effort:** `XL`
- **Dependencies:** Query plan parser; query execution safety; result tab framework.
- **Likely ownership:** Plan parser/model, plan ViewModel, JavaFX visualization view.
- **Definition of done:** Users can open a plan as a navigable operator tree; each node shows cost, actual time, rows, loops, buffers, and warnings; expensive or suspicious nodes are visually discoverable; raw text and JSON remain available.
- **Acceptance notes:** The visualization must degrade gracefully for plans without `ANALYZE` data or for unsupported PostgreSQL plan nodes.

### P1-02: Query-plan comparison

- **Value:** Make index, rewrite, and configuration experiments easy to evaluate.
- **Effort:** `L`
- **Dependencies:** Plan explorer; stable plan model.
- **Likely ownership:** Plan comparison model and ViewModel.
- **Definition of done:** Users can compare two saved plans side by side or as a diff; changed costs, row estimates, timing, and node structure are highlighted; plans include connection and execution-option context.

### P1-03: Schema diff and migration draft generator

- **Value:** Turn database inspection into a practical migration workflow and a strong portfolio differentiator.
- **Effort:** `XL`
- **Dependencies:** Complete metadata model; object definition retrieval; SQL generation.
- **Likely ownership:** Schema snapshot/diff service, migration ViewModel, SQL preview view.
- **Definition of done:** Users can compare two connections or snapshots; additions, removals, and changes are classified; generated SQL is reviewable, editable, ordered, and exportable; destructive operations are clearly marked and never executed automatically.

### P1-04: Safe table data editor

- **Value:** Support small corrections and data exploration without leaving the application.
- **Effort:** `XL`
- **Dependencies:** Table detail inspector; transaction controls; primary-key and permission detection.
- **Likely ownership:** Data editor ViewModel/service and table view.
- **Definition of done:** Users can page through rows, edit supported scalar values, insert and delete rows, preview generated SQL, and commit or rollback changes; tables without a safe key are read-only; permissions and affected-row counts are shown.

### P1-05: SQL completion and inline diagnostics

- **Value:** Reduce syntax and identifier errors while making the editor feel like a serious tool.
- **Effort:** `XL`
- **Dependencies:** SQL editor abstraction; metadata cache; PostgreSQL grammar strategy.
- **Likely ownership:** Editor service, completion provider, diagnostics ViewModel.
- **Definition of done:** Completion understands keywords, schemas, tables, columns, functions, aliases, and quoting; syntax errors are underlined where reliably detectable; completion remains responsive while metadata loads asynchronously.

### P1-06: PostgreSQL performance dashboard

- **Value:** Provide a guided overview of server activity and database health.
- **Effort:** `XL`
- **Dependencies:** Permissions-aware diagnostics queries; cancellable background services.
- **Likely ownership:** Diagnostics service and dashboard ViewModel.
- **Definition of done:** The dashboard can show active sessions, long-running queries, locks, database sizes, cache indicators, and vacuum/analyze status where permissions allow; every metric identifies its source query and collection time.
- **Acceptance notes:** The dashboard must distinguish unavailable data from healthy zero values.

### P1-07: Database documentation workspace

- **Value:** Make the tool useful for onboarding and communicating database design.
- **Effort:** `L`
- **Dependencies:** Object detail inspector; comments and relationship metadata.
- **Likely ownership:** Documentation model, export service, documentation view.
- **Definition of done:** Users can add local notes without modifying the database, browse a generated schema catalog, and export Markdown or HTML containing tables, columns, relationships, comments, and selected SQL definitions.

### P1-08: Relationship and dependency graph

- **Value:** Reveal how schemas, tables, views, functions, and triggers connect.
- **Effort:** `L`
- **Dependencies:** Metadata dependency queries; documentation or inspector model.
- **Likely ownership:** Graph model/ViewModel and JavaFX visualization.
- **Definition of done:** Users can open a selected object's inbound and outbound dependencies, filter by object type, navigate from nodes to the outline, and handle cycles and incomplete permissions without freezing.

### P1-09: Reproducible query investigation bundle

- **Value:** Make performance findings and bug reports shareable.
- **Effort:** `M`
- **Dependencies:** Query tabs; history; plan model; export service.
- **Likely ownership:** Workspace export/import service.
- **Definition of done:** Users can export a bundle containing SQL, sanitized connection context, plans, selected results, and notes; import opens it as a clearly isolated workspace; credentials and sensitive result data require explicit inclusion.

### P1-10: PostgreSQL feature capability profile

- **Value:** Make version-dependent behavior understandable.
- **Effort:** `M`
- **Dependencies:** Session dashboard; server version detection.
- **Likely ownership:** Capability service and UI feature gates.
- **Definition of done:** The app detects PostgreSQL server version and permissions, explains unavailable actions, and avoids presenting unsupported metadata or diagnostics as universally available.

## P2: Production Readiness

### P2-01: Credential and secret-handling review

- **Value:** Reduce the risk of exposing database credentials.
- **Effort:** `L`
- **Dependencies:** Connection persistence and runtime password flow.
- **Likely ownership:** `ConnectionStore`, settings, connection dialog.
- **Definition of done:** Passwords remain runtime-only by default; optional OS keychain integration is evaluated or implemented; logs, history, bundles, crash output, and generated files are checked for secrets; sensitive fields are cleared when no longer needed.

### P2-02: Connection resilience and reconnect workflow

- **Value:** Recover cleanly from network interruptions, sleep, database restarts, and idle disconnects.
- **Effort:** `L`
- **Dependencies:** Connection lifecycle; query cancellation.
- **Likely ownership:** `DBConnection`, connection ViewModel, services.
- **Definition of done:** Connection loss has a distinct state and actionable message; reconnect can be requested without restarting the application; in-flight queries fail clearly; stale metadata is not silently presented as current.

### P2-03: Permission-aware feature gating

- **Value:** Make the application safe and useful with read-only users.
- **Effort:** `M`
- **Dependencies:** Session dashboard; diagnostics queries.
- **Likely ownership:** Capability service and ViewModels.
- **Definition of done:** The UI detects relevant permissions where feasible, disables or explains unsupported actions, and still allows raw SQL where the user has permission; permission errors include the operation and object involved.

### P2-04: Structured logging and support diagnostics

- **Value:** Diagnose failures without requiring users to reproduce everything from a terminal.
- **Effort:** `M`
- **Dependencies:** Existing SLF4J/Logback setup.
- **Likely ownership:** Application-wide logging conventions; support export.
- **Definition of done:** Logs include correlation identifiers for query executions and connection attempts, never include secrets, and can be exported with user-controlled redaction; UI errors point to useful logs without exposing stack traces by default.

### P2-05: Automated integration-test matrix

- **Value:** Prevent PostgreSQL-version and permission regressions.
- **Effort:** `L`
- **Dependencies:** Docker test database.
- **Likely ownership:** Maven test profiles and database fixtures.
- **Definition of done:** Tests cover metadata, permissions, cancellation, transactions, and representative PostgreSQL versions or documented limits; UI tests remain separated from fast tests; test data is deterministic and resettable.

### P2-06: Performance and scalability budget

- **Value:** Keep the tool responsive against large databases and result sets.
- **Effort:** `L`
- **Dependencies:** Metadata refresh and result-table improvements.
- **Likely ownership:** Metadata services, result model, UI tests/benchmarks.
- **Definition of done:** A documented benchmark covers large schemas, many objects, and large results; metadata loads are cancellable/backgrounded; table rendering and filtering have measurable limits; regressions are detectable in CI where practical.

### P2-07: Accessibility and keyboard-first workflows

- **Value:** Make repeated database work faster and the application usable with assistive technology.
- **Effort:** `L`
- **Dependencies:** Stable view structure and command actions.
- **Likely ownership:** FXML views, controllers, ViewModels, keyboard command layer.
- **Definition of done:** Core actions have discoverable keyboard shortcuts; focus order is logical; controls have accessible text; dialogs work without mouse-only interaction; contrast and UI state indicators are reviewed.

### P2-08: Internationalisation completeness

- **Value:** Preserve the existing English/German product promise as features grow.
- **Effort:** `M`
- **Dependencies:** New feature work.
- **Likely ownership:** Resource bundles and views.
- **Definition of done:** No new user-visible strings are hard-coded; English and German bundles cover normal, error, loading, confirmation, and empty states; formatting handles identifiers and SQL without accidental translation.

### P2-09: Packaging and release automation

- **Value:** Make the application easy to install and evaluate.
- **Effort:** `L`
- **Dependencies:** Stable application startup and configuration.
- **Likely ownership:** Maven/build metadata and CI.
- **Definition of done:** Linux, macOS, and Windows packaging options are documented or automated; version metadata is consistent; a clean machine can install and launch a release artifact; release notes identify compatibility and known limitations.

### P2-10: Error, loading, and empty-state design pass

- **Value:** Replace ambiguous failures with recoverable workflows.
- **Effort:** `M`
- **Dependencies:** Feature-specific states.
- **Likely ownership:** ViewModels and FXML views.
- **Definition of done:** Every background operation has loading, success, empty, cancelled, and error behavior appropriate to its context; retry and refresh actions are available where meaningful; errors include safe diagnostic detail.

## P3: Broad Ideation

These ideas are deliberately deferred until the core querying and inspection workflows are reliable.

| Idea | Potential value | Effort | Main dependency or risk |
|---|---|---:|---|
| SSH tunnel and bastion connection support | Reach protected environments without a separate tunnel tool | XL | Credential storage, platform behavior, security review |
| Cloud provider connection helpers | Reduce setup friction for managed PostgreSQL | L | Provider-specific authentication and API churn |
| PostgreSQL log viewer and log interpretation | Correlate server events with query behavior | L | Log access, permissions, deployment differences |
| Extension catalog browser | Explain installed extensions and available versions | M | Server permissions and extension metadata differences |
| Notification and alert rules | Surface long queries, locks, or disk pressure | L | Background polling, noise control, lifecycle |
| SQL linting rules and style profiles | Improve consistency in shared SQL | L | Parser quality and configurable rules |
| Notebook-style investigation documents | Combine SQL, results, plans, and prose | XL | Document model, rendering, persistence |
| Team workspace and review comments | Collaboratively review queries and migrations | XL | Sync, identity, conflict resolution, hosting |
| Database seed-data generator | Create realistic local fixtures from metadata | XL | Privacy, data quality, and destructive-operation risk |
| REST/GraphQL API explorer for PostgreSQL-backed services | Connect schema exploration to application APIs | XL | Scope drift away from PostgreSQL core |
| Extension/plugin API | Let users add exporters, diagnostics, or visualizations | XL | Long-term compatibility and security boundary |
| Dark/light/custom theme system | Improve personalisation and presentation | M | Accessibility, visual consistency, maintenance |
| Embedded PostgreSQL sandbox | Give demos a zero-setup local database | XL | Packaging, native binaries, lifecycle, data persistence |
| Query cost budget enforcement | Prevent accidental expensive queries | L | False positives, permissions, transaction semantics |
| AI-assisted SQL explanations | Explain plans and suggest query improvements | XL | Privacy, correctness, provider dependency, trust |

## Cross-Cutting Acceptance Standards

Every feature should satisfy the following unless explicitly exempted:

- Blocking JDBC work and large transformations run off the JavaFX Application Thread.
- UI state is owned by ViewModels; controllers remain binders rather than business-logic holders.
- Metadata queries use parameterized `DBConnection` APIs and preserve resource ownership in the DB layer.
- Passwords and other secrets are not persisted, logged, exported, or placed in history by accident.
- User-visible text is present in both language resource bundles.
- Long-running work is cancellable or has a documented reason why cancellation is unavailable.
- Tests cover the normal path and the most important failure or permission path.
- Destructive operations have preview, transaction, read-only, or confirmation safeguards appropriate to their risk.
- Large databases and result sets have an explicit loading, pagination, truncation, or cancellation strategy.

## Suggested First Milestone

The first milestone should be a coherent daily-use slice rather than a collection of unrelated demos:

1. Multi-tab query workspace
2. Query history
3. Query cancellation
4. Result export
5. Transaction and write-safety controls
6. Metadata refresh and outline search

A milestone is complete when a user can open a connection, run several independent investigations, recover a previous query, stop a bad query, export a result, refresh changed metadata, and safely leave an uncommitted write transaction.

## Open Product Decisions

These decisions should be made before the relevant epics begin:

- Is the default connection mode read-only, or should it mirror the server/session default?
- Should query history be per connection profile, global, or both?
- What maximum result size should be rendered in memory, and how should users request larger exports?
- Should saved workspaces be portable across machines, with secrets always excluded?
- Which PostgreSQL versions are officially supported and tested?
- Is schema diff intended as review-only SQL generation, or eventually as an execution workflow?
- Which permissions should be required before exposing performance dashboards?
- Is the first release distributed as a developer build, an installer, or both?
