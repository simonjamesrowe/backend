# Repository Guidelines

## Project Structure & Module Organization
This repo is a Gradle multi-module monolith. Shared build logic lives in `build.gradle`, while `settings.gradle` wires every module under `modules/`. `modules/api-gateway` exposes the REST facade, `modules/search-service` owns Elasticsearch flows, `modules/model` holds DTOs, and `modules/component-test` contains reusable Testcontainers fixtures. Configuration assets stay in `config/` (for example, Checkstyle rules) and generated build output under `build/`. Place new code in the proper module’s `src/main/java` and co-locate resources in `src/main/resources`.

## Build, Test, and Development Commands
Use the Gradle wrapper exclusively:
```
./gradlew clean build                           # compile all modules and run unit tests
./gradlew :modules:api-gateway:bootRun          # launch the API gateway locally
./gradlew :modules:search-service:bootBuildImage # create container image with Spring AOT
./gradlew test jacocoTestReport                 # execute suites and publish coverage HTML
```
For focused builds, swap the module path (e.g., `:modules:model:build`).

## Coding Style & Naming Conventions
Java 21 is enforced via toolchains and `options.release = 21`. Checkstyle (`config/checkstyle/checkstyle.xml`) blocks tabs, enforces four-space indentation, 120-character lines, and canonical naming for classes, methods, and constants. Favor descriptive packages that mirror bounded contexts (`io.github.simonjamesrowe.search`). Avoid wildcard imports and keep constructors slim—push wiring into Spring configuration. Run `./gradlew checkstyleMain` before pushing if your IDE is not wired to the shared config.

## Testing Guidelines
Tests sit beside code in each module’s `src/test/java`. Use JUnit 5 with Mockito/AssertJ, and rely on the `component-test` module for Postgres, Kafka, or Elasticsearch Testcontainers. Name tests with the `*Test` suffix and describe the behavior (`ResumeControllerTest`). Every module must keep Jacoco coverage reports green; integration additions should ship replayable fixtures or WireMock stubs. Run `./gradlew test` before opening a PR; add `-PskipTests` only when debugging CI issues with a follow-up run.

## Commit & Pull Request Guidelines
Git history favors short, imperative commits (“Enable Spring AOT processing...”). Keep scope tight—one behavior per commit—and mention the module when it clarifies intent. Pull requests need a concise summary, linked issue/ticket, validation notes (commands executed), and screenshots for user-facing contract changes such as new endpoints. Include roll-forward/rollback considerations whenever shared models change.

## Security & Configuration Tips
API credentials and Docker logins are pulled from environment variables (`DOCKER_USERNAME`, `GITHUB_TOKEN`, etc.). Never commit `.env` files or sample secrets. Each service reads its own `application.yml`; bind new settings via Spring configuration properties and document defaults in the README or module-specific docs. Prefer `@ConfigurationProperties` over direct `System.getenv` calls to keep environments swappable.
