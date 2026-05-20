# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

A microservices architecture lab with three Java services communicating asynchronously through Kafka. The domain is intentionally simple; the goal is demonstrating distributed systems patterns end-to-end.

## Commands

### Build (each service independently)

```bash
cd document-service && mvn clean package -DskipTests
cd processing-service && mvn clean package -DskipTests
cd export-job-service && mvn clean package -DskipTests
```

### Run tests

```bash
# All tests in a service
cd document-service && mvn test

# Single test class
cd document-service && mvn test -Dtest=DocumentServiceTest

# Spring Boot services use mvn; Quarkus service (export-job-service) also uses mvn
```

### Local environment (requires built JARs first)

```bash
docker compose up -d --build   # start everything
docker compose down            # stop everything
```

### Terraform (AWS infrastructure)

```bash
cd terraform
terraform init
terraform fmt && terraform validate
terraform plan
terraform apply
terraform destroy  # destroys all AWS resources — incurs cost while running
```

## Architecture

### Service responsibilities

| Service | Framework | Port | Database | Role |
|---|---|---|---|---|
| `document-service` | Spring Boot | 8081 | PostgreSQL | REST API, publishes events to Kafka |
| `processing-service` | Spring Boot | 8082 | MongoDB | Kafka consumer, persists processing history |
| `export-job-service` | Quarkus | 8083 | — | Scheduled job, exports completed events to file or S3 |

### Event flow

1. `POST /api/v1/documents` → `document-service` persists to PostgreSQL → publishes `DocumentSubmittedEvent` to Kafka topic `document-submitted`
2. `processing-service` consumes `document-submitted` (consumer group `processing-service`) → persists `ProcessingEvent` to MongoDB with status `COMPLETED`
3. `export-job-service` runs every 60 seconds → calls `processing-service` via HTTP → writes a JSON file via `StorageServiceResolver`

### Spring Cloud Stream bindings

- **document-service**: publishes on binding `documentSubmitted-out-0` → destination `document-submitted`
- **processing-service**: consumes on binding `processDocumentSubmitted-in-0`; the function bean name (`spring.cloud.function.definition: processDocumentSubmitted`) must match the `DocumentProcessorConsumer` class registered as a `Consumer<Message<DocumentSubmittedEvent>>`

### Storage abstraction (export-job-service)

`StorageServiceResolver` selects the active implementation based on the `storage.provider` config property (`local` or `s3`). To switch to S3 in Docker Compose, set `STORAGE_PROVIDER=s3` and provide `AWS_REGION`, `S3_BUCKET_NAME`, `S3_EXPORT_PREFIX`.

### Idempotence (processing-service)

`DocumentProcessorConsumer` checks MongoDB before processing: if a `ProcessingEvent` with the same `documentId` already has status `COMPLETED` or `FAILED`, it throws `BusinessException` and skips reprocessing.

### Observability

All three services propagate `correlationId` via MDC. Structured log fields: `traceId`, `spanId`, `correlationId`, `documentId`, `processingEventId`, `jobName`.

- **Spring Boot services**: `GET /actuator/health`, `GET /actuator/prometheus`
- **Quarkus service**: `GET /q/health`, `GET /q/metrics`
- **Jaeger UI**: http://localhost:16686 — traces sent via OTLP
- **Grafana**: http://localhost:3000 (admin/admin)
- **Kafka UI**: http://localhost:8088

### Fault tolerance (export-job-service)

`ExportJobService.exportCompletedEvents()` is annotated with `@Retry(maxRetries=2)`, `@Timeout(5000)`, and `@CircuitBreaker`. These are MicroProfile Fault Tolerance annotations executed by SmallRye.

### AWS vs local mapping

| Concern | Local | AWS |
|---|---|---|
| Relational DB | PostgreSQL container | RDS PostgreSQL |
| NoSQL | MongoDB container | DynamoDB (planned migration) |
| Export storage | Local filesystem | S3 |
| Messaging | Kafka on Docker | Kafka on EC2 |
| Observability | Prometheus + Grafana + Jaeger | CloudWatch |
