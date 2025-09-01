# Customer Flow API
Customer Flow is an API for managing customer service workflows with dynamic queues and priority levels. It allows organizations to register requests, organize them in a priority-based queue, assign them to agents, and track performance through SLAs and metrics. #CleanArchitecture #Scalability

## ✨ Features

*   **Complete Management:** CRUD operations for Customers and Attendants.
*   **Service Flow:** Creation, cancellation, assignment, and completion of service tickets.
*   **Priority Queue:** The system organizes the ticket queue based on the customer's qualification level (DEFAULT, VIP, EXPRESS).
*   **Clean Architecture:** Designed with **Hexagonal Architecture (Ports and Adapters)**, decoupling business logic from infrastructure.
*   **RESTful API:** Clear and well-defined endpoints for all operations.
*   **Internationalization:** Support for error messages in English (default) and Portuguese.

## 🏛️ Architecture

This project is built using the principles of **Hexagonal Architecture (Ports and Adapters)**. The goal is to isolate the core business logic from infrastructure details, such as the web framework or the database.

-   **`domain`**: Contains the purest business entities and rules of the system. It has no external dependencies.
-   **`application`**: Defines the "Ports" (interfaces representing what the application does) and the "Use Cases" (the implementations of these ports). It is the heart of the application's logic.
-   **`infra`**: Contains the "Adapters," which are the concrete implementations for the outside world. This is where Spring Boot controllers (adapter for the REST API), PostgreSQL repositories (adapter for the database), and other configurations reside.

This approach makes the system more testable, flexible, and easier to maintain in the long run.

## 🛠️ Tech Stack

-   **Backend:** Java 17, Spring Boot 3
-   **Database:** PostgreSQL
-   **Containerization:** Docker, Docker Compose
-   **Testing:**
    -   JUnit 5
    -   MockMvc for API layer testing
    -   Testcontainers for database integration testing

## 🚀 Getting Started

### Prerequisites

-   Docker and Docker Compose

### Running with Docker Compose

The simplest and recommended way to run the project is by using Docker Compose. It will build the application image, start the database container, and connect them automatically.

In the project root, run the following command:

```sh
docker-compose up --build
```

-   The `--build` flag is necessary for the first run or whenever there are changes to the application's source code.
-   After execution, the API will be available at `http://localhost:8080`.

To stop all services, press `CTRL + C` in the terminal where the command is running, or execute `docker-compose down` in another terminal.

### Running Locally (Development Alternative)

If you prefer to run the application directly on your machine (outside of Docker), you can still use Docker Compose to start just the database:

1.  **Start the database:**
    ```sh
    docker-compose up -d postgres-db
    ```

2.  **Run the application with Maven:**
    ```sh
    mvn spring-boot:run
    ```

## 🧪 Running Tests

To run the complete test suite (unit and integration), execute:

```sh
mvn test
```

Integration tests that require a database use **Testcontainers**. It will automatically start a temporary PostgreSQL container for each test run, ensuring a clean and isolated environment.

## 📖 API Guide

The following are the main available endpoints.

---

### Customers

#### `POST /customers`

Creates a new customer.

**Request Body:**

```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+14155552671",
  "qualifier": "DEFAULT"
}
```

**Response (201 Created):**

```json
{
  "customerId": "a1b2c3d4-e5f6-7890-1234-567890abcdef"
}
```

---

### Attendants

#### `POST /attendants`

Creates a new attendant.

**Request Body:**

```json
{
  "name": "Jane Smith",
  "email": "jane.smith@example.com"
}
```

**Response (201 Created):**

```json
{
  "attendantId": "f1e2d3c4-b5a6-7890-1234-567890abcdea"
}
```

---

### Tickets

#### `POST /tickets`

Creates a new service ticket.

**Request Body:**

```json
{
  "customerId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
  "attendantId": "f1e2d3c4-b5a6-7890-1234-567890abcdea"
}
```

**Response (201 Created):**

```json
{
  "ticketId": "12345678-abcd-efgh-ijkl-mnopqrstuvwx"
}
```

#### `POST /tickets/pull-next`

An attendant pulls the next available ticket from the queue (respecting priority).

**Request Body:**

```json
{
  "attendantId": "f1e2d3c4-b5a6-7890-1234-567890abcdea"
}
```

**Response (200 OK):**

```json
{
  "ticketId": "12345678-abcd-efgh-ijkl-mnopqrstuvwx",
  "customerId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
  "customerName": "John Doe"
}
```

#### `POST /tickets/{id}/cancel`

Cancels a ticket.

**Request Body:**

```json
{
  "reason": "Customer gave up on the service."
}
```

**Response (200 OK):**

```json
{
  "ticketId": "12345678-abcd-efgh-ijkl-mnopqrstuvwx"
}
```

#### `POST /tickets/{id}/complete`

Completes a service.

**Request Body:**

```json
{
  "attendantId": "f1e2d3c4-b5a6-7890-1234-567890abcdea",
  "description": "Problem successfully resolved."
}
```

**Response (200 OK):**

```json
{
  "ticketId": "12345678-abcd-efgh-ijkl-mnopqrstuvwx"
}
```

---

### Queue

#### `GET /queue`

Views the pending ticket queue.

**Example Response (200 OK):**

```json
[
  {
    "ticketId": "12345678-abcd-efgh-ijkl-mnopqrstuvwx",
    "customerName": "John Doe",
    "priority": 1,
    "status": "PENDING",
    "createdAt": "2023-10-27T10:30:00Z"
  }
]
```