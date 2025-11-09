# Repository Guidelines

## System Overview
This project is a Java 21 modular monolith that powers simonjamesrowe.com. The single deployable backend blends API gateway endpoints, search features, and in-process Kafka event processing. Virtual threads are enabled globally, so keep code synchronous and blocking-friendly—no reactive stacks.

## Module & Layout Basics
- Root Gradle build (`build.gradle`) captures shared plugins/dependencies; `settings.gradle` includes every module housed under `modules/`.
- `modules/backend` combines REST controllers, scheduled jobs, Kafka producer/consumer pairs, Elasticsearch repositories, and CMS integrations. Packages are organized by responsibility (config, core, dataproviders, entrypoints, mapper).
- `modules/model` provides DTOs and shared domain contracts—touch this module whenever an API or event schema changes.
- `modules/component-test` holds reusable Testcontainers fixtures (Kafka, Elasticsearch, WireMock) and should be the home for new integration utilities.
- Config assets (Checkstyle, etc.) live in `config/`. Build output lands under `build/`. Put new code in `src/main/java` plus resources in `src/main/resources` inside the relevant module.

## Tooling & Runtime Expectations
- Java toolchain is pinned to 21 with Spring Boot 3.3.5; stick with the Gradle wrapper (`./gradlew`) for every task.
- Infrastructure dependencies: Elasticsearch, Kafka, SendGrid, CMS HTTP APIs, and PDF generation libraries. Kafka producers and consumers run in-process using the `cms-events` topic.
- Observability is provided through Spring Actuator + Micrometer tracing (Zipkin compatible). Preserve actuator endpoints when adding new modules/config.

## Common Commands
```
./gradlew clean build                         # compile everything + tests
./gradlew :modules:backend:bootRun            # run backend locally
./gradlew :modules:backend:bootBuildImage     # build container image
./gradlew :modules:model:build                # scoped module build
./gradlew test jacocoTestReport               # run all tests + coverage HTML
```
Prefer focused `:modules:<name>:test` or `:modules:<name>:build` invocations when iterating on a single module. Always run at least `./gradlew test` before opening a PR.

## Coding Style & Conventions
- Follow Checkstyle rules in `config/checkstyle/checkstyle.xml`: four-space indentation, 120-char lines, no tabs, no wildcard imports, canonical naming.
- Keep constructors lightweight; wire dependencies via Spring configuration classes or `@ConfigurationProperties` records. Favor descriptive packages rooted under `com.simonjamesrowe.backend`.
- When adding configuration, expose it through properties classes instead of `System.getenv`, and document defaults in README or module docs.
- Virtual threads mean blocking I/O is acceptable—avoid mixing in reactive frameworks.

## Testing Expectations
- Unit tests live beside code in `src/test/java` for each module. Use JUnit 5 + Mockito + AssertJ.
- Integration or component tests should reuse fixtures from `modules/component-test` and Testcontainers (Kafka, Elasticsearch, WireMock). Share new utilities there rather than duplicating setup code.
- Name tests with the `*Test` suffix describing the behavior (`ResumeControllerTest`). Keep Jacoco coverage green; add WireMock stubs or sample payloads for any new external interactions.
- Use `./gradlew checkstyleMain` if your IDE is not wired to the shared lint rules.

## Git & PR Hygiene
- Commit messages are short and imperative, ideally one behavior per commit, e.g., “Add blog search endpoint (backend).”
- Pull requests must summarize changes, reference tickets, list validation commands, and include screenshots or sample payloads for user-visible API changes. Call out roll-forward/rollback concerns whenever shared models or schemas change.

## Security & Configuration
- Credentials flow via environment variables (`DOCKER_USERNAME`, `DOCKER_PASSWORD`, `GITHUB_TOKEN`, SendGrid keys, etc.). Never commit `.env` files or sample secrets.
- Each module owns its `application.yml`. Bind new settings through `@ConfigurationProperties` and keep defaults sane for local development.
- When touching Docker/image workflows, keep `bootBuildImage` compatible with the current base image strategy (standard JVM image, no native builds).

## When In Doubt
Consult `README.md` for architecture diagrams and flow descriptions covering REST, Kafka, and scheduled sync paths. If changing shared contracts or infrastructure interactions, update both README and relevant module docs to keep future contributors onboarded quickly.
