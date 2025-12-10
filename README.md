# java-backend
## How to run springboot application

1. Install Lombok Plugin

- Install Lombok plugin - should be suggested in the bottom right corner of IntelliJ
- If not suggested: File → Settings → Plugins → Search "Lombok" → Install

2. Reload Maven Project

- If you see 'M' icon on the right panel: Click it → Reload Maven Project (this will take a while)
- If you don't see 'M' icon: Look for Maven reload suggestion in the bottom right corner and click it

3. Run Spring Boot Application

- If the green arrow in top right corner is grey find main class: src/main/java/com/backend/BackendApplication.java and click on the green arrow next to a main class
- From now on, you can use the green arrow in top right corner

4. Test the Application

- Go to http://localhost:8080/hello/
- Should display "Hello World"

5. Unit tests
- test are automaticly run by a ci workflow, do not merge until you pass them

6. Docker
- On merge to main docker image is build and pushed to dockerhub with latest tag
- See https://hub.docker.com/repository/docker/haniazipser2004/awesome-amazing-project
- docker pull haniazipser2004/awesome-amazing-project:latest
- docker run -p 8080:8080 haniazipser2004/awesome-amazing-project:latest (or different port mapping if 8080 is in use)

7. Docker compose
  If you want to run whole backend (java + python + keycloak):
- clone this repo
- run compose.yaml
- Java backend -> http://localhost:8080
- Keycloak -> http://localhost:8081
- Python microservice -> http://localhost:8000

7. Documentation
- Documentation is generated under http://localhost:8080/swagger-ui/index.html
- To add custom descrtptions see example controller

## Testing H2 db updates
0. Update data.sql file, which contains queries creating the database
1. Rebuild .jar with `mvn clean package`
2. Build your own docker image locally and use it to run the app (instead of the one from CI/CD)

_See [this PR](https://github.com/JUCanEat/java-backend/pull/38) for reference and tips_
