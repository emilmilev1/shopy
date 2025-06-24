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
    id: string;
    customer: string;
    datePlaced: string;
    items: OrderItem[];
    status: string;
};

export type ApiRoute = {
    orderId: string;
    route: {
        from: [number, number];
        to: [number, number];
    }[];
};
