# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy Maven wrapper first for dependency caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

# Copy source and package (tests skipped — run ./mvnw test before building)
COPY src/ src/
RUN ./mvnw package -DskipTests -Dspotless.check.skip=true -q

# Stage 2: Runtime
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
