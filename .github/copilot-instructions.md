# Copilot Instructions for PostgreSQL Query Tool

## Project Overview
Desktop GUI application for querying PostgreSQL databases, built with Java 23 (source/target) and JavaFX 25. Uses Maven for builds. JavaFX 25 requires JDK 23 or later; the project is currently built with JDK 25.

## Architecture
Follows the **MVVM** (Model-View-ViewModel) pattern:

- **Views**: FXML files in `src/main/resources/de/oliverpabst/pqt/views/`
- **Controllers** (`de.oliverpabst.pqt.controller`): thin binders only — wire JavaFX property bindings between the View and the ViewModel; no business logic
- **ViewModels** (`de.oliverpabst.pqt.viewmodel`): own all UI state as JavaFX properties; expose command methods (`runQuery()`, `testConnection()`, etc.)
- **Services** (`de.oliverpabst.pqt.service`): background `Service<T>` / `Task<T>` subclasses that run JDBC work off the UI thread (`QueryService`, `ConnectionTestService`)
- **DB layer** (`de.oliverpabst.pqt.db`): JDBC wrappers (`DBConnection`), `ConnectionStore`, `MetadataManager`
- **Domain models** (`de.oliverpabst.pqt.db.metadata.model`): `Schema`, `Table`, `View`, `Function`, `Sequence`, `Trigger`, and nested table objects (`Column`, `Index`, `Constraint`)
- **UI tree model** (`de.oliverpabst.pqt.model`): `DBOutlineTreeItem`, `OutlineComponentType`
- **Result model** (`de.oliverpabst.pqt.model`): `QueryResult` record

### Wiring pattern
Each controller has a `setViewModel(XViewModel vm)` method that is called immediately after `FXMLLoader.load()`. All property bindings and listener registrations happen there, not in `initialize()`.

## Conventions
- **Package prefix**: `de.oliverpabst.pqt`
- **Singletons**: Application-wide state lives in `ImageProvider`, `ConnectionStore`, and `SettingsStore` — access via their `getInstance()` methods, do not pass them as constructor arguments.
- **UI strings**: Never hard-code user-visible strings. Add them to both `guistrings.properties` (English) and `guistrings_de.properties` (German), then reference via `ResourceBundle`.
- **Observable collections**: Use JavaFX `ObservableList` / `ObservableMap` for any data that drives a UI component.
- **Lazy loading**: Tree nodes must load children on-demand (override `isLeaf()` and populate in an expansion listener), not eagerly.
- **Passwords**: Never persist passwords. `DBConnection` holds a runtime-only password field populated by the user at connect time.
- **SQL parameters**: Always use the `executeQuery(sql, params, ResultSetHandler)` overload in `DBConnection` for metadata queries — never concatenate user input into SQL strings.
- **JDBC resources**: Use `ResultSetHandler<T>` callbacks so that `Statement` and `ResultSet` are closed inside `DBConnection`, not in callers.
- **Schema ordering**: `Schema` stores its children in `TreeMap` for deterministic alphabetical ordering.
- **Immutability first**: Mark variables and parameters as `final` whenever they are not reassigned.

## Best Practices

### Java
- Prefer small, focused classes with a single responsibility; move logic out of controllers and into ViewModels, services, or the DB layer.
- Prefer expressive types over raw collections; use generics consistently and avoid unchecked casts unless there is no better option.
- Prefer `final` fields, method parameters, and local variables whenever they are not reassigned.
- Use records for immutable data carriers when a type is purely structural and does not need JavaFX properties or framework-specific behavior.
- Keep public APIs narrow and intention-revealing; avoid exposing mutable internal collections directly unless a JavaFX observable type is part of the contract.
- Handle exceptions close to the boundary where they can be explained meaningfully to the user or logged with context.
- Use SLF4J parameterized logging; do not build log messages via string concatenation.

### Maven
- Keep dependency versions centralized in `pom.xml` properties where practical, and prefer one authoritative version per library family.
- Use Maven plugins to enforce build rules instead of ad-hoc shell scripts when the rule is part of normal project maintenance.
- Keep the build reproducible: avoid environment-specific assumptions beyond the documented JavaFX OS profiles.
- Prefer `mvn test` for fast validation and `mvn clean package` for full packaging checks.
- Keep UI tests separated from fast unit tests using tags and Maven configuration; do not make the default test run depend on a graphical desktop.
- When adding dependencies, ensure they work with Java 23+ and the selected JavaFX version.

### JavaFX / OpenJFX
- Keep all scene graph mutations on the JavaFX Application Thread.
- Run blocking I/O, JDBC work, and long-running metadata loads off the UI thread using `Service<T>`, `Task<T>`, or virtual threads when appropriate.
- Controllers should bind controls to ViewModel properties and delegate commands; they should not own business or persistence logic.
- Prefer property bindings over manual synchronization when UI state mirrors ViewModel state.
- Dispose listeners when windows close or controllers are torn down to avoid leaks.
- Keep FXML controllers lightweight and let `setViewModel(...)` perform the wiring immediately after loading.
- Reuse shared assets such as icons and localized strings through central providers and resource bundles.
- For tests, prefer headless-friendly TestFX configuration and keep UI tests explicitly tagged.

### PostgreSQL
- Qualify database object names with their schema when writing SQL that targets known application objects.
- Preserve deterministic ordering in metadata and UI lists; add explicit `ORDER BY` clauses for SQL results when ordering matters.
- Prefer PostgreSQL metadata from `information_schema` for portable catalog access unless PostgreSQL-specific catalogs are required.
- Keep DDL naming consistent with the Flyway conventions already used in `docker/flyway/sql/`.
- Design queries to fetch only the columns actually needed by the UI or metadata model.
- Never embed credentials in source code, SQL migrations, logs, or persisted connection profile data.

### JDBC
- Use `PreparedStatement` for every internal parameterized query; never concatenate user input into SQL.
- Keep connection lifecycle management inside `DBConnection`; callers should not manage pooled connections directly.
- Always close JDBC resources promptly via try-with-resources or existing wrapper abstractions.
- Use the pooled raw statement path only for user-authored SQL text that cannot be parameterized ahead of time.
- Map `NULL` handling deliberately when converting JDBC results into UI-friendly models.
- Log database failures with schema/query context when helpful, but never log passwords or other secrets.
- Keep metadata access methods side-effect aware and cache-friendly; do not bypass `MetadataManager` for repeated metadata retrieval.

## Key Dependencies
| Dependency | Version | Purpose |
|---|---|---|
| JavaFX | 25 | UI framework |
| PostgreSQL JDBC | 42.7.10 | Database connectivity |
| HikariCP | 5.1.0 | JDBC connection pooling |
| Jackson Databind | 2.18.3 | JSON persistence for connection profiles |
| SLF4J | 2.0.17 | Logging API |
| Logback | 1.5.18 | Logging backend |
| JUnit 5 | 5.12.2 | Unit testing |
| TestFX | 4.0.18 | JavaFX UI testing |

## Build
```
mvn clean package
```
Platform-specific JavaFX classifiers are selected automatically via Maven profiles (`win`, `linux`, `mac`).

Useful commands:

```
mvn test
mvn -Pui-tests test
mvn javafx:run
```

## Testing
Tests live in `src/test/java`. Use JUnit 5 annotations (`@Test`, `@BeforeEach`, etc.). Default test runs exclude UI tests tagged with `@Tag("ui")`; run them explicitly with the `ui-tests` Maven profile.

Run with:

```
mvn test
mvn -Pui-tests test
```

## Local test database

A Docker Compose setup lives under `docker/`. Start it with:

```
docker compose -f docker/docker-compose.yml up
```

PostgreSQL 17 listens on host port **5440** (`localhost:5440`). Flyway applies all migrations automatically on first start.

| Setting | Value |
|---|---|
| Host | `localhost` |
| Port | `5440` |
| Database | `pqt_shop` |
| Flyway / DDL user | `pqt_admin` / `pqt_admin_pw` |
| inv admin | `inv_admin` / `inv_admin_pw` (role: `role_inv_admin`) |
| inv read-only | `inv_user` / `inv_user_pw` (role: `role_inv_connect`) |
| cust admin | `cust_admin` / `cust_admin_pw` (role: `role_cust_admin`) |
| cust read-only | `cust_user` / `cust_user_pw` (role: `role_cust_connect`) |

Privileges are attached to the `role_*` roles; login users are simply assigned to the appropriate role.

The database has two schemas: `inv` (inventory — products, brands, categories, stock, price history, tags) and `cust` (customers — accounts, addresses, orders, order items, payment methods, reviews). Both are seeded with 5 000 products and 500 customers.

### Flyway SQL naming conventions

All DDL in `docker/flyway/sql/` follows these conventions:

| Object type | Pattern | Example |
|---|---|---|
| Sequence | `sq_${name}_id` | `inv.sq_products_id` |
| Index | `ix_${table}_${columns}` | `ix_products_category_id` |
| Primary Key | `pk_${table}_${columns}` | `pk_products_id` |
| Unique Constraint | `uc_${table}_${columns}` | `uc_products_sku` |
| Function | `fn_${purpose}` | `inv.fn_get_available_stock` |
| SQL keywords | UPPERCASE | `CREATE TABLE`, `SELECT` |

Additional rules:
- Primary keys, unique constraints, and indexes are declared **after** `CREATE TABLE` via explicit `ALTER TABLE … ADD CONSTRAINT` / `CREATE INDEX` statements — never inline.
- Every object reference is schema-qualified (`inv.products`, `cust.orders`, etc.).

## What to Avoid
- Do not add new global singletons; prefer passing dependencies explicitly in new code.
- Do not store passwords — not in `ConnectionStore`, not in JSON, not anywhere on disk.
- Do not bypass the `MetadataManager` cache for repeated metadata queries.
- Do not put business logic in controllers; delegate to ViewModels or the DB layer.
