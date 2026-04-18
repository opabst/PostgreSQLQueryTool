# Copilot Instructions for PostgreSQL Query Tool

## Project Overview
Desktop GUI application for querying PostgreSQL databases, built with Java 25 and JavaFX 25. Uses Maven for builds. JavaFX 25 requires JDK 23 or later; the project is currently built with JDK 25.

## Architecture
Follows an MVC pattern:
- **Views**: FXML files in `src/main/resources/de/oliverpabst/pqt/views/`
- **Controllers**: Java classes in `de.oliverpabst.pqt.controller`
- **DB layer**: `de.oliverpabst.pqt.db` — JDBC wrappers and metadata queries
- **Domain models**: `de.oliverpabst.pqt.db.metadata.model` — Schema, Table, View, Function, Sequence, Trigger, and nested table objects (Column, Index, Constraint)

## Conventions
- **Package prefix**: `de.oliverpabst.pqt`
- **Singletons**: Application-wide state lives in `ImageProvider`, `ConnectionStore`, and `SettingsStore` — access via their `getInstance()` methods, do not pass them as constructor arguments.
- **UI strings**: Never hard-code user-visible strings. Add them to both `guistrings.properties` (English) and `guistrings_de.properties` (German), then reference via `ResourceBundle`.
- **Observable collections**: Use JavaFX `ObservableList` / `ObservableMap` for any data that drives a UI component.
- **Lazy loading**: Tree nodes must load children on-demand (override `isLeaf()` and populate in an expansion listener), not eagerly.
- **FXML controllers**: Each controller is wired to its `.fxml` via `fx:controller`. Keep business logic out of controllers; delegate to the DB layer or model classes.

## Key Dependencies
| Dependency | Version | Purpose |
|---|---|---|
| JavaFX | 25 | UI framework |
| PostgreSQL JDBC | 42.2.18 | Database connectivity |
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

## What to Avoid
- Do not add new global singletons; prefer passing dependencies explicitly in new code.
- Do not store plain-text passwords beyond what `ConnectionStore` already does.
- Do not bypass the `MetadataManager` cache for repeated metadata queries.
