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

## Key Dependencies
| Dependency | Version | Purpose |
|---|---|---|
| JavaFX | 25 | UI framework |
| PostgreSQL JDBC | 42.7.10 | Database connectivity |
| Jackson Databind | 2.18.3 | JSON persistence for connection profiles |
| SLF4J | 1.7.21 | Logging |
| JUnit 5 | 5.7.0 | Unit testing |

## Build
```
mvn clean package
```
Platform-specific JavaFX classifiers are selected automatically via Maven profiles (`win`, `linux`, `mac`).

## Testing
Tests live in `src/test/java`. Use JUnit 5 annotations (`@Test`, `@BeforeEach`, etc.). Run with:
```
mvn test
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
