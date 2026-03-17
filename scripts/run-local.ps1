$env:SPRING_PROFILES_ACTIVE = "dev"
if (-not $env:DB_URL) { $env:DB_URL = "jdbc:postgresql://localhost:5432/template_dev" }
if (-not $env:DB_USERNAME) { $env:DB_USERNAME = "postgres" }
if (-not $env:DB_PASSWORD) { $env:DB_PASSWORD = "postgres" }

.\mvnw.cmd spring-boot:run
