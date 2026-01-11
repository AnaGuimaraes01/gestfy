# Gestfy - AI Agent Development Guide

## Architecture Overview

**Gestfy** is a full-stack sales and business management system for small food businesses (ice cream shops, snack bars). It uses:
- **Backend**: Spring Boot 3.2.5 + Java 17 + PostgreSQL + JPA/Hibernate
- **Frontend**: Vanilla HTML/JS (no framework) + static CSS
- **Communication**: REST API (`/api/*` endpoints)

### Core Modules
1. **Clientes** (Customers): Personal data, orders relationship
2. **Produtos** (Products): Catalog with prices, images
3. **Pedidos** (Orders): Order management with items, payment status
4. **Estoque** (Inventory): Stock levels, movements tracking
5. **Caixa** (Cash Register): Daily cash flow, financial records
6. **Relatórios** (Reports): Analytics and financial insights

## Key Architectural Patterns

### Backend Structure
- **Models** (`models/`): JPA entities with `@Entity` annotations, relationships via `@OneToMany`, `@ManyToOne`; many use Lombok `@Getter`/`@Setter` (optional)
- **DTOs** (`dto/`): Request records (e.g., `ClienteRequest`) with Jakarta Validation (`@NotBlank`, `@Email`), response records/POJOs for API contracts
- **Controllers** (`controllers/`): RESTful endpoints with `@RequestBody` validation, `ResponseEntity` for status codes; all use `@CrossOrigin(origins = "*")`
- **Repositories** (`repositories/`): Spring Data JPA interfaces, use simple methods like `findAll()`, `findById()`, custom finders like `findByData()` when needed
- **Config** (`config/`): `EnvConfig` loads `.env` via dotenv-java library for PostgreSQL credentials

### Frontend-Backend Contract
- Frontend uses `fetch()` API with JSON payloads
- Base URL: `https://gestfy-backend.onrender.com/api/{resource}` (hardcoded in JS files; development uses `localhost:8080`)
- Response format: Array of DTOs or single DTO object
- Error handling: Check `response.ok`, catch exceptions, display errors to user via DOM elements (e.g., `msg.textContent`)
- CORS enabled on all controllers via `@CrossOrigin(origins = "*")`

### Data Flow Example (Produtos)
```
produtos.js (fetch) → ProdutoController (/api/produtos) 
→ ProdutoRepository.findAll() → JPA → DTO mapping 
→ JSON response → JS updates DOM
```

## Development Workflows

### Building Backend
```bash
cd backend
./mvnw clean package  # Windows: mvnw.cmd
```

### Running Backend
```bash
# Requires .env file with: DB_URL, DB_USERNAME, DB_PASSWORD
./mvnw spring-boot:run
```

### Database Setup
- PostgreSQL driver: `org.postgresql:postgresql`
- DDL mode: `spring.jpa.hibernate.ddl-auto=update` (auto-updates schema)
- Connection via environment variables in `.env`

### Frontend Development
- Open HTML files directly in browser (static files, no build needed)
- Two main areas: `admin/` (management panel) and `cliente/` (customer area)
- API URLs hardcoded as `https://gestfy-backend.onrender.com/api/{resource}` (update to `localhost:8080` for local development)
- CSS: `frontend/admin/css/style.css` and `frontend/cliente/css/style.css` (separate stylesheets per area)

## Code Conventions & Patterns

### DTO Naming & Validation
- **Request DTOs**: Use `record` (immutable, e.g., `ClienteRequest`) with `@Valid` annotation
- **Response DTOs**: Use regular classes with getters/setters (e.g., `ClienteDTO`)
- **Validation**: Always use `@NotBlank`, `@Email` etc. on request DTOs; messages in Portuguese

**Example** (`ClienteRequest.java`):
```java
public record ClienteRequest(
    @NotBlank(message = "O nome do cliente é obrigatório")
    String nome,
    @Email
    String email
) {}
```

### Model-DTO Conversion
- DTOs can optionally use `fromEntity()` factory method (e.g., `ClienteDTO.fromEntity(cliente)`) for reusability
- Stream + collect pattern for lists: `repository.findAll().stream().map(c -> new DTO(...)).collect(Collectors.toList())`
- Single entity mapping: `repository.findById(id).map(DTO::new).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build())`

### HTTP Status Codes
- `POST` with creation: Return `HttpStatus.CREATED` with `ResponseEntity.status(HttpStatus.CREATED)`
- Success responses wrap DTOs in `ResponseEntity`

### Relationships & Cascades
- Use `@JsonIgnore` on collection fields to prevent circular serialization (e.g., `Pedido.itens` list)
- Use `@JsonManagedReference`/`@JsonBackReference` for bidirectional relationships (e.g., `PedidoItem` ↔ `Pedido`)
- Use `CascadeType.ALL` + `orphanRemoval = true` for order items

### Environment Configuration
- Use `.env` file (ignored in git) for database credentials
- `EnvConfig` class loads `.env` into Spring properties (via `EnvironmentPostProcessor`)
- Access via `${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}` in `application.properties`

## Common Tasks

### Adding a New REST Endpoint
1. Create `Request` record in `dto/{resource}/` with validations
2. Create `Response` DTO class in same folder
3. Add `@PostMapping` or `@GetMapping` to `controllers/{Resource}Controller`
4. Inject `{Resource}Repository` via constructor
5. Map model to DTO in controller; return `ResponseEntity.status(...)`
6. Add corresponding `fetch()` in `frontend/admin/js/{resource}.js` or `frontend/cliente/js/{resource}.js`

### Adding a New Model Entity
1. Create class in `models/` with `@Entity` + `@Table(name = "...")` 
2. Define relationships with `@OneToMany`/`@ManyToOne` + `@JoinColumn`
3. Add getters/setters for all fields
4. For one-to-many collections: set initial value (e.g., `new ArrayList<>()`)
5. Create repository extending `JpaRepository<Model, Long>` in `repositories/`

### Frontend List Display Pattern
```javascript
async function listar{Resource}() {
  const response = await fetch(API_URL);
  const items = await response.json();
  
  list.innerHTML = "";
  items.forEach(item => {
    const li = document.createElement("li");
    li.innerHTML = `<strong>${item.campo}</strong>`;
    list.appendChild(li);
  });
}
```

## File Organization Reference

- **Backend entry**: `backend/src/main/java/com/empresa/gestfy/GestfyApplication.java`
- **REST controllers**: `backend/src/main/java/com/empresa/gestfy/controllers/`
- **Database models**: `backend/src/main/java/com/empresa/gestfy/models/`
- **Repositories**: `backend/src/main/java/com/empresa/gestfy/repositories/`
- **DTOs**: `backend/src/main/java/com/empresa/gestfy/dto/` (organized by resource: `cliente/`, `produto/`, `pedido/`, `caixa/`, `estoque/`, `relatorios/`)
- **Config (Env)**: `backend/src/main/java/com/empresa/gestfy/config/EnvConfig.java`
- **Frontend admin panel**: `frontend/admin/index.html` with `frontend/admin/js/`
- **Frontend customer area**: `frontend/cliente/index.html` with `frontend/cliente/js/`

## Critical Integration Points

1. **PostgreSQL Connection**: Requires `.env` file with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` in project root
2. **CORS**: All controllers use `@CrossOrigin(origins = "*")` to allow frontend (file:// or deployed) to access backend
3. **DateTime Handling**: Use `LocalDateTime` for timestamps (e.g., `Pedido.data`), `LocalDate` for dates (e.g., `Caixa.data`)
4. **JSON Serialization**: `@JsonIgnore` prevents circular refs on collections; `@JsonManagedReference`/`@JsonBackReference` for order-item relationships
5. **Frontend API URLs**: Hardcoded in each JS file; for local dev, replace `https://gestfy-backend.onrender.com/api` with `http://localhost:8080/api`

## Dependencies Note

Key dependencies beyond Spring Boot:
- **Lombok**: `org.projectlombok:lombok` (optional, used for `@Getter`/`@Setter` on some models)
- **dotenv-java**: `io.github.cdimascio:dotenv-java` 3.0.0 (loads `.env` file in `EnvConfig`)
- **Jakarta Validation**: Built-in with Spring Boot for DTO validation

---

**Last Updated**: January 11, 2026 | **Java**: 17 | **Spring Boot**: 3.2.5 | **Frontend**: Vanilla JS
