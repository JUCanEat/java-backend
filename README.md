# java-backend
## How to run springboot application
### IntelliJ IDEA
1. Install Lombok Plugin

- Install Lombok plugin - should be suggested in the bottom right corner of IntelliJ
- If not suggested: File → Settings → Plugins → Search "Lombok" → Install

2. Reload Maven Project

- If you see 'M' icon on the right panel: Click it → Reload Maven Project (this will take a while)
- If you don't see 'M' icon: Look for Maven reload suggestion in the bottom right corner and click it

3. Run Spring Boot Application

- If the green arrow in top right corner is grey find main class: src/main/java/com/backend/BackendApplication.java and click on the green arrow next to a main class
- From now on, you can use the green arrow in top right corner

### Terminal / browser / dev
4. Test the Application

- Go to http://localhost:8080/hello/
- Should display "Hello World"

5. Unit tests
- test are automaticly run by a ci workflow, do not merge until you pass them

6. Migrations
- We use flyway for database migrations, which means that if you want to update the database schema, 
you need to create a new migration file in src/main/resources/db/migration.
- If you manage to "break" the database, you can reset it by running `docker compose down -v` (this will remove the postgres data volume associated with the compose file

7. Docker compose

**If you want to run whole backend locally, fast (java + keycloak + rabbitmq + postgres db)**:
- run compose.yaml (see pt. 2 of `6. Migrations`)
- run spring boot application (see `3. Run Spring Boot Application`)
- Java backend -> http://localhost:8080
- Keycloak -> http://localhost:8081
- POSTGRES DB shell -> `docker exec -it <container_id> psql -U postgres` (use `docker ps` to find container id)

If you want to run the backend with the containerized version of java as a sanity check:
- run full_compose.yaml with `docker compose -f full_compose.yaml up`

9. Documentation
- Documentation is generated under http://localhost:8080/swagger-ui/index.html
- To add custom descrtptions see example controller

## Testing H2 db updates
0. Update data.sql file, which contains queries creating the database
1. Rebuild .jar with `mvn clean package`
2. Build your own docker image locally and use it to run the app (instead of the one from CI/CD)

_See [this PR](https://github.com/JUCanEat/java-backend/pull/38) for reference and tips_
