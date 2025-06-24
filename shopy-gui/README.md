# Shopy - Warehouse Management System

An application for managing warehouse operations, including product inventory, order processing, and route tracking.

## Features

- **Dashboard**: Overview of warehouse operations
- **Product Management**: CRUD operations for inventory items
- **Order Tracking**: Monitor order status and view picking routes
- **Real-time Bot View**: Live visualization of warehouse bot movements
- **API Integration**: Connects to Spring Boot backend

## Tech Stack

- **React 18** with TypeScript
- **MUI Components** for styling
- **Fetch API** for backend communication
- **Modern ES6+** features

## Project Structure

```
src/
├── components/          # React components
│   ├── Header.tsx      # Navigation header
│   ├── Dashboard.tsx   # Main dashboard view
│   ├── Products.tsx    # Product management
│   ├── Orders.tsx      # Order tracking
├── types/              # TypeScript type definitions
│   └── index.ts        # All type definitions
├── services/           # API services
│   └── api.ts          # Backend API communication
└── App.tsx             # Main application component
```

## Getting Started

### Prerequisites

- Spring Boot backend running on `http://localhost:8080/api`

### Installation

1. **Install dependencies**
```bash
npm install
```

2. **Start the development server**
```bash
npm start
```

3. **Open your browser**
Navigate to `http://localhost:3000`

## Configuration

### Environment Variables

Create `.env.development` file for configuration:
```env
REACT_APP_API_URL=http://localhost:8080/api
```

## API Endpoints

The frontend connects to these backend endpoints:

### Products
- `GET /api/products` - List all products
- `POST /api/products` - Create new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Orders
- `POST /api/orders` - Create new order
- `GET /api/orders/{id}` - Get specific order status

### Routes
- `GET /api/routes?orderId={id}` - Get picking route for order

## Data Flow

1. **App Load**: Fetches products and orders from API
2. **User Actions**: Triggers API calls for CRUD operations
3. **Real-time Updates**: Refreshes data after operations
4. **Error Handling**: Graceful fallback to mock data

---