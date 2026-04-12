#!/bin/bash

set -e
set -u

echo "setting up";

PGPASSWORD="${POSTGRES_PASSWORD}" psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER java_backend PASSWORD 'java_backend';
    CREATE DATABASE java_backend;
    GRANT ALL PRIVILEGES ON DATABASE java_backend TO java_backend;
EOSQL

# GRANT ... ON DATABASE does not cover existing schemas inside that database,
# so java_backend would fail to create any tables (e.g. flyway_schema_history) without this.
PGPASSWORD="${POSTGRES_PASSWORD}" psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "java_backend" <<-EOSQL
    GRANT ALL ON SCHEMA public TO java_backend;
EOSQL

echo "setup done";