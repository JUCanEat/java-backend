# Understanding Ambiguities

Questions and answers that arose during review of the Copilot PR feedback and the post-migration plan.

---

## 1. Is `SPRING_PROFILES_ACTIVE=k8s` genuinely missing from the backend chart?

Yes. Looking at `external/charts/charts/backend/values.yaml`, the `env:` block has `SPRING_RABBITMQ_*`, `SPRING_AI_OPENAI_API_KEY`, `SPRINGDOC_*`, `JAVA_OPTS` — but no `SPRING_PROFILES_ACTIVE`. Without it, Spring Boot runs with no active profile, loading only `application.properties` (the base one). `application-k8s.properties` — which has `spring.flyway.enabled=false`, the Keycloak URL, etc. — is never read. This is a genuine oversight, not nitpicking. The one caveat: it *could* be baked into the Docker image's entrypoint, but there is no evidence of that from the chart side.

---

## 2. Can we just edit V1, and what does adding composite PKs do to Hibernate?

Yes, we can edit V1 safely **because it has never been applied to any real PostgreSQL** — it was generated, put in the PR, and never run. Flyway only locks a migration's checksum once it applies it and records it in `flyway_schema_history`. Since that has not happened yet, V1 is just an SQL file we can edit freely.

As for Hibernate: we are still using Hibernate. The relationship here is:
- **Hibernate generates Java ↔ table mappings** at runtime (queries, inserts, etc.)
- **Flyway owns the DDL** — it creates/alters the physical tables
- `ddl-auto=validate` means Hibernate *only checks* that the columns it expects exist — it does NOT validate constraints like primary keys or indexes

So adding `PRIMARY KEY (daily_menu_id, dish_id)` to the `daily_menu_dishes` table is invisible to Hibernate at validation time. Hibernate cares about column names and types, not about whether a PK constraint is declared. Nothing changes in the Java codebase — no entity edits, no annotations. The PK is purely a database-level constraint that enables `ON CONFLICT DO NOTHING` to work in V2.

---

## 3. Can a Helm chart inject values into Spring Boot `.properties` files?

Not directly — the chart cannot write to files inside the container. What it does is inject **environment variables** into the pod. Spring Boot has a feature called relaxed binding where environment variables automatically override properties with no extra configuration:

```
SPRING_RABBITMQ_USERNAME  →  spring.rabbitmq.username
SPRING_DATASOURCE_URL     →  spring.datasource.url
```

Environment variables sit higher in Spring Boot's property source priority than `.properties` files, so they silently win. The `.properties` file value is the fallback; the env var is the override.

The `${RABBITMQ_USERNAME}` syntax in a `.properties` file is a different (and more explicit) approach: it is a Spring placeholder that tells Spring "read this value from an env var named `RABBITMQ_USERNAME`". Both work, but the `SPRING_RABBITMQ_USERNAME` env var format (already in the backend chart) makes the explicit placeholder unnecessary — Spring picks it up automatically. Copilot's comment (#12) is still valid as a defensive clarity fix for the hardcoded `admin/admin` left in the file.

---

## 4. How does `{{ include "java-backend.fullname" . }}` work?

This is Go template syntax (Helm uses Go's `text/template`). `include` calls a named template defined in `_helpers.tpl`. The standard `fullname` helper produces a string like `<release-name>-<chart-name>` — e.g. if you deploy with `helm install jucaneat ./charts`, the fullname becomes something like `jucaneat-backend`. The `.` passes the full rendering context (values, release metadata, etc.) into the helper.

The important constraint: this template syntax **only works inside `templates/` files**, not in `values.yaml`. `values.yaml` is plain YAML with no templating. So in the secret template file (`postgres-secret.yaml`) we can write `{{ include "java-backend.fullname" . }}-postgres-credentials`, but in `values.yaml` the `secretKeyRef.name` must be a hardcoded string. That is why the plan uses a fixed name convention (`backend-postgres-secret`) and documents it — ensuring consistency between where the Secret is created and where it is referenced.

---

## 5. What does "injected at deploy time" mean?

`values.yaml` holds defaults. `--set` at deploy time overrides them:

```bash
helm upgrade --install jucaneat ./external/charts \
  --set backend.postgres.username=myuser \
  --set backend.postgres.password=hunter2
```

Helm merges `--set` values on top of `values.yaml`, renders all templates with the merged result, and sends the rendered YAML to Kubernetes. The `required` function in the secret template (`{{ required "postgres.username is required" .Values.postgres.username }}`) causes the render to fail immediately if the value is empty — so forgetting `--set` gives a clear error rather than a pod starting with blank credentials. The rendered Secret ends up in the cluster, the pod reads it via `secretKeyRef`, and Spring Boot picks it up as an env var. The actual credentials never touch the git repository.
