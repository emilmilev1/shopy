import { Product, Order, OrderItem } from "../types";

const API_BASE_URL = process.env.REACT_APP_API_URL;

export class ApiService {
    // Products
    static async getProducts(): Promise<Product[]> {
        const response = await fetch(`${API_BASE_URL}/products`);
        if (!response.ok) {
            throw new Error("Failed to fetch products");
        }
        return response.json();
    }

    static async createProduct(product: Omit<Product, "id">): Promise<Product> {
        const response = await fetch(`${API_BASE_URL}/products`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(product),
        });
        if (!response.ok) {
            throw new Error("Failed to create product");
        }
        return response.json();
    }

    static async updateProduct(
        id: number,
        updates: Partial<Product>
    ): Promise<Product> {
        const response = await fetch(`${API_BASE_URL}/products/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                newQuantity: updates.quantity,
                newPrice: updates.price,
                newLocation: updates.location,
            }),
        });
        if (!response.ok) {
            throw new Error("Failed to update product");
        }
        return response.json();
    }

    static async deleteProduct(id: number): Promise<void> {
        const response = await fetch(`${API_BASE_URL}/products/${id}`, {
            method: "DELETE",
        });
        if (!response.ok) {
            throw new Error("Failed to delete product");
        }
    }

    // Orders
    static async getOrders(): Promise<Order[]> {
        const res = await fetch(`${API_BASE_URL}/orders`);
        if (!res.ok) {
            throw new Error("Failed to fetch orders");
        }
        return res.json();
    }

    static async createOrder(items: OrderItem[]): Promise<Order> {
        const response = await fetch(`${API_BASE_URL}/orders`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ items }),
        });
        if (!response.ok) {
            throw new Error("Failed to create order");
        }
        return response.json();
    }

    static async getOrderStatus(orderId: number) {
        const response = await fetch(`${API_BASE_URL}/orders/${orderId}`);
        if (!response.ok) {
            throw new Error("Failed to fetch order");
        }
        return response.json();
    }

    // Routes
    static async getOrderRoute(orderId: number) {
        const response = await fetch(
            `${API_BASE_URL}/routes?orderId=${orderId}`
        );
        if (!response.ok) {
            throw new Error("Route not found");
        }
        return response.json();
    }
}
