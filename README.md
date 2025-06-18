# 🛒 Spring Boot E-Commerce API

A full-featured e-commerce backend built with Spring Boot, providing secure authentication, product management, order processing, and user profile features. Designed with RESTful APIs and scalable service-layer architecture.

---

## 🚀 Features

- 🔐 JWT-based Authentication & Role-based Authorization
- 🧾 RESTful APIs for Products, Orders, and Users
- 📦 Inventory Management
- 📧 Email verification
- 🧪 JUnit Unit Testing
- 🐘 MySQL / PostgreSQL compatible

---

## 📂 Project Structure

```bash
springboot-ecommerce/
├── pom.xml
├── mvnw / mvnw.cmd
├── HELP.md
├── insertDummyDevData.sql
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/backend/ecommerce/
│   │   │       ├── EcommerceApplication.java
│   │   │       ├── api/               # Controllers, DTOs, Security
│   │   │       ├── service/           # Business logic
│   │   │       ├── model/             # Entities, DAOs
│   │   │       └── exception/         # Custom exceptions
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       ├── java/                      # Unit tests
│       └── resources/
└── .gitignore
```
---

## 🧪 Running Locally

Clone the repository:
```bash
git clone https://github.com/your-username/springboot-ecommerce.git
cd springboot-ecommerce
```

Run the app:
```bash
./mvnw spring-boot:run
```

## 📬 API Endpoints Overview
| Method | Endpoint       | Description                  |
|--------|----------------|------------------------------|
| POST   | /auth/login    | Authenticate user            |
| POST   | /auth/register | Register a new user          |
| GET    | /products      | Get all products             |
| POST   | /orders        | Place a new order            |
| GET    | /users/me      | Fetch logged-in user profile |
