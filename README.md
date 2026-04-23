# Task Management REST API

A complete, production-ready Task Management REST API built with Spring Boot, Spring Security & JWT authentication, and Hibernate/JPA. This is a portfolio project designed demonstrating clean architecture, solid code quality, and best practices.

## Tech Stack
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![JWT](https://img.shields.io/badge/JWT-Tokens-black)

## Features
- **User Authentication**: Secure JWT-based Login and Registration.
- **Role-Based Access**: Users can only manage and view their OWN tasks.
- **CRUD Operations**: Complete management of Tasks (Create, Read, Update, Delete).
- **Advanced Filtering & Sorting**: Filter Tasks by priority or status and sort by due dates easily.
- **Global Error Handling**: Standardized error responses across the application.
- **Input Validation**: Utilizing Jakarta Validation API for clean and secure inputs.

## Prerequisites
- **Java 17** or higher
- **Maven** 3.8+
- **MySQL** installed locally or running in Docker

## Local Setup

1. **Create the Database in MySQL:**
   ```sql
   CREATE DATABASE taskmanager_db;
   ```

2. **Configure Environment Variables (or update `application.properties`):**
   ```properties
   DB_URL=jdbc:mysql://localhost:3306/taskmanager_db
   DB_USERNAME=root
   DB_PASSWORD=your_password
   ```

3. **Build and Run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## Schema Reference (ASCII)
```
+----------------+          +----------------+
|     users      |          |     tasks      |
+----------------+          +----------------+
| id (PK)        |<---------| user_id (FK)   |
| name           |          | id (PK)        |
| email (UNIQUE) |          | title          |
| password       |          | description    |
| created_at     |          | status         |
|                |          | priority       |
+----------------+          | due_date       |
                            | created_at     |
                            | updated_at     |
                            +----------------+
```

## API Endpoints

### Authentication Group (No Token)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Authenticate and obtain JWT |

### Tasks Group (Requires Bearer Token)
| Method | Endpoint | Description |
|---|---|---|
| POST   | `/api/tasks` | Create a new task |
| GET    | `/api/tasks` | Get all tasks (Filters apply) |
| GET    | `/api/tasks/{id}` | Get task by ID |
| PUT    | `/api/tasks/{id}` | Update existing task |
| DELETE | `/api/tasks/{id}` | Delete existing task |

### Filtering & Sorting Commands
- `GET /api/tasks?status=TODO`
- `GET /api/tasks?priority=HIGH`
- `GET /api/tasks?status=TODO&priority=HIGH`
- `GET /api/tasks?sortBy=dueDate`
- `GET /api/tasks?sortBy=priority`

## Sample JSON Payloads

### Registration Request
```json
{
  "name": "Praneeth",
  "email": "test@gmail.com",
  "password": "pass123"
}
```

### Create Task Request
```json
{
  "title": "Complete Backend Logic",
  "description": "Finalize Spring Security configuration and task assignments.",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2026-06-15"
}
```

## Testing via Postman
1. Create a `POST` request to `/api/auth/login` to get the Bearer Token.
2. In all `/api/tasks` endpoints, add an `Authorization` header with value `Bearer {YOUR_TOKEN}` in the Postman Authorization tab.

## Deployment Instructions (Railway/Render)
1. Provide the following Env Variables on your cloud PAAS:
   - `DB_URL`: Postgres/MySQL connection string provided by the loud provider.
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `JWT_SECRET`
2. Define the exact start command: `mvn spring-boot:run` or just run the bundled standard `java -jar target/taskmanager-0.0.1-SNAPSHOT.jar`
