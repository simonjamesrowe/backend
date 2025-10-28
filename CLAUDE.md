# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :modules:backend:build
./gradlew :modules:model:build
./gradlew :modules:component-test:build

# Run tests for all modules
./gradlew test

# Run tests for specific module
./gradlew :modules:backend:test

# Run single test class
./gradlew :modules:backend:test --tests com.simonjamesrowe.apigateway.test.ApiGatewayApplicationTests

# Run single test method
./gradlew :modules:backend:test --tests "com.simonjamesrowe.apigateway.test.ApiGatewayApplicationTests.testMethod"

# Clean build
./gradlew clean build

# Build Docker image
./gradlew :modules:backend:bootBuildImage

# Run service locally
./gradlew :modules:backend:bootRun

# Generate test reports
./gradlew jacocoTestReport

# Check code quality with SonarQube
./gradlew sonarqube

# Run Checkstyle code style checks
./gradlew checkstyleMain checkstyleTest

# Run all quality checks
./gradlew check
```

## Architecture Overview

This is a multi-module Gradle project with a modular monolith architecture. All modules are under the `modules/` directory with cross-module dependencies managed through Gradle project references.

### Module Dependency Graph
```
backend ──┬──> model
          └──> component-test (test only)
```

### Unified Backend Module

The `backend` module is a unified monolith that combines API gateway and search functionality:
- **`apigateway/`** package - API gateway functionality (REST endpoints, CMS proxy, SendGrid integration, resume generation)
- **`searchservice/`** package - Search functionality (Elasticsearch indexing, search endpoints, CMS sync)
- Both packages follow clean architecture with `core/`, `dataproviders/`, `entrypoints/`, and `config/` layers

### Clean Architecture Layers

The backend module follows clean architecture:
- **`core/`** - Business logic (use cases, models, repository interfaces)
- **`dataproviders/`** - External integrations (CMS, SendGrid, Elasticsearch)
- **`entrypoints/`** - Entry points (REST controllers, Kafka consumers, scheduled tasks)
- **`config/`** - Spring configuration classes

### Event-Driven Communication

Internal communication via Kafka (producer and consumer in same JVM):
- **Producer**: `WebhookController` publishes to `cms-events` topic
- **Consumer**: `KafkaEventConsumer` consumes from `cms-events`
- **Event Types**: `BLOG_UPDATED`, `BLOG_DELETED`, `JOB_UPDATED`, `SKILLS_UPDATED`

### Data Flow Patterns

1. **Synchronous REST**: Client → backend → External Services (CMS, SendGrid)
2. **Asynchronous Events**: CMS Webhook → backend (WebhookController) → Kafka → backend (KafkaEventConsumer) → Elasticsearch
3. **Scheduled Sync**: Backend periodically syncs all content from CMS to Elasticsearch

### Migration History

This project was migrated from separate services:
- Originally: `api-gateway` and `search-service` as separate deployments
- Now: Unified `backend` module combining both services
- All code converted to Pure Java 21
- Uses standard JVM Docker images (no GraalVM native compilation)

### Required Environment Variables

For building Docker images and publishing:
- `DOCKER_USERNAME` - Docker Hub username
- `DOCKER_PASSWORD` - Docker Hub password
- `GITHUB_ACTOR` - GitHub username for package access
- `GITHUB_TOKEN` - GitHub token for accessing private packages

### Technology Stack

- **Java 21** with Spring Boot 3.3.5
- **Gradle** with Groovy DSL (not Kotlin DSL)
- **Spring Web** (servlet-based, not WebFlux)
- **Spring Data Elasticsearch** for search functionality
- **Spring Kafka** for event-driven communication
- **Testcontainers** for integration testing
- **Lombok** for reducing boilerplate
- **Standard JVM deployment** (bootBuildImage with no native compilation)