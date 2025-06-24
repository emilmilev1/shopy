# Warehouse Management System

A full-stack warehouse management application with a Spring Boot backend and React frontend, featuring inventory management, order processing, and automated picking route calculation.

## Project Structure

This repository contains two main applications:

- **[Shopy-API](./Shopy-API/)** - Spring Boot backend API
- **[shopy-gui](./shopy-gui/)** - React frontend application
- **[files](./files/)** - Additional resources including Postman collection

## Features

### Core Functionality
- **Product Management**: CRUD operations for products with warehouse locations (X, Y)
- **Order Processing**: Place orders with automatic stock validation
- **Route Optimization**: Calculate optimal picking routes for warehouse bots
- **Status Tracking**: Real-time order status (SUCCESS/FAIL)
- **Stock Management**: Automatic inventory reduction upon order fulfillment
- **User Authentication**: JWT-based authentication with user-specific data isolation

### Frontend Features
- **Modern UI**: Material-UI components with responsive design
- **User Management**: Registration, login, and logout functionality
- **Real-time Updates**: Immediate UI updates without page refresh
- **Protected Routes**: Authentication-based route protection
- **User-specific Data**: Each user only sees their own products and orders

### Bonus Features
- **Web Server**: RESTful API with Spring Boot
- **Database Integration**: PostgreSQL with JPA/Hibernate and Flyway migrations
- **Sample Data**: Automatic initialization of user-specific products and orders
- **API Testing**: Postman collection for testing endpoints

## Technical Stack

### Backend
- **Framework**: Spring Boot with Java 17
- **Database**: PostgreSQL with JPA/Hibernate
- **Migrations**: Flyway for database schema management
- **Security**: Spring Security with JWT authentication
- **API**: RESTful endpoints with JSON responses

### Frontend
- **Framework**: React 18 with TypeScript
- **UI Library**: Material-UI (MUI)
- **Routing**: React Router v6
- **State Management**: React hooks and context
- **HTTP Client**: Axios with interceptors

## Quick Start

### Prerequisites
- Java 17+
- PostgreSQL 12+
- Maven 3.6+

### Backend Setup

1. **Navigate to the backend directory**
```bash
cd Shopy-API
```

2. **Create Database**
```bash
createdb Shopy
```

3. **Configure Database** (update `src/main/resources/application.properties`)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/Shopy
spring.datasource.username=postgres
spring.datasource.password=your_password
```

4. **Run Backend Application**
```bash
mvn spring-boot:run
```

5. **Verify Backend Installation**
```bash
curl http://localhost:8080/api/products
```

### Frontend Setup

1. **Navigate to the frontend directory**
```bash
cd shopy-gui
```

2. **Install Dependencies**
```bash
npm install
```

3. **Configure API URL** (create `.env` file)
```env
REACT_APP_API_URL=http://localhost:8080
```

4. **Run Frontend Application**
```bash
npm start
```

5. **Access the Application**
Open [http://localhost:3000](http://localhost:3000) in your browser

## API Testing

### Postman Collection
There is a collection available in the `files` folder:
- **File**: `files/Shopy.postman_collection.json`
- **Import**: Import this file into Postman to test all API endpoints
- **Features**: Pre-configured requests for all CRUD operations, authentication, and route testing

## API Endpoints

### Authentication
- `POST /auth/register` - Register new user
- `POST /auth/login` - User login

### Products
- `POST /api/products` - Create product
- `GET /api/products` - List all products (user-specific)
- `GET /api/products/{id}` - Get specific product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Orders
- `POST /api/orders` - Place new order
- `GET /api/orders` - List all orders (user-specific)
- `GET /api/orders/{id}` - Check order status

### Routes
- `GET /api/routes?orderId={id}` - Get picking route

## Sample Data

The application automatically creates:
- **5 Sample Users**: Alice, Bob, Carol, David, Eva
- **User-specific Products**: Each user gets their own set of products
- **Sample Orders**: Successful and failed orders for each user
- **Calculated Routes**: Optimal picking paths for each order

## Configuration

### Backend Application Properties
```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/Shopy
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.flyway.enabled=true
app.initialize.data=true
```

### Frontend Environment Variables
```env
REACT_APP_API_URL=http://localhost:8080
```

## Project Structure

### Backend Structure
```
Shopy-API/src/main/java/org/example/shopyapi/
├── controller/          # REST API endpoints
├── service/            # Business logic
├── repository/         # Data access layer
├── model/              # JPA entities
├── dto/                # Data transfer objects
```

## Development

### Running Both Applications
1. Start the backend: `cd Shopy-API && mvn spring-boot:run`
2. Start the frontend: `cd shopy-gui && npm start`
3. Access the application at [http://localhost:3000](http://localhost:3000)

### Testing
- Use the Postman collection in `files/Shopy.postman_collection.json`
- Test user registration and login flows
- Verify user-specific data isolation
- Test order creation and route calculation
