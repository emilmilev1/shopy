# Warehouse Management System

A Spring Boot application that simulates a warehouse environment with inventory management, order processing, and automated picking route calculation.

## Features

### Core Functionality
- **Product Management**: CRUD operations for products with warehouse locations (X, Y)
- **Order Processing**: Place orders with automatic stock validation
- **Route Optimization**: Calculate optimal picking routes for warehouse bots
- **Status Tracking**: Real-time order status (SUCCESS/FAIL)
- **Stock Management**: Automatic inventory reduction upon order fulfillment

### Bonus Features
- **Web Server**: RESTful API with Spring Boot
- **Database Integration**: PostgreSQL with JPA/Hibernate and Flyway migrations
- **Sample Data**: Automatic initialization of 15 products and 7 sample orders

## Technical Stack

- **Backend**: Spring Boot 3.5.0 with Java 17
- **Database**: PostgreSQL with JPA/Hibernate
- **Migrations**: Flyway for database schema management
- **API**: RESTful endpoints with JSON responses

## Quick Start

### Prerequisites
- Java 17+
- PostgreSQL 12+
- Maven 3.6+

### Setup

1. **Create Database**
```bash
createdb Shopy
```

2. **Configure Database** (update `src/main/resources/application.properties`)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/Shopy
spring.datasource.username=postgres
spring.datasource.password=your_password
```

3. **Run Application**
```bash
mvn spring-boot:run
```

4. **Verify Installation**
```bash
curl http://localhost:8080/api/products
```

## API Endpoints

### Products
- `POST /api/products` - Create product
- `GET /api/products` - List all products
- `GET /api/products/{id}` - Get specific product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Orders
- `POST /api/orders` - Place new order
- `GET /api/orders/{id}` - Check order status

### Routes
- `GET /api/routes?orderId={id}` - Get picking route

## Sample Data

The application automatically creates:
- **15 Products**: Beverages, fruits, dairy, meat, grains, vegetables
- **7 Sample Orders**: 6 successful orders, 1 failed order
- **Calculated Routes**: Optimal picking paths for each order

## Configuration

### Application Properties
```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/Shopy
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.flyway.enabled=true
```

## Project Structure

```
src/main/java/org/example/shopyapi/
├── controller/          # REST API endpoints
├── service/            # Business logic
├── repository/         # Data access layer
├── model/              # JPA entities
├── dto/                # Data transfer objects
└── config/             # Configuration classes
```