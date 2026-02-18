# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

**HSS Backend (Hayvan Sağlık Sistemi - Animal Health System)** is a Spring Boot 3.5.6 veterinary clinic management system deployed on Google Cloud Platform. The system manages animals, owners, appointments, medical records, vaccinations, prescriptions, lab tests, billing, and more.

## Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.5.6
- **Database**: PostgreSQL 15 (local dev) / Cloud SQL (production)
- **ORM**: Spring Data JPA with Hibernate
- **Database Migrations**: Flyway
- **Security**: Spring Security with JWT (custom implementation using jjwt)
- **Cloud Platform**: Google Cloud Platform (Cloud Run, Cloud SQL, Cloud Storage)
- **Build Tool**: Maven (wrapper included)
- **Mapping**: MapStruct 1.5.5
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Containerization**: Docker with multi-stage builds

## Development Commands

### Build & Run

```powershell
# Clean and build the project
.\mvnw clean package

# Build without tests
.\mvnw clean package -DskipTests

# Run the application locally (dev profile)
.\mvnw spring-boot:run

# Run with specific profile
.\mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Testing

```powershell
# Run all tests
.\mvnw test

# Run specific test
.\mvnw test -Dtest=HssBackendApplicationTests

# Skip integration tests
.\mvnw test -Dtest=!*IntegrationTest
```

### Database

```powershell
# Start local PostgreSQL with Docker Compose
docker-compose up -d postgres

# Start PostgreSQL with pgAdmin
docker-compose up -d

# Stop services
docker-compose down

# Flyway migrate manually
.\mvnw flyway:migrate

# Flyway info (view migration status)
.\mvnw flyway:info

# Flyway validate
.\mvnw flyway:validate
```

### Docker

```powershell
# Build Docker image
docker build -t hss-backend:local .

# Run Docker container
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev hss-backend:local

# Build and run with Docker Compose
docker-compose up --build
```

## Architecture Overview

### Layered Architecture

The codebase follows a classic layered architecture pattern:

1. **Controller Layer** (`controller/`): REST API endpoints, request validation, HTTP responses
2. **Service Layer** (`service/`): Business logic, transaction management
3. **Repository Layer** (`repository/`): Data access using Spring Data JPA
4. **Entity Layer** (`entity/`): JPA entities with relationships
5. **DTO Layer** (`dto/`): Data transfer objects split into `request/` and `response/`
6. **Mapper Layer** (`mapper/`): MapStruct mappers for entity-DTO conversion
7. **Config Layer** (`config/`): Spring configuration classes
8. **Exception Layer** (`exception/`): Custom exception classes

### Core Domain Model

The system is built around these primary entities with complex relationships:

- **Animal**: Central entity with one-to-many relationships to medical records, appointments, lab tests, prescriptions, vaccinations, documents
- **Owner**: Links to animals, invoices, documents, communications
- **Appointment**: Scheduled visits with status tracking (SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW)
- **MedicalHistory**: Complete medical records for animals
- **Staff**: Veterinarians, receptionists, and other staff with role-based permissions
- **Species/Breed**: Hierarchical relationship where breeds belong to species
- **Invoice/InvoiceItem**: Billing with line items
- **StockProduct/StockTransaction**: Inventory management
- **Document**: File storage integration with Google Cloud Storage

### Security Architecture

- **Authentication**: JWT-based using a custom implementation (SecurityConfig.java)
- **Authorization**: Role-based access control with roles: ADMIN, VETERINARIAN, STAFF, RECEPTIONIST
- **JWT Structure**: Tokens include a `roles` claim (array) that gets converted to Spring Security GrantedAuthorities with `ROLE_` prefix
- **Public Endpoints**: `/api/public/**`, `/actuator/health`, `/actuator/info`, Swagger UI endpoints, and currently `/api/animals/**` and `/api/appointments/**` (for development)
- **CORS**: Configured to allow all origins in development

### Database Migrations

Flyway migrations are located in `src/main/resources/db/migration/`:
- V1: Core tables (species, breed, owner, animal)
- V2: Medical tables (medical_history, clinical_examination, lab_test, radiological_imaging)
- V3: Medicine and vaccine tables
- V4: Billing tables (invoice, invoice_item)
- V5: Document and communication tables
- V6: Staff and authentication tables
- V7: Inventory tables (stock_product, stock_transaction, equipment)
- V8: System tables (reminder, report_schedule)
- V9: Indexes and constraints
- V11: Staff role table updates

**Important**: When creating new migrations, follow the naming convention `V{number}__{description}.sql` and always increment the version number sequentially.

### Cloud Deployment

The application is deployed to Google Cloud Run with:
- **Development**: `hss-backend-dev` service (min 0, max 3 instances, 512Mi memory)
- **Production**: `hss-backend` service (min 1, max 10 instances, 1Gi memory)
- **Cloud SQL**: PostgreSQL instance with separate dev/prod databases
- **Cloud Storage**: For file uploads (documents, images)
- **CI/CD**: Cloud Build (`cloudbuild.yaml`) triggered on develop branch (dev) and v* tags (prod)

### Configuration Profiles

- **dev** (default): Local development with Docker PostgreSQL, GCP integrations disabled, Firebase disabled
- **prod**: Production with Cloud SQL, Cloud Storage, Firebase enabled

Environment-specific configs are in `application-{profile}.yaml`. The `.env` file contains local development secrets and should never be committed to production.

## Key Patterns & Conventions

### MapStruct Usage

MapStruct is used for entity-DTO conversions. When creating new mappers:
- Use static methods (not interface mappers)
- Handle nested relationships explicitly to avoid lazy loading issues
- Example: `AnimalMapper.toResponse()`, `AnimalMapper.toEntity()`

### Exception Handling

Custom exceptions are defined in the `exception/` package:
- `ResourceNotFoundException`: Entity not found by ID or other criteria
- `DuplicateResourceException`: Unique constraint violations
- `FileStorageException`: File upload/storage errors
- `FileNotFoundException`: File retrieval errors

### Transaction Management

Services are annotated with `@Transactional` at the class level. Read-only methods use `@Transactional(readOnly = true)` for performance optimization.

### Lombok Usage

The project heavily uses Lombok:
- `@Data`: Entities and DTOs
- `@Builder`: Entities
- `@RequiredArgsConstructor`: Services and controllers for dependency injection
- `@Slf4j`: Logging

**Important**: The Maven compiler plugin is configured with annotation processors in the correct order: Spring Boot Config Processor → Lombok → MapStruct → Lombok-MapStruct binding. Do not modify this order.

### Validation

Jakarta validation annotations are used on DTOs and entities:
- `@NotBlank`, `@NotNull`, `@Size`, `@Email`, `@Pattern`, `@DecimalMin`, `@DecimalMax`
- Controllers use `@Valid` on request bodies to trigger validation

### Logging

Use SLF4J logger (available via `@Slf4j`) with appropriate log levels:
- `log.info()`: Important business operations (create, update, delete)
- `log.debug()`: Detailed debugging information
- `log.error()`: Errors and exceptions

## API Documentation

Swagger UI is available at: `http://localhost:8090/swagger-ui.html` (development)

OpenAPI configuration is in `config/OpenApiConfig.java`.

## Development Environment Setup

1. Ensure Java 21 is installed and set as `JAVA_HOME`
2. Copy `.env` file and configure local database credentials
3. Start PostgreSQL: `docker-compose up -d postgres`
4. Run the application: `.\mvnw spring-boot:run`
5. Access API at `http://localhost:8090`
6. Access Swagger at `http://localhost:8090/swagger-ui.html`
7. Access pgAdmin at `http://localhost:5050` (if started with docker-compose)

## Database Credentials (Local Development)

From `.env` and `docker-compose.yml`:
- **Database**: hss_dev
- **Host**: localhost:5432
- **Username**: hss-dev-user
- **Password**: DevPassword123!
- **pgAdmin**: admin@hss.local / admin123

## Common Issues & Solutions

### Issue: MapStruct not generating mapper implementations
**Solution**: Run `.\mvnw clean compile` to regenerate mappers. Check that annotation processor order is correct in `pom.xml`.

### Issue: Flyway migration fails
**Solution**: Check migration file naming and sequence. Use `.\mvnw flyway:info` to see current state. If needed, manually fix the `flyway_schema_history` table in the database.

### Issue: "Could not find or load main class" when running locally
**Solution**: Clean and rebuild: `.\mvnw clean package -DskipTests`, then run again.

### Issue: JWT authentication fails
**Solution**: The JWT implementation uses a hardcoded secret key for testing (`mySecretKeyForTestingPurposesOnly...`). Ensure tokens are generated and validated with the same key. For production, this should be externalized to environment variables.

### Issue: Lazy loading exceptions in DTOs
**Solution**: Ensure proper fetching strategy in repository queries or use explicit JOIN FETCH. MapStruct mappers should handle nested relationships carefully to avoid triggering lazy loads outside transaction boundaries.

## File Upload Configuration

File uploads are configured via `config/FileUploadProperties.java` and stored in Google Cloud Storage (production) or locally (development). The `StorageService` abstracts storage operations.

## Testing Strategy

- Currently minimal tests (only `HssBackendApplicationTests.java` which is a context load test)
- When adding tests, follow Spring Boot testing patterns:
  - `@SpringBootTest` for integration tests
  - `@WebMvcTest` for controller tests
  - `@DataJpaTest` for repository tests
  - Mock external dependencies (Cloud SQL, Cloud Storage, Firebase)

## Cloud Build Pipeline

The `cloudbuild.yaml` defines a multi-step CI/CD pipeline:
1. Maven dependency download (cached)
2. Test execution (parallel with build)
3. Maven build
4. Docker image build
5. Docker image push to Artifact Registry
6. Database migration (prod only)
7. Cloud Run deployment
8. Release artifacts (prod only)
9. Monitoring metrics

**Trigger configuration**:
- `develop` branch → dev environment
- `v*` tags → production environment

## Important Notes

- **Never commit secrets**: The `.env` file is in `.gitignore`. Use Google Secret Manager for production secrets.
- **Database schema changes**: Always create Flyway migrations. Never use `spring.jpa.hibernate.ddl-auto=update` in production.
- **Lazy loading**: Be cautious with JPA relationships. Use `@Transactional` appropriately and fetch data eagerly when needed to avoid LazyInitializationException.
- **API versioning**: Currently no versioning. If breaking changes are needed, consider adding `/v1/` prefix to all endpoints.
- **Pagination**: Use Spring's `Pageable` for list endpoints to avoid performance issues with large datasets.
