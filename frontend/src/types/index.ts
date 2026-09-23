export interface Product {
    id: string;
    name: string;
    description: string;
    sku: string;
    price: number;
    category: string;
    brand: string;
    stockQuantity: number;
    attributes: Record<string, string>;
    active: boolean;
}

export interface CartItem {
    product: Product;
    quantity: number;
}

export interface OrderResponse {
    id: number;
    orderNumber: string;
    customerId: string;
    status: string;
    totalAmount: number;
    items: Array<{
        sku: string;
        quantity: number;
        price: number;
    }>;
    createdAt: string;
}