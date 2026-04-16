# Developer Workflow

## Manual Testing (Frontend + Backend)

**Start infrastructure once:**
```bash
docker compose up postgres keycloak rabbitmq elasticSearch -d
```
Then run the backend from your IDE or terminal (`dev` profile). The app connects to all services via `localhost`. Flyway runs automatically on startup and applies any pending migrations.

**What you need to worry about:**
- Docker must be running
- If you changed the schema, write a new migration file first (see [Schema Changes](#schema-changes)) — the app won't start if `ddl-auto=validate` detects a mismatch

**What is taken care of:**
- Schema is created and kept up to date by Flyway at every app startup
- Seed data (restaurants, dishes, etc.) is loaded once by Flyway and persists across restarts
- No Docker image rebuild needed — edit code, restart app, done

---

## Unit & Integration Tests

```bash
mvn test
```

Tests currently use an **H2 in-memory database** with Flyway disabled. Hibernate manages the schema directly via `ddl-auto=create-drop`, creating a fresh schema at the start of each test run and dropping it at the end. This is configured in `src/test/resources/application.properties`.

**What you need to worry about:**
- Nothing — no external database or Docker required to run tests

**What is taken care of:**
- Tests are fully isolated — each run starts from a clean schema
- CI runs these same tests identically without any database infrastructure

> **Planned:** Once Flyway is confirmed working end-to-end, the test setup will be migrated to **Testcontainers**, which spins up a real PostgreSQL Docker container automatically for the test run and tears it down when done. Flyway migrations will run inside that container, so tests will always run against the real schema. When that is done, Docker must be running to execute tests, and the `src/test/resources/application.properties` override will be removed.

---

## Schema Changes

Whenever you modify an entity (add a column, rename a field, add a table, etc.):

1. **Create a new migration file** in `src/main/resources/db/migration/`:
   ```
   V{next_number}__{short_description}.sql
   ```
   Example: `V3__add_user_preferences_table.sql`

2. **Never edit an existing migration file.** Flyway checksums every applied migration — modifying one will cause a checksum mismatch and the app (and tests) will refuse to start.

3. Restart the app locally — Flyway applies the new migration automatically.

Everything downstream (tests, staging, production) picks up the migration without additional steps.

---

## Deploying to Staging

Triggered automatically on merge to the `staging` branch (or equivalent) via GitHub Actions:

1. Build & test (`mvn verify` — includes Testcontainers tests)
2. Build and push Docker image
3. **Run `mvn flyway:migrate` against the staging database** using secrets stored in GitHub — migration runs and is verified *before* the app is deployed
4. `helm upgrade` rolls out the new image to AKS

**What you need to worry about:**
- Your migration SQL must be correct before merging — if `flyway:migrate` fails, the pipeline stops and the old version keeps running untouched

**What is taken care of:**
- Database is migrated before any pod restarts
- No manual database access required
- Rollback: if migration fails, deployment does not proceed

---

## Deploying to Production

Same pipeline as staging, triggered on merge to `main` (or a release tag), using production secrets.

**What you need to worry about:**
- Migration was already validated on staging — verify staging is healthy before promoting to production
- Migrations are **irreversible by default** — dropping a column or table cannot be undone automatically; plan destructive changes carefully

**What is taken care of:**
- CI runs the migration against the production database before deploying
- No developer needs direct production database access for routine deployments
- Zero-downtime is achieved by ensuring migrations are backwards-compatible (old pods can still run while new pods start)
