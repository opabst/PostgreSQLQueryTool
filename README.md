# PostgreSQL Query Tool

A desktop GUI client for querying PostgreSQL databases. It lets you write and run SQL statements, inspect query plans, and browse database objects — schemas, tables, views, functions, sequences, and triggers — in a tree outline.

## Features

- **SQL editor** — write and execute arbitrary SQL; results are shown in a sortable table
- **EXPLAIN / EXPLAIN ANALYZE** — inspect query plans with a dedicated output tab
- **Database object outline** — lazily-loaded tree of all schemas and their objects
- **Connection manager** — save multiple named connections (host, port, database, user); passwords are never persisted to disk
- **Internationalisation** — UI available in English and German

## Requirements

| Tool | Minimum version |
|---|---|
| JDK | 23 (JDK 25 recommended) |
| Maven | 3.8 |
| PostgreSQL server | any version supported by JDBC driver 42.x |

JavaFX 25 is downloaded automatically by Maven. No separate JavaFX installation is required.

## Building

```bash
mvn clean package
```

This compiles the sources, runs the tests, and produces a shaded JAR in `target/`.
Maven automatically selects the correct JavaFX native classifier for your platform (`win`, `linux`, or `mac`).

## Testing

```bash
mvn test
```

Unit tests are located under `src/test/java` and use JUnit 5.

## Running

```bash
mvn javafx:run
```

Or, after `mvn clean package`, run the shaded JAR directly:

```bash
java -jar target/pqt-1.0-SNAPSHOT.jar
```

## Project structure

```
src/main/java/de/oliverpabst/pqt/
├── controller/        # Thin JavaFX controllers — bind UI to ViewModels
├── db/                # JDBC connection wrapper, ConnectionStore, MetadataManager
│   └── metadata/
│       └── model/     # Domain models: Schema, Table, View, Function, …
├── model/             # UI tree model (DBOutlineTreeItem)
├── service/           # Background JavaFX Services (QueryService, ConnectionTestService)
├── viewmodel/         # ViewModels (MainViewModel, WelcomeViewModel, ConnectionViewModel)
├── ImageProvider.java
├── PostgresQueryTool.java   # Application entry point
└── SettingsStore.java
```

## Technology stack

| Dependency | Version | Purpose |
|---|---|---|
| JavaFX | 25 | UI framework |
| PostgreSQL JDBC | 42.7.10 | Database connectivity |
| Jackson Databind | 2.18.3 | JSON persistence for connection profiles |
| SLF4J | 1.7.21 | Logging |
| JUnit 5 | 5.7.0 | Unit testing |

## Local test database (Docker)

A ready-to-use PostgreSQL 17 test database is provided via Docker Compose. It contains two schemas — `inv` (inventory) and `cust` (customers) — seeded with 5 000 products and 500 customers.

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (or Docker Engine + Compose plugin)

### Start

```bash
docker compose -f docker/docker-compose.yml up
```

Flyway runs all migrations automatically on first start and exits. PostgreSQL keeps running.

### Connect

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

Privileges are attached to the `role_*` roles; login users are simply assigned to the appropriate role. `inv_user` and `cust_user` have `SELECT`-only access on their respective schemas.

### Reset

```bash
docker compose -f docker/docker-compose.yml down -v
docker compose -f docker/docker-compose.yml up
```

The `-v` flag removes the named volume so the database is fully recreated.

### Credentials note

The file `docker/.env` contains plain-text passwords for local development only. It is excluded from version control via `.gitignore` and must **never** be committed.

## License

See [LICENSE](LICENSE).
