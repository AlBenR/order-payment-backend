# Order Management System

Proyecto backend profesional construido con **Java 17 + Spring Boot**, enfocado en **arquitectura hexagonal**, **DDD** y **event-driven architecture** usando el **Transactional Outbox Pattern**.

Este proyecto está diseñado explícitamente para ser mostrado en un **CV técnico**, priorizando decisiones arquitectónicas reales por encima de ejemplos simplificados.

---

## 🧩 Problemática que resuelve

Gestiona el ciclo de vida completo de una orden:

- Creación de órdenes
- Confirmación
- Pago
- Cancelación
- Envío

Garantizando:

- Reglas de negocio consistentes
- Transiciones de estado válidas
- Emisión confiable de eventos de dominio
- Integración eventual con otros sistemas (Kafka / mensajería)

---

## 🏗️ Arquitectura

### Arquitectura Hexagonal (Ports & Adapters)

La aplicación está dividida claramente en:

- **domain** → Modelo de dominio puro (sin Spring)
- **application** → Casos de uso (services / ports in)
- **infrastructure** → Persistencia, REST, outbox, adapters

Esto permite:

- Independencia del framework
- Alta testabilidad
- Evolución segura del dominio

---

## 📦 Domain-Driven Design (DDD)

- Aggregate Root: **Order**
- Entidades de valor: `OrderItem`, `Money`
- Reglas de negocio encapsuladas en el dominio
- Eventos de dominio explícitos:
  - `OrderCreatedEvent`
  - `OrderConfirmedEvent`
  - `OrderPaidEvent`
  - `OrderCancelledEvent`
  - `OrderShippedEvent`

---

## 🔔 Event-Driven Architecture

### Transactional Outbox Pattern

Los eventos de dominio **no se publican directamente**.

Flujo:

1. El dominio genera eventos
2. Se persisten en `outbox_events` dentro de la misma transacción
3. Un **Outbox Relay** los procesa de forma asíncrona
4. Se publican a un adaptador externo (Kafka / Message Broker)

Esto garantiza:

- Consistencia transaccional
- No pérdida de eventos
- Idempotencia

---

## 🔁 Retry & Backoff

El Outbox Relay implementa:

- Reintentos automáticos
- Backoff progresivo
- Límite máximo de intentos
- Estados:
  - `PENDING`
  - `SENT`
  - `FAILED`

Diseñado para producción real.

---

## 🧪 Testing

- Tests de dominio
- Tests de application services
- Tests de controladores REST

El dominio se prueba **sin Spring**, garantizando aislamiento real.

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
```
---

