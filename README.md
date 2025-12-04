# Notification System

A REST Application that saves notifications and sends them to specific users

## Application Tech Stack

- **Java:** 17
- **Spring Boot:** 3.4.0
- **Build Tool:** Maven
- **Database:** MySQL, H2 (Testing)
- **Backend Framework:** Spring MVC
- **Frontend:** None
- **ORM:** Spring Data JPA
- **Security:** Spring Security
- **Logging:** SLF4J / Logback
- **Testing:** H2Database, Spring Test, Jacoco (For measuring Tests Line coverage)
- **Server port** 8081

## Settings and description

### Endpoints

- **Main endpoint** - (`/notification/v1`)
- **Other endpoints:**
    - Post - `/user` - save user that you receive from the Main Application
    - Post - `/notification` - save notification to the database, received from the Main Application
    - Get - `/notifications/{UUID}` - get notifications by User receiver
    - Get - `/notifications/title/{String}` - get notifications by Title
    - Get - `/notification/{UUID}` - get single notification by id
    - Delete - `/notification/{UUID}` - delete single notification by id

### Security Info

- CSRF is used
- User authentication used for the endpoints

## Databases

- **Database:** MySQL
- **ORM:** Spring Data JPA
- **Primary Keys:** UUID
- **Integration Tests** H2Database
- **Entity Relationships:**
    - Notification ↔ User (Many-to-One) (Sender and Receiver)

### Entities

1. **Notification** - notifications sent to the users.
2. **User** - Users using the application, that have either sent or received a notification

## Logging

- logger.info - used in Post and Delete functions
- logger.error - when an operation has failed in completing.

## Error Handling

- **GlobalExceptionHandler** - returns response, if an error is caught. Handles:
    - NoResourceFoundException - for invalid urls
    - UnknownElementException - custom exception - when a row in the database is not found
    - Exception - used for any other exceptions thrown
- **ErrorResponse** - For sending specific error message with HTTP code

## Structure

```
Game-Pool/
├── logs  # keeps recent errors that were caught by the logger
├── src/
│   ├── main/
│   │   ├── java/main/
│   │   │   ├── exception/      # Exception Handler and custom exceptions
│   │   │   ├── model/          # Entities
│   │   │   ├── repository/     # JPA Repositories
│   │   │   ├── security/       # contains SecurityFilterChain for authentication build
│   │   │   ├── service/        # Entity Services
│   │   │   └── web/            # Controllers of the Application and DTO`s
│   │   │       └── dto/        # Data Transfer Objects
│   │   └── resources/          # properties for the application and tests
│   └── test/                   # All the tests
└── pom.xml
```

## Integrations

- **Spring Security**
- **Spring Data JPA**
- **MySQL**