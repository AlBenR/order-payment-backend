# Order & Payment Backend System

Proyecto backend **profesional** construido con **Java 17 + Spring Boot**, diseñado para demostrar dominio real de **arquitectura hexagonal**, **Domain-Driven Design (DDD)**, **Event-Driven Architecture**, **seguridad con JWT**, y **buenas prácticas de testing**.

El sistema modela un flujo realista de **órdenes y pagos**, con separación clara de responsabilidades por **bounded context** y comunicación desacoplada mediante eventos confiables usando el **Transactional Outbox Pattern**.

---

## 🧩 Problemática que resuelve

Gestiona de forma consistente y segura el **ciclo completo de vida de una orden**, desde su creación hasta el pago y envío, garantizando:

- Reglas de negocio explícitas
- Transiciones de estado válidas
- Consistencia transaccional
- Seguridad por roles
- Integración eventual entre contextos

---

## 🧠 Bounded Contexts

### 🛒 Order Context
Responsable del ciclo de vida de las órdenes:

- Creación de órdenes
- Confirmación
- Cancelación
- Pago
- Envío
- Emisión de eventos de dominio confiables

### 💳 Payment Context
Responsable de los pagos asociados a órdenes confirmadas:

- Creación de pagos
- Asociación `Payment ↔ Order`
- Persistencia consistente
- Emisión de eventos de pago

### 🔐 Auth Context
Responsable de la autenticación y autorización:

- Registro y gestión de usuarios
- Roles (`CUSTOMER`, `ADMIN`)
- Autenticación mediante **JWT**
- Inicialización automática de usuario administrador
- Separación completa del dominio de órdenes y pagos

---

## 🏗️ Arquitectura

### Arquitectura Hexagonal (Ports & Adapters)

Cada contexto sigue **Ports & Adapters**, manteniendo el dominio completamente aislado de frameworks:

```
domain
├─ model
├─ event
├─ ports
│ ├─ in
│ └─ out
application
├─ service
infrastructure
├─ repository
├─ outbox
├─ adapters
└─ rest
```


Beneficios clave:
- Dominio puro (sin dependencias de Spring)
- Alta testabilidad
- Evolución segura del modelo
- Bajo acoplamiento
- Preparado para microservicios

---

## 📦 Domain-Driven Design (DDD)

### Order Context
- **Aggregate Root**: `Order`
- Value Objects: `OrderId`, `Money`, `OrderItem`
- Estados explícitos y controlados
- Eventos de dominio:
  - `OrderCreatedEvent`
  - `OrderConfirmedEvent`
  - `OrderCancelledEvent`
  - `OrderPaidEvent`
  - `OrderShippedEvent`

### Payment Context
- **Aggregate Root**: `Payment`
- Value Objects: `PaymentId`, `OrderId`, `Money`
- Eventos de dominio:
  - `PaymentCreatedEvent`
  - `PaymentFailedEvent`

Las reglas de negocio viven **en el dominio**, no en servicios anémicos.

---

## 🔔 Event-Driven Architecture

### Transactional Outbox Pattern

Los eventos de dominio **no se publican directamente**.

Flujo:
1. El dominio genera eventos
2. Se persisten en la tabla `outbox_events` dentro de la misma transacción
3. Un **Outbox Processor** los consume de forma asíncrona
4. Se publican a adaptadores externos (simulados)

Beneficios:
- Consistencia transaccional
- No pérdida de eventos
- Publicación desacoplada
- Base sólida para sistemas distribuidos

---

## 🔁 Retry, Backoff y Estados del Outbox

El Outbox implementa:
- Reintentos automáticos
- Control de intentos
- Backoff basado en tiempo
- Estados explícitos:
  - `PENDING`
  - `SENT`
  - `FAILED`

---

## 🔐 Seguridad (Spring Security + JWT)

El sistema implementa seguridad **realista y profesional**:

- Autenticación stateless con **JWT**
- Roles:
  - `CUSTOMER`
  - `ADMIN`
- Control de acceso por endpoint
- Separación clara entre:
  - Seguridad
  - Dominio
  - Casos de uso

### Reglas de acceso (ejemplo)
- `CUSTOMER` puede:
  - Crear órdenes
  - Consultar sus órdenes
- `ADMIN` puede:
  - Visualizar todas las ordenes.
  - Enviar órdenes

La lógica de autorización **no contamina el dominio**.

---

## 🔑 Modelo de Usuario

- `AuthenticatedUser` como modelo de seguridad compartido
- Extracción del usuario autenticado mediante `CurrentUserProvider`
- Adaptación limpia entre Spring Security y el dominio

---

## 🧪 Testing

Cobertura de pruebas a múltiples niveles:

- ✅ Tests de dominio (sin Spring)
- ✅ Tests de application services
- ✅ Tests de controladores REST
- ✅ Tests de seguridad con `@WebMvcTest` y `spring-security-test`

Separación clara entre:
- **Controller Tests** (funcionalidad)
- **Security Tests** (autorización y autenticación)

Esto garantiza:
- Dominio verdaderamente aislado
- Seguridad verificable
- Alta confianza en el comportamiento del sistema

---

## 🛠️ Stack Tecnológico

- Java 17
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- PostgreSQL / H2
- Maven
- Lombok
- JUnit 5
- Mockito

---

## 🚀 Cómo ejecutar el proyecto

```bash
mvn spring-boot:run
