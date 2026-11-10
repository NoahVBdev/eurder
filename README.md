# Eurder

REST back end for ordering items: customers register, admins manage items, customers place orders.
Built with Spring Boot 4, JPA (Hibernate), PostgreSQL and Flyway.

**Implemented stories:** register a customer (1), add an item (2), order items (3),
view all customers (7), view a single customer (8).

## Run it

**Prerequisites:** Java 21+ and access to a PostgreSQL database. No Maven install needed (`./mvnw`).

Set these environment variables, e.g. in your IntelliJ run configuration:

| Variable | Required | Example / format |
|---|---|---|
| `DB_URL` | yes | `jdbc:postgresql://<host>:<port>/<database>` |
| `DB_USERNAME` | yes | |
| `DB_PASSWORD` | yes | |
| `EURDER_ADMIN_PASSWORD` | no | defaults to `admin-dev-password`. **Always set it outside your own machine.** |

Then start `EurderApplication`, or run `./mvnw spring-boot:run`.
On startup, Flyway creates or updates the `eurder` schema.

- API docs: http://localhost:8080/swagger-ui.html
- Auth: HTTP Basic. The admin is `admin@eurder.com` with `EURDER_ADMIN_PASSWORD`. Customers log in with the email and password they registered with.

## Profiles

| Profile | Use |
|---|---|
| *(none)* | Normal run, `INFO` logging |
| `dev` | Debug logging and SQL statements with their parameters |
| `test` | Activated automatically in integration tests: in-memory H2 and fixed admin credentials |

## Tests

```
./mvnw verify
```

Runs everything against an in-memory H2 database (PostgreSQL mode), so no database or environment variables are needed.

## Database

- Migrations live in `src/main/resources/db/migration` and run automatically on startup.
- **Never edit a migration that has already run.** Every change is a new `V<n>__<description>.sql`.
- Hibernate only validates the schema (`ddl-auto=validate`), it never changes it.

## Design decisions

- **IDs** are UUIDs generated in Java, so entities have a stable identity before they are saved (`BaseEntity`).
- **Emails** are stored and compared in lowercase. Uniqueness is guarded by the database (`uq_customer_email`).
- **Orders keep snapshots** of the item name, the price and the shipping address at the time of ordering ([#23](https://github.com/NoahVBdev/eurder/issues/23)).
- **Concurrent stock changes** are detected with optimistic locking (`@Version` on `Item`) and return `409 Conflict` ([#24](https://github.com/NoahVBdev/eurder/issues/24)).
- **Errors** are returned as RFC 9457 problem details, without internal details.