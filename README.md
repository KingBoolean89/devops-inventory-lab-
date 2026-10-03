# DevOps Inventory Lab

A simple inventory management REST API built with Java, Spring Boot, Maven, MySQL, and Flyway.

## Prerequisites

- JDK 21 or later and Maven.
- A running MySQL server and an existing database with a user authorized to create and access its tables.

The executable JAR includes the JDBC driver and Flyway migrations. H2 is a test-only dependency, not a runtime database fallback.

## Configuration

All application configuration comes from environment variables. No configuration file edits are needed. The following tables cover every variable referenced by the application's configuration. Standard Spring Boot environment overrides are also available, for example `LOGGING_LEVEL_ROOT` (default `INFO`).

### Required Variables

These five variables have no defaults. Set all five before starting the JAR. Database and API credentials are independent; the API has one configured Basic Auth user with the `USER` role. Supply the API password as plain text in the environment; the application hashes it with BCrypt in memory.

| Variable | Default | Example local-development value | Purpose |
| --- | --- | --- | --- |
| `DB_URL` | None | `jdbc:mysql://127.0.0.1:3306/inventory?allowPublicKeyRetrieval=true&sslMode=DISABLED` | MySQL JDBC URL, including the database name |
| `DB_USERNAME` | None | `inventory_user` | MySQL user |
| `DB_PASSWORD` | None | `dev-db-change-me` | MySQL password |
| `BASIC_AUTH_USERNAME` | None | `inventory_dev` | API username; must not be empty |
| `BASIC_AUTH_PASSWORD` | None | `dev-api-change-me` | API password; use a nonempty value |

The example passwords are disposable local-development values, not production credentials. Credentials deliberately have no automatic fallback. The example URL disables TLS and permits public-key retrieval only for local development; configure the JDBC URL for your database's TLS requirements elsewhere. Basic Auth should use HTTPS outside local development.

### Optional Variables

Omit these to use the defaults. The default values are also suitable local-development examples; change `SERVER_PORT` if `8080` is occupied.

| Variable | Default / local example | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8080` | HTTP port |
| `APP_NAME` | `devops-inventory-lab` | Spring application name |
| `DB_DRIVER` | `com.mysql.cj.jdbc.Driver` | JDBC driver class |
| `FLYWAY_ENABLED` | `true` | Run and validate migrations at startup |
| `FLYWAY_LOCATIONS` | `classpath:db/migration` | Migration locations; comma-separated if multiple |
| `JPA_DDL_AUTO` | `validate` | Validate the schema after Flyway; Hibernate does not create it |
| `JPA_OPEN_IN_VIEW` | `false` | Keep persistence contexts scoped to transactions |
| `HIBERNATE_DIALECT` | `org.hibernate.dialect.MySQLDialect` | SQL dialect |
| `MANAGEMENT_ENDPOINTS` | `health` | Actuator endpoints exposed over HTTP; comma-separated |
| `HEALTH_PROBES_ENABLED` | `true` | Enable `/actuator/health/liveness` and `/actuator/health/readiness` |

Keep Flyway enabled and Hibernate in `validate` mode for the normal MySQL workflow. Flyway creates tables, not the database or MySQL user. Its user needs DDL and DML privileges in the application database. The default health endpoint includes a database connectivity check; the liveness/readiness groups report application availability separately.

## Local Packaged-JAR Startup

First build in a terminal without runtime database overrides:

```bash
mvn clean package
```

In an existing local MySQL server, run this SQL as an administrator. The examples below use only a local development database and credentials:

```sql
CREATE DATABASE inventory;
CREATE USER 'inventory_user'@'127.0.0.1' IDENTIFIED BY 'dev-db-change-me';
GRANT ALL PRIVILEGES ON inventory.* TO 'inventory_user'@'127.0.0.1';
```

Then set the complete runtime environment and start the packaged JAR:

```bash
export DB_URL='jdbc:mysql://127.0.0.1:3306/inventory?allowPublicKeyRetrieval=true&sslMode=DISABLED'
export DB_USERNAME='inventory_user'
export DB_PASSWORD='dev-db-change-me'
export BASIC_AUTH_USERNAME='inventory_dev'
export BASIC_AUTH_PASSWORD='dev-api-change-me'
export SERVER_PORT='8080'

java -jar target/devops-inventory-lab-0.0.1-SNAPSHOT.jar
```

On a new database, startup logs should show Flyway applying V1, Hibernate initializing, Tomcat starting on the configured port, and `Started InventoryApplication`. On later starts, Flyway validates the existing migration and reports the schema up to date. Application records survive restarts because they are stored in MySQL.

Missing required variables cause startup to fail. Connection errors indicate the MySQL host, port, database, or credentials need checking. Set both `BASIC_AUTH_USERNAME` and `BASIC_AUTH_PASSWORD` even when database initialization already succeeds.

## Runtime Smoke Checks

In a second terminal, set the same API credentials and port. The following requests assume a fresh database where the first category and product have ID `1`; otherwise substitute the IDs returned by the create responses.

```bash
export BASIC_AUTH_USERNAME='inventory_dev'
export BASIC_AUTH_PASSWORD='dev-api-change-me'
export SERVER_PORT='8080'

# Public health: 200 and status UP.
curl -i "http://127.0.0.1:${SERVER_PORT}/actuator/health"

# Missing credentials: 401 with a WWW-Authenticate header.
curl -i "http://127.0.0.1:${SERVER_PORT}/api/categories"

# Wrong password: 401.
curl -i -u "${BASIC_AUTH_USERNAME}:wrong-password" \
  "http://127.0.0.1:${SERVER_PORT}/api/categories"

# Category: POST 201, GET 200.
curl -i -u "${BASIC_AUTH_USERNAME}:${BASIC_AUTH_PASSWORD}" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Hardware","description":"Local development"}' \
  "http://127.0.0.1:${SERVER_PORT}/api/categories"
curl -i -u "${BASIC_AUTH_USERNAME}:${BASIC_AUTH_PASSWORD}" \
  "http://127.0.0.1:${SERVER_PORT}/api/categories/1"

# Product: POST 201, GET 200.
curl -i -u "${BASIC_AUTH_USERNAME}:${BASIC_AUTH_PASSWORD}" \
  -H 'Content-Type: application/json' \
  -d '{"sku":"HAMMER-001","name":"Claw Hammer","price":14.99,"categoryId":1}' \
  "http://127.0.0.1:${SERVER_PORT}/api/products"
curl -i -u "${BASIC_AUTH_USERNAME}:${BASIC_AUTH_PASSWORD}" \
  "http://127.0.0.1:${SERVER_PORT}/api/products/1"

# Inventory: first PUT 201, subsequent PUT 200, GET returns updated quantity 25.
curl -i -u "${BASIC_AUTH_USERNAME}:${BASIC_AUTH_PASSWORD}" \
  -X PUT -H 'Content-Type: application/json' \
  -d '{"quantity":42,"reorderLevel":10}' \
  "http://127.0.0.1:${SERVER_PORT}/api/inventory/1"
curl -i -u "${BASIC_AUTH_USERNAME}:${BASIC_AUTH_PASSWORD}" \
  -X PUT -H 'Content-Type: application/json' \
  -d '{"quantity":25,"reorderLevel":5}' \
  "http://127.0.0.1:${SERVER_PORT}/api/inventory/1"
curl -i -u "${BASIC_AUTH_USERNAME}:${BASIC_AUTH_PASSWORD}" \
  "http://127.0.0.1:${SERVER_PORT}/api/inventory/1"
```

## API

All `/api/**` endpoints require Basic Auth. `/actuator/health` and its health groups are public. POST returns `201` with a `Location` header; PUT returns `200`, except inventory creation returns `201`. DELETE returns `204`. Missing records return `404`, invalid field values return `400`, and uniqueness/foreign-key conflicts return `409`.

- `GET /api/categories`
- `POST /api/categories`
- `GET /api/categories/{id}`
- `PUT /api/categories/{id}`
- `DELETE /api/categories/{id}`
- `GET /api/products`
- `POST /api/products`
- `GET /api/products/{id}`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`
- `GET /api/inventory`
- `GET /api/inventory/{productId}`
- `PUT /api/inventory/{productId}`
- `DELETE /api/inventory/{productId}`

## Tests

The test profile defaults to an isolated H2 database in MySQL compatibility mode with Flyway enabled and test-only API credentials (`test-user` / `test-password`). These defaults are not present in the packaged application. Integration tests cover real Basic Auth, health, product updates, and inventory creation/update.

Runtime environment variables override test defaults too. To run the full build from a terminal where runtime variables were exported, clear the configuration overrides for that command:

```bash
env -u DB_URL -u DB_USERNAME -u DB_PASSWORD -u DB_DRIVER \
  -u HIBERNATE_DIALECT -u FLYWAY_ENABLED -u FLYWAY_LOCATIONS \
  -u JPA_DDL_AUTO -u JPA_OPEN_IN_VIEW \
  -u BASIC_AUTH_USERNAME -u BASIC_AUTH_PASSWORD mvn clean package
```

In a clean terminal, simply run `mvn clean package`. No tests are skipped. MySQL runtime verification is separate from these H2-backed tests: start the packaged JAR and run the smoke checks above against MySQL.
