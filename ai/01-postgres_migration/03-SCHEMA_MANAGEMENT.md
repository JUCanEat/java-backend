# Schema Management with Flyway

## Generating the Initial Schema (V1)

Once Flyway is added, the first migration file (`V1__init_schema.sql`) needs to exactly match
what Hibernate would create from the current entity annotations. Rather than hand-rolling this
DDL, let Hibernate generate it for you.

Add these properties temporarily to your dev config, run the app once, then remove them:

```properties
spring.flyway.enabled=false
spring.jpa.properties.javax.persistence.schema-generation.scripts.action=create
spring.jpa.properties.javax.persistence.schema-generation.scripts.create-target=V1__init_schema.sql
spring.jpa.hibernate.ddl-auto=create
```

The file will be written to the project root. Move it to `src/main/resources/db/migration/` and
it becomes your baseline migration. This guarantees V1 matches the entities exactly with no
manual transcription errors.

---

## Entity Changes Now Require a Migration File — Always

Before Flyway, Hibernate managed the schema automatically via `ddl-auto=create`. Every restart
recreated all tables from entity annotations. This is no longer the case.

With Flyway and `ddl-auto=validate`, **Hibernate no longer modifies the database schema**.
It only checks that the database matches the entity annotations at startup — and refuses to start
if there is a mismatch.

This means: **every change to an entity that affects the database structure must be accompanied
by a new Flyway migration file.** This is unavoidable and by design.

Examples of changes that require a migration:
- Adding or removing a field that maps to a column
- Renaming a field (without `@Column(name=...)` keeping the old name)
- Adding a new entity class (new table)
- Changing a relationship (e.g., `@OneToMany` to `@ManyToMany`, which changes join tables)
- Adding or removing `@Column` constraints (nullable, length, unique)

Examples that do NOT require a migration:
- Adding or changing business logic in a method
- Adding a transient field (`@Transient`)
- Changing validation annotations that don't affect the schema (`@NotNull` on the Java side only)

### The new workflow for entity changes

1. Update the entity class
2. Write `V{next}__{description}.sql` in `src/main/resources/db/migration/`
3. Restart the app — Flyway applies the migration, Hibernate validates and passes

If you skip step 2, the app will fail to start with a schema validation error. This is the
safety net that prevents schema drift between environments.

### Never edit an existing migration file

Flyway checksums every migration it has applied. Editing an applied file causes a checksum
mismatch and the app will refuse to start across all environments, including production.
Always add a new file — never modify an old one.
