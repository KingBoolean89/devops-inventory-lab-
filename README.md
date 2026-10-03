# DevOps Inventory Lab

A simple inventory management REST API built with Java, Spring Boot, Maven, MySQL, and Flyway.

## Configuration

The application is configured through environment variables:

| Variable | Required | Default | Description |
| --- | --- | --- | --- |
| `DB_URL` | yes | none | JDBC URL for MySQL |
| `DB_USERNAME` | yes | none | Database username |
| `DB_PASSWORD` | yes | none | Database password |
| `BASIC_AUTH_USERNAME` | yes | none | HTTP basic auth username |
| `BASIC_AUTH_PASSWORD` | yes | none | HTTP basic auth password |
| `SERVER_PORT` | no | `8080` | HTTP port |
| `APP_NAME` | no | `devops-inventory-lab` | Spring application name |
| `DB_DRIVER` | no | `com.mysql.cj.jdbc.Driver` | JDBC driver |
| `FLYWAY_ENABLED` | no | `true` | Enable Flyway migrations |
| `FLYWAY_LOCATIONS` | no | `classpath:db/migration` | Flyway migration locations |
| `JPA_DDL_AUTO` | no | `validate` | Hibernate schema mode |
| `JPA_OPEN_IN_VIEW` | no | `false` | JPA open-in-view setting |
| `HIBERNATE_DIALECT` | no | `org.hibernate.dialect.MySQLDialect` | Hibernate dialect |
| `MANAGEMENT_ENDPOINTS` | no | `health` | Exposed actuator endpoints |
| `HEALTH_PROBES_ENABLED` | no | `true` | Enable liveness/readiness health groups |

## Run

```bash
export DB_URL='jdbc:mysql://localhost:3306/inventory'
export DB_USERNAME='inventory_user'
export DB_PASSWORD='inventory_password'
export BASIC_AUTH_USERNAME='admin'
export BASIC_AUTH_PASSWORD='change-me'

mvn spring-boot:run
```

## API

All API endpoints require basic authentication except `GET /actuator/health`.

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

```bash
mvn test
```
