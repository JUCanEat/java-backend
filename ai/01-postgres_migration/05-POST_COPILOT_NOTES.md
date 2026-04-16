# Plan: Address Copilot Review + Add Backend Postgres to external/charts

## Context

We migrated the java-backend from H2 to PostgreSQL with Flyway (PR #61, branch `proper-db`).
Copilot reviewed the PR and found 12 issues. A structural problem was also discovered:
the Helm chart changes we made live in `k8s/`, but **`external/charts` (JUCanEat/Charts submodule)
is the authoritative umbrella chart for AKS deployment**. The `k8s/` folder is legacy/local-dev config.

Postgres is expected to run **inside the cluster** (not Azure managed), matching the pattern used
for Keycloak's Postgres already in external/charts.

## Is k8s/ relevant?

**No, not for AKS.** `external/charts/charts/backend/` is the correct location for the backend
Helm config. The `k8s/` folder uses NodePort services, nip.io domains, and hardcoded creds —
it is local/minikube-only. Leave it as-is; add the correct changes to `external/charts`.

---

## Proposed Changes

### 1. Add Backend Postgres to `external/charts` (umbrella chart level)

**New file:** `external/charts/templates/backend-postgres.yaml`

Follows the same pattern as `templates/keycloak/keycloak.yaml` (lines 184–260):
- `PersistentVolumeClaim`: `backend-postgres-pvc` (2Gi, ReadWriteOnce)
- `Deployment`: named `backend-postgres`, image `mirror.gcr.io/postgres:17`
  - `POSTGRES_USER` / `POSTGRES_PASSWORD` from secret `backend-postgres-secret`
  - `POSTGRES_DB=jucaneat`
  - `PGDATA=/var/lib/postgresql/data/pgdata`
  - PVC mounted at `/var/lib/postgresql/data/pgdata`
- `Service`: ClusterIP, name `backend-postgres`, port 5432

Secret (`backend-postgres-secret`) holds `username` and `password`.
It will be created by the backend subchart's secret template (see §2) using values injected at deploy time.

---

### 2. Add Postgres Secret + Env Vars to `external/charts/charts/backend/`

**New file:** `external/charts/charts/backend/templates/postgres-secret.yaml`
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: backend-postgres-secret
type: Opaque
stringData:
  username: {{ required "postgres.username is required" .Values.postgres.username }}
  password: {{ required "postgres.password is required" .Values.postgres.password }}
```
Name `backend-postgres-secret` is fixed (not release-scoped) because the postgres Deployment in
the umbrella chart references it by this name. This is consistent with how `postgres-secret` works
for Keycloak.

**Edit:** `external/charts/charts/backend/values.yaml`
- Add `postgres:` section:
  ```yaml
  postgres:
    username: ""
    password: ""
  ```
- Add to the `env:` block:
  ```yaml
  - name: SPRING_PROFILES_ACTIVE
    value: k8s
  - name: SPRING_DATASOURCE_URL
    value: jdbc:postgresql://backend-postgres:5432/jucaneat
  - name: SPRING_DATASOURCE_USERNAME
    valueFrom:
      secretKeyRef:
        name: backend-postgres-secret
        key: username
  - name: SPRING_DATASOURCE_PASSWORD
    valueFrom:
      secretKeyRef:
        name: backend-postgres-secret
        key: password
  ```

Deploy command:
```bash
helm upgrade --install jucaneat ./external/charts \
  --set backend.postgres.username=<user> \
  --set backend.postgres.password=<password>
```

---

### 3. Fix Flyway Migration Schema — `V1__init_schema.sql` (Copilot #2, #3)

V1 has never been applied to a real Postgres instance (still in PR), so we can edit it directly.

**Edit:** `src/main/resources/db/migration/V1__init_schema.sql`
- Add `PRIMARY KEY (daily_menu_id, dish_id)` to `daily_menu_dishes` — enables `ON CONFLICT DO NOTHING` in V2
- Add `PRIMARY KEY (dish_id, allergen)` to `dish_allergens` — same reason

---

### 4. Fix `application-dev.properties` (Copilot #5)

**Edit:** `src/main/resources/application-dev.properties`
- Add: `spring.jpa.hibernate.ddl-auto=validate`

---

### 5. Fix `application-k8s.properties` (Copilot #10, #12)

**Edit:** `src/main/resources/application-k8s.properties`
- Fix Keycloak port: `http://keycloak:8080` → `http://keycloak:8081`
- Replace hardcoded RabbitMQ credentials:
  ```properties
  spring.rabbitmq.username=${RABBITMQ_USERNAME}
  spring.rabbitmq.password=${RABBITMQ_PASSWORD}
  ```
  (Already injected from `rabbitmq-default-user` secret by the backend chart.)

---

### 6. Pin Flyway Maven Plugin Version (Copilot #11)

**Edit:** `pom.xml`
- Add `<version>` matching the `flyway-core` version already declared in the pom.

---

### 7. Fix Documentation in `ai/01-postgres_migration/` (Copilot #4, #7, #8, #9)

- **`01-DEVELOPER_WORKFLOW.md`** (#4): Remove Testcontainers references; describe actual test setup (H2 in-memory, Flyway disabled, Hibernate create-drop).
- **`02-POSTGRES_MIGRATION.md`** (#9): Change `postgres:16` → `postgres:17`.
- **`03-SCHEMA_MANAGEMENT.md`** (#7): Add `spring.flyway.enabled=false` to the temporary props block used when generating V1.
- **`04-POSTGRES_SETUP_STEPS.md`** (#8): Change "Remove the three temporary lines" → "Remove all four temporary lines".

---

## Files to Modify

| File | Change |
|------|--------|
| `external/charts/templates/backend-postgres.yaml` | NEW — PVC + Deployment + Service for backend postgres |
| `external/charts/charts/backend/templates/postgres-secret.yaml` | NEW — backend-postgres-secret |
| `external/charts/charts/backend/values.yaml` | Add postgres section + 4 env vars |
| `src/main/resources/db/migration/V1__init_schema.sql` | Add composite PKs to junction tables |
| `src/main/resources/application-dev.properties` | Add ddl-auto=validate |
| `src/main/resources/application-k8s.properties` | Fix Keycloak port, fix RabbitMQ creds |
| `pom.xml` | Pin Flyway Maven plugin version |
| `ai/01-postgres_migration/01-DEVELOPER_WORKFLOW.md` | Fix Testcontainers reference |
| `ai/01-postgres_migration/02-POSTGRES_MIGRATION.md` | Fix postgres:16 → postgres:17 |
| `ai/01-postgres_migration/03-SCHEMA_MANAGEMENT.md` | Add spring.flyway.enabled=false to temp props |
| `ai/01-postgres_migration/04-POSTGRES_SETUP_STEPS.md` | Fix "three" → "four" |

### Files NOT changed
- `k8s/` — left as-is for local/minikube dev
- `V2__seed_data.sql` — no change needed once V1 has the PKs

---

## Verification

1. `./mvnw test` — all tests pass (H2, Flyway disabled)
2. `docker compose up` — app connects to postgres:17, Flyway runs V1+V2 cleanly
3. `helm template ./external/charts --set backend.postgres.username=x --set backend.postgres.password=x` — verify secret, postgres deployment, and env vars render correctly
4. Confirm all Copilot review comments are resolved before merging PR #61
