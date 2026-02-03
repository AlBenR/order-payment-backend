# Order & Payment Backend System

Backend system built with **Java 17 + Spring Boot**, focused on **clean architecture**, **DDD**, **event-driven integration**, **JWT security**, and **AWS deployment**.

The project models a real-world **order and payment flow**, designed to demonstrate production-ready backend practices.

## 🚀 Key Features

- Hexagonal Architecture (Ports & Adapters)
- Domain-Driven Design (DDD)
- Event-Driven Architecture with Transactional Outbox
- Secure authentication with JWT and role-based authorization
- Separation by bounded contexts (Order, Payment, Auth)
- PostgreSQL persistence
- Deployed and tested on AWS (EC2 + RDS)
- Unit and integration testing across layers

## 🧠 Bounded Contexts

- **Order**: order lifecycle management (create, confirm, cancel, pay, ship)
- **Payment**: payment processing linked to confirmed orders
- **Auth**: authentication, authorization and user management (JWT, roles)

## 🏗️ Architecture

The system follows **Hexagonal Architecture**, keeping the domain fully isolated from frameworks.

Key benefits:
- High testability
- Low coupling
- Clean separation of concerns
- Ready for microservices evolution

## 🔔 Event-Driven Design

- Domain events persisted using the **Transactional Outbox Pattern**
- Asynchronous event processing
- Retry, backoff and explicit event states (PENDING, SENT, FAILED)
- Reliable and consistent integration between bounded contexts

## 🔐 Security

- Stateless authentication using JWT
- Role-based authorization (CUSTOMER, ADMIN)
- Security fully decoupled from domain logic

## ☁️ AWS Deployment

- EC2 used to run the Spring Boot application
- RDS PostgreSQL used as external database
- Network isolation using Security Groups
- Application exposed on port 8080
- Database access restricted to EC2 security group only

Infrastructure can be recreated easily; currently stopped to avoid costs.

## 🛠️ Tech Stack

- Java 17
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven
- JUnit 5 / Mockito
- AWS EC2 & RDS

## ▶️ Run Locally

```bash
mvn spring-boot:run
