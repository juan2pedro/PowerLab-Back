# copilot-instructions.md — StrengthLab Backend

## Proyecto

API REST para la aplicación de powerlifting **StrengthLab**.  
Stack: **Spring Boot + PostgreSQL + MapStruct + SpringDoc/OpenAPI 3.0**

El frontend Angular consume esta API. El contrato completo está en `Open_API_Swagger`.

---

## Contexto de dominio

### Aggregate Roots (tienen Controller propio)

- `Exercise` — catálogo de movimientos/variantes
- `TrainingSessionTemplate` — día de programación (bloque/semana/día)
- `WorkoutSession` — sesión real ejecutada en una fecha

### Entidades dependientes (sin Controller propio)

- `TrainingSetTemplate` — set pautado dentro de un día de programación
- `WorkoutEntry` — registro real por ejercicio dentro de una sesión
- `WorkoutSet` — serie individual dentro de un entry

### Reglas de dominio clave

- `Exercise.baseName` y `Exercise.mainPattern` son obligatorios.
- `TrainingSetTemplate.displayOrder` es obligatorio.
- `WorkoutSession` puede existir sin plantilla (entreno libre).
- `WorkoutEntry` siempre pertenece a una `WorkoutSession`.

---

## API REST

Base URL local: `http://localhost:8080`

| Recurso | Prefijo |
|---------|---------|
| Exercises | `/api/exercises` |
| Training Sessions | `/api/training-sessions` |
| Workouts | `/api/workouts` |

---

## Decisiones de diseño (ADRs)

- **ADR-0001**: PostgreSQL. Integridad referencial y consultas por rango/filtro.
- **ADR-0002**: Auth OIDC — **fuera del MVP, no implementar**.
- **ADR-0003**: Controllers solo para aggregate roots. DTOs en request/response. MapStruct para mapeo. **No exponer entidades JPA directamente.**

---

## Estructura del proyecto

```
config/          → AppConfig, OpenApiConfig
controllers/     → ExerciseController, TrainingSessionController, WorkoutController,
                   ErrorHandlerExceptionController
exceptions/      → ResourceNotFoundException
models/
  domain/        → entidades JPA
  dto/
    exercise/
    trainingsessiontemplate/
    trainigsettemplate/
    workoutentry/
    workoutsession/
    workoutset/
  ApiErrorResponse
mappers/         → interfaces MapStruct
repositories/    → ExerciseRepository, TrainingSessionTemplateRepository,
                   TrainingSetTemplateRepository, WorkoutEntryRepository,
                   WorkoutSessionRepository, WorkoutSetRepository
services/        → interfaces: ExerciseService, TrainingSessionService, WorkoutService
                   impls:  ExerciseServiceImpl, TrainingSessionServiceImpl, WorkoutServiceImpl
```

### Reglas de ubicación

- Nueva entidad JPA → `models/domain/`
- Nuevo DTO → `models/dto/<aggregate>/`
- Nuevo mapper → `mappers/`
- Nueva lógica de negocio → `services/` (interfaz + impl)
- Nuevo endpoint → `controllers/` **solo si es aggregate root**
- Nueva excepción → `exceptions/`

---

## Antes de realizar cualquier cambio

Verificar obligatoriamente antes de escribir o modificar código:

1. **Ubicación correcta** — el archivo va en la carpeta que le corresponde según la estructura de arriba.
2. **Single Responsibility** — cada clase tiene una única razón de cambio.
3. **Open/Closed** — extender sin modificar lo existente cuando sea posible.
4. **Dependency Inversion** — inyectar siempre por interfaz, nunca por la clase `Impl`.
5. **Clean Code** — nombres descriptivos, métodos pequeños, sin código muerto ni comentarios obvios.
6. **Dirección de dependencias** — `controller → service (interfaz) → serviceImpl → repository`. No saltarse capas.
7. **No duplicar lógica** — reutilizar lo existente antes de crear algo nuevo.

Si un cambio viola alguno de estos puntos, proponer la solución correcta antes de implementar.

---

## Convenciones

- Controllers reciben y devuelven **DTOs**, nunca entidades JPA.
- Mapeos entidad ↔ DTO exclusivamente con **MapStruct**.
- Validaciones con Bean Validation en los DTOs de request.
- Lógica de negocio en la capa `service`, nunca en el controller.
- Los servicios se inyectan siempre por su **interfaz**.
