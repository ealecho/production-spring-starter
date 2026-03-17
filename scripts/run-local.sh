#!/usr/bin/env bash
set -euo pipefail

export SPRING_PROFILES_ACTIVE=dev
export DB_URL=${DB_URL:-jdbc:postgresql://localhost:5432/template_dev}
export DB_USERNAME=${DB_USERNAME:-postgres}
export DB_PASSWORD=${DB_PASSWORD:-postgres}

./mvnw spring-boot:run
