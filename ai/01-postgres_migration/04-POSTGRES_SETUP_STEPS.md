# PostgreSQL Setup Steps

These are one-time or per-environment steps required after the Flyway migration was introduced.
They are not automated — a developer must perform them manually.

---

## 1. Generate V1 Schema Migration (one-time)

Hibernate no longer manages the schema, so the initial schema must be captured as a Flyway
migration file. Do this once when first setting up the PostgreSQL migration.

Temporarily add these lines to `src/main/resources/application-dev.properties`:

```properties
spring.flyway.enabled=false
spring.jpa.hibernate.ddl-auto=create
spring.jpa.properties.javax.persistence.schema-generation.scripts.action=create
spring.jpa.properties.javax.persistence.schema-generation.scripts.create-target=V1__init_schema.sql
```

Flyway must be disabled here because Spring Boot runs Flyway before Hibernate's DDL auto.
Without this, Flyway tries to apply V2 (seed data) before the tables exist and fails.

Start PostgreSQL:
```bash
docker compose up postgres -d
```

Run the app. A file named `V1__init_schema.sql` will appear in the project root.

Move it to `src/main/resources/db/migration/`:
```bash
mv V1__init_schema.sql src/main/resources/db/migration/V1__init_schema.sql
```

Remove all four temporary lines from `application-dev.properties`.

---

## 2. Start PostgreSQL for Local Development

The `postgres` service in `docker/compose.yaml` automatically creates the `jucaneat` database
via the `POSTGRES_DB` environment variable. Simply run:

```bash
docker compose up postgres -d
```

To start only postgres without the full stack.

To start everything without building a docker image (time-consuming, long latency):
```bash
docker compose up postgres keycloak rabbitmq elasticSearch -d
```

Then run the Spring Boot app directly from your IDE or:
```bash
mvn spring-boot:run
```

Flyway will apply all pending migrations automatically on startup.

---

## 3. Deploy to Kubernetes (Staging / Production)

Credentials must be passed at deploy time via `--set` flags. Never commit real credentials
to `values.yaml` or any file tracked by git.

```bash
helm upgrade --install java-backend ./k8s/helm/java-backend \
  --set postgres.url=jdbc:postgresql://<host>:5432/jucaneat \
  --set postgres.username=<user> \
  --set postgres.password=<password>
```

For staging and production, the CI pipeline (GitHub Actions) runs Flyway migrations as an
explicit step before `helm upgrade`. The app starts with `spring.flyway.enabled=false` in the
`k8s` profile — the database is always migrated by CI, not by the pod itself.
