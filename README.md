# java-backend
## Contents
1. How to run the Spring Boot application
2. Troubleshooting common errors

### 1. How to run the Spring Boot application

1. Install Lombok Plugin in IntelliJ IDEA

- Installatlion should automatically be suggested in the bottom right corner of IntelliJ
- If not suggested: File → Settings → Plugins → Search "Lombok" → Install

2. Reload Maven Project

- If you see the 'M' icon on the right panel: Click it → Reload Maven Project (this will take a while)
- If you don't see the 'M' icon: Look for "Maven reload" suggestion in the bottom right corner and click it.

3. Run Spring Boot application

- If the green arrow in the top right corner is grey, find the main class: src/main/java/com/backend/BackendApplication.java and click on the green arrow next to a main class
- From now on, you can use the green arrow in the top right corner.

4. Test the application

- Go to http://localhost:8080/hello/
- It should display "Hello World"

5. Unit tests
- Tests are automatically run by a CI workflow, _do not_ merge until you pass them.

6. Docker
- On merge to main, a docker image is built and pushed to Dockerhub with latest tag
- See https://hub.docker.com/repository/docker/haniazipser2004/awesome-amazing-project
- `docker pull haniazipser2004/awesome-amazing-project:latest`
- `docker run -p 8080:8080 haniazipser2004/awesome-amazing-project:latest` (or different port mapping if 8080 is in use)

7. Docker compose
  If you want to run the whole backend (Java + Python + Keycloak):
- clone this repo
- run compose.yaml
- Java backend -> http://localhost:8080
- Keycloak -> http://localhost:8081
- Python microservice -> http://localhost:8000

7. Documentation
- Documentation is generated under http://localhost:8080/swagger-ui/index.html
- To add custom descrtptions see example controller

### 2. Troubleshooting common errors
If while attempting to build the project you encounter the following error:
```
Error:java: java.lang.ExceptionInInitializerError 
com.sun.tools.javac.code.TypeTags
```

You should proceed as follows:

- Gear icon on the right side of the top Toolbar (top-right corner of IntelliJ IDEA)
- Project structure
- Rightmost context window, select 'Dependencies' tab
- Module SDK

Select the correct SDK (make sure JDK 21 is installed somewhere in the system and point to it there).
