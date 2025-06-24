export type Product = {
    id: string | number;
    name: string;
    price: number;
    quantity: number;
    location: { x: number; y: number };
};

export type OrderItem = {
    productId?: string;
    productName?: string;
    quantity: number;
};

export type Order = {
    id: string | number;
    items: OrderItem[];
    status: string;
    createdAt?: string;
};

export type Route = {
    orderId: number;
    status: string;
    visitedLocations: number[][];
};
