# Order & Payment Backend System

Proyecto backend profesional construido con **Java 17 + Spring Boot**, enfocado en **arquitectura hexagonal**, **Domain-Driven Design (DDD)** y **Event-Driven Architecture**, utilizando el **Transactional Outbox Pattern** para garantizar consistencia e integración confiable entre contextos.

---

## 🧩 Problemática que resuelve

Gestiona de forma consistente el **flujo completo de órdenes y pagos**, separando responsabilidades por contexto de negocio y comunicándolos mediante eventos.

### Order Context
- Creación de órdenes
- Confirmación
- Cancelación
- Envío
- Emisión de eventos de dominio confiables

### Payment Context
- Creación de pagos a partir de órdenes confirmadas
- Asociación Payment ↔ Order
- Persistencia consistente
- Emisión de eventos de pago

Garantizando:
- Reglas de negocio explícitas
- Transiciones de estado válidas
- Consistencia transaccional
- Integración eventual entre contextos

---

## 🏗️ Arquitectura

### Arquitectura Hexagonal (Ports & Adapters)

Cada contexto está estructurado siguiendo Ports & Adapters:

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

Esto permite:
- Dominio puro (sin dependencias de Spring)
- Alta testabilidad
- Evolución segura del modelo de negocio
- Independencia del framework

---

## 📦 Domain-Driven Design (DDD)

### Order Context
- **Aggregate Root**: `Order`
- Value Objects: `OrderId`, `Money`, `OrderItem`
- Estados controlados explícitamente
- Eventos de dominio:
  - `OrderCreatedEvent`
  - `OrderConfirmedEvent`
  - `OrderCancelledEvent`
  - `OrderShippedEvent`

### Payment Context
- **Aggregate Root**: `Payment`
- Value Objects: `PaymentId`, `OrderId`, `Money`
- Eventos de dominio:
  - `PaymentCreatedEvent`
  - `PaymentFailedEvent`

Las reglas de negocio viven **dentro del dominio**, no en servicios anémicos.

---

## 🔔 Event-Driven Architecture

### Transactional Outbox Pattern

Los eventos de dominio **no se publican directamente**.

Flujo:
1. El dominio genera eventos
2. Se persisten en la tabla `outbox_events` dentro de la misma transacción
3. Un **Outbox Processor** los consume de forma asíncrona
4. Se publican a adaptadores externos (Kafka / Message Broker / integración simulada)

Beneficios:
- Consistencia transaccional
- No pérdida de eventos
- Publicación desacoplada
- Base sólida para microservicios

---

## 🔁 Retry, Backoff y Estados

El Outbox implementa:
- Reintentos automáticos
- Control de intentos
- Backoff basado en tiempo
- Estados explícitos:
  PENDING
  SENT
  FAILED
  
---

## 🔗 Integración entre contextos

- El **Order Context** emite `OrderConfirmedEvent`
- El **Payment Context** lo consume mediante un adapter inbound
- Se crea un `Payment` de forma eventual y consistente
- Se evita acoplamiento directo entre contextos

Esto refleja una **integración realista entre bounded contexts**.

---

## 🧪 Testing

- Tests de dominio (sin Spring)
- Tests de application services
- Tests de controladores REST
- Validación de reglas de negocio y transiciones de estado

El dominio se prueba de forma **aislada**, garantizando verdadera independencia.

---

## 🛠️ Stack Tecnológico

- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL / H2
- Maven
- Lombok
- JUnit 5

---

## 🚀 Cómo ejecutar

```bash
mvn spring-boot:run


