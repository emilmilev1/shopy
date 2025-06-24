import axios from "axios";
import { Product, Order } from "../types";

const API_BASE_URL = process.env.REACT_APP_API_URL;

// axios instance with base config
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

api.interceptors.request.use(
    (config: any) => {
        const token = localStorage.getItem("jwt_token");
        if (token && config.headers) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error: any) => {
        return Promise.reject(error);
    }
);

api.interceptors.response.use(
    (response: any) => response,
    (error: any) => {
        if (error.response?.status === 401) {
            localStorage.removeItem("jwt_token");
            localStorage.removeItem("user");
            window.location.href = "/login";
        }
        return Promise.reject(error);
    }
);

export interface User {
    id: number;
    name: string;
    email: string;
    telephone: string;
    address: string;
}

export interface JwtResponse {
    token: string;
    user: User;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    name: string;
    email: string;
    password: string;
    telephone: string;
    address: string;
}

export interface Route {
    orderId: number;
    status: string;
    visitedLocations: number[][];
}

export interface Point {
    x: number;
    y: number;
}

// Auth API
export const authAPI = {
    login: async (credentials: LoginRequest): Promise<JwtResponse> => {
        const response = await api.post<JwtResponse>(
            "/auth/login",
            credentials
        );
        const { token, user } = response.data;

        localStorage.setItem("jwt_token", token);
        localStorage.setItem("user", JSON.stringify(user));

        return response.data;
    },

    register: async (userData: RegisterRequest): Promise<JwtResponse> => {
        const response = await api.post<JwtResponse>(
            "/auth/register",
            userData
        );
        const { token, user } = response.data;

        // Store token and user data
        localStorage.setItem("jwt_token", token);
        localStorage.setItem("user", JSON.stringify(user));

        return response.data;
    },

    logout: () => {
        localStorage.removeItem("jwt_token");
        localStorage.removeItem("user");
    },

    getCurrentUser: (): User | null => {
        const userStr = localStorage.getItem("user");
        return userStr ? JSON.parse(userStr) : null;
    },

    isAuthenticated: (): boolean => {
        return !!localStorage.getItem("jwt_token");
    },

    getToken: (): string | null => {
        return localStorage.getItem("jwt_token");
    },
};

// Products API
export const productsAPI = {
    getAll: async (): Promise<Product[]> => {
        const response = await api.get<Product[]>("/api/products");
        return response.data;
    },

    getById: async (id: number): Promise<Product> => {
        const response = await api.get<Product>(`/api/products/${id}`);
        return response.data;
    },

    create: async (product: Omit<Product, "id">): Promise<Product> => {
        const response = await api.post<Product>("/api/products", product);
        return response.data;
    },

    update: async (
        id: number,
        updateData: {
            newQuantity: number;
            newPrice: number;
            newLocation: { x: number; y: number };
        }
    ): Promise<Product> => {
        const response = await api.put<Product>(
            `/api/products/${id}`,
            updateData
        );
        return response.data;
    },

    delete: async (id: number): Promise<void> => {
        await api.delete(`/api/products/${id}`);
    },
};

// Orders API
export const ordersAPI = {
    getAll: async (): Promise<Order[]> => {
        const response = await api.get<Order[]>("/api/orders");
        return response.data;
    },

    getStatusById: async (id: number): Promise<Order> => {
        const response = await api.get<Order>(`/api/orders/${id}`);
        return response.data;
    },

    create: async (orderData: any): Promise<Order> => {
        const response = await api.post<Order>("/api/orders", orderData);
        return response.data;
    },
};

// Routes API
export const routesAPI = {
    getAll: async (): Promise<Route[]> => {
        const response = await api.get<Route[]>("/api/routes");
        return response.data;
    },

    getById: async (orderId: number): Promise<Route> => {
        const response = await api.get<Route>(`/api/routes?orderId=${orderId}`);
        return response.data;
    },
};

export default api;
