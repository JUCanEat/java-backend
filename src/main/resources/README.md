# Application Configuration

As of 01.04.2026, the application defaults to the `-dev` profile (set in `application.properties`) across all environments, 
including the Kubernetes cluster. 

The Kubernetes cluster is what interests us in this README.
Rather than activating the `-k8s` profile, the Helm chart at `external/charts/charts/backend/values.yaml` injects 
environment variables that override the dev profile's localhost defaults: 
- redirecting the datasource to the in-cluster Postgres (`postgres:5432/java_backend`), 
- pointing JWT validation at the cluster-internal Keycloak instance, 
- wiring RabbitMQ credentials from a Kubernetes Secret, 
- disabling Flyway (since migrations are intended to be run by a CI pipeline before deployment),
- ...

The `-k8s` profile and its properties file are retained from an earlier unsuccessful attempt to set them up,
but are not actively used.
