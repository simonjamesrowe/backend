# Backend Modulith

Multi-module Gradle project for Java components powering simonjamesrowe.com.

## Architecture

This is a modular monolith with a unified backend service that combines API gateway and search functionality.

### Modules

- **`modules/model`** - Common data models and DTOs
- **`modules/component-test`** - Test utilities and shared test components
- **`modules/backend`** - Unified backend service (REST API + Search)

### Backend Module Structure

The backend module follows clean architecture with a flat package structure:

```
com.simonjamesrowe.backend/
├── BackendApplication.java       # Main application entry point
├── config/                        # Spring configuration classes
├── core/                          # Business logic (use cases, models, repository interfaces)
├── dataproviders/                 # External integrations (CMS, SendGrid, Elasticsearch)
├── entrypoints/                   # Entry points (REST, Kafka, Scheduled tasks)
└── mapper/                        # Data transformation mappers
```

## Technology Stack

- **Java 21** - Primary language with virtual threads support
- **Spring Boot 3.3.5** - Application framework
- **Spring Web** - Servlet-based web framework with virtual threads
- **Elasticsearch** - Search engine
- **Apache Kafka** - Event streaming (in-process producer & consumer)
- **Testcontainers** - Integration testing
- **Lombok** - Reduce boilerplate code
- **Gradle** - Build tool (Groovy DSL)

## Application Flows

### REST API Flows

#### 1. Contact Us Flow
```
POST /api/contact
├── ContactUsController (backend)
├── ContactUseCase (backend)
├── EmailSender (backend)
└── SendGrid Email Service
```

#### 2. Resume Generation Flow
```
POST /api/resume
├── ResumeController (backend)
├── ResumeUseCase (backend)
├── CmsResumeRepository (backend)
├── CMS API Call
└── PDF Generation
```

#### 3. File Upload Flow
```
POST /api/upload
├── UploadController (backend)
├── CompressFileUseCase (backend)
└── File Processing
```

#### 4. Blog Search Flow
```
GET /api/search/blogs?q={query}
├── BlogController (backend)
├── SearchBlogsUseCase (backend)
├── BlogIndexRepository (backend)
└── Elasticsearch Query
```

#### 5. Site Search Flow
```
GET /api/search/site?q={query}
├── SiteController (backend)
├── SearchSiteUseCase (backend)
├── SiteSearchRepository (backend)
└── Elasticsearch Query
```

### Kafka Event Flows

#### 1. CMS Content Update Flow
```
CMS Webhook Event
├── WebhookController (backend - producer)
├── Kafka Producer
├── cms-events Topic
├── KafkaEventConsumer (backend - consumer)
├── IndexBlogUseCase (backend)
└── Elasticsearch Index Update
```

**Note:** Producer and consumer run in the same JVM for internal event-driven communication.

#### 2. Site Content Indexing Flow
```
CMS Content Change
├── cms-events Topic
├── KafkaEventConsumer (backend)
├── IndexSiteUseCase (backend)
└── Elasticsearch Index Update
```

#### 3. Scheduled Synchronization Flow
```
@Scheduled CmsSynchronization (backend)
├── CmsRestApi (backend)
├── Fetch All Content
├── BlogMapper/JobMapper/SkillsGroupMapper
└── Bulk Elasticsearch Update
```

## Virtual Threads

The application uses Java 21's virtual threads for improved concurrency:

- Enabled via `spring.threads.virtual.enabled=true`
- All HTTP requests run on virtual threads automatically
- Allows simple, synchronous code without blocking platform threads
- No need for reactive/async programming patterns

## Event Types

### Kafka Topics
- **`cms-events`** - CMS content change notifications (internal to backend module)

### Event Schemas
```json
{
  "eventType": "BLOG_UPDATED|BLOG_DELETED|JOB_UPDATED|SKILLS_UPDATED",
  "model": "blog|job|skills",
  "entry": {},
  "createdAt": "ISO-8601"
}
```

## Building and Running

### Prerequisites
- Java 21
- Docker (for containerized services and Testcontainers)

### Build Commands
```bash
# Build all modules
./gradlew build

# Build backend module
./gradlew :modules:backend:build

# Run tests
./gradlew test

# Run backend tests
./gradlew :modules:backend:test

# Create Docker image
./gradlew :modules:backend:bootBuildImage
```

### Running Services
```bash
# Start backend service
./gradlew :modules:backend:bootRun
```

## Configuration

### Environment Variables
- `DOCKER_USERNAME` - Docker registry username
- `DOCKER_PASSWORD` - Docker registry password
- `GITHUB_ACTOR` - GitHub actor for package access
- `GITHUB_TOKEN` - GitHub token for package access

### Application Properties
The backend service has `application.yml` in `modules/backend/src/main/resources`.

Key configurations:
- Virtual threads enabled
- Kafka consumer group: `backend`
- Elasticsearch connection settings
- SendGrid API key
- CMS URL

## Dependencies

### Inter-module Dependencies
```
backend ──┬──> model
          └──> component-test (test only)
```

### External Dependencies
- Spring Boot 3.3.5
- Spring Data Elasticsearch
- Spring Kafka
- SendGrid (email)
- PDF generation libraries
- Testcontainers

## Testing

The project uses:
- **JUnit 5** - Test framework
- **Mockito** - Mocking framework
- **Testcontainers** - Integration testing with Elasticsearch, Kafka
- **AssertJ** - Assertions
- **WireMock** - HTTP mocking

Test utilities are provided in the `component-test` module for:
- Elasticsearch testing
- Kafka testing
- Wiremock testing

## Monitoring

- **Spring Actuator** - Health checks and metrics
- **Micrometer Tracing** - Distributed tracing
- **Zipkin** - Trace visualization

## Migration History

This project was refactored from separate microservices:
- Originally: `api-gateway` and `search-service` as separate deployments
- Now: Unified `backend` module combining both services
- All code uses synchronous programming with virtual threads (no reactive/async patterns)
- Uses standard JVM Docker images (no GraalVM native compilation)
