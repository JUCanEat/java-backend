#!/bin/bash

set -e
set -u

echo "setting up";

PGPASSWORD="${POSTGRES_PASSWORD}" psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER java_backend PASSWORD 'java_backend';
    CREATE DATABASE java_backend;
    GRANT ALL PRIVILEGES ON DATABASE java_backend TO java_backend;
EOSQL

echo "setup done";