---
applyTo: "src/main/java/**/db/**"
---

## PostgreSQL

- Qualify database object names with their schema when writing SQL that targets known application objects.
- Preserve deterministic ordering in metadata and UI lists; add explicit `ORDER BY` clauses for SQL results when ordering matters.
- Prefer PostgreSQL metadata from `information_schema` for portable catalog access unless PostgreSQL-specific catalogs are required.
- Keep DDL naming consistent with the Flyway conventions in `docker/flyway/sql/`.
- Design queries to fetch only the columns actually needed by the UI or metadata model.

## JDBC

- Use `PreparedStatement` for every internal parameterized query; never concatenate user input into SQL.
- Keep connection lifecycle management inside `DBConnection`; callers should not manage pooled connections directly.
- Always close JDBC resources promptly via try-with-resources or existing wrapper abstractions.
- Use the pooled raw statement path only for user-authored SQL text that cannot be parameterized ahead of time.
- Map `NULL` handling deliberately when converting JDBC results into UI-friendly models.
- Log database failures with schema/query context when helpful, but never log passwords or other secrets.
- Keep metadata access methods cache-friendly; do not bypass `MetadataManager` for repeated metadata retrieval.
