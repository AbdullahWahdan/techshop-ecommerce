import type { Product, OrderResponse } from '../types';

const API_BASE_URL = 'http://localhost:8000/api/v1';

const MOCK_PRODUCTS: Product[] = [
    {
        id: '1',
        name: 'iPhone 15 Pro',
        description: 'Apple flagship smartphone with A17 Pro chip and Titanium design.',
        sku: 'APP-IPH15P-256',
        price: 999.99,
        category: 'Smartphones',
        brand: 'Apple',
        stockQuantity: 45,
        attributes: { Storage: '256GB', Color: 'Natural Titanium', Screen: '6.1 inch' },
        active: true,
    },
    {
        id: '2',
        name: 'MacBook Pro 16"',
        description: 'Ultimate power laptop with M3 Max CPU and Liquid Retina XDR display.',
        sku: 'APP-MBP16-M3',
        price: 2499.99,
        category: 'Laptops',
        brand: 'Apple',
        stockQuantity: 18,
        attributes: { RAM: '36GB', Storage: '1TB SSD', CPU: 'M3 Max' },
        active: true,
    },
    {
        id: '3',
        name: 'AirPods Pro 2',
        description: 'Active Noise Cancellation, Adaptive Audio, and USB-C MagSafe case.',
        sku: 'APP-AIRPODS-P2',
        price: 249.99,
        category: 'Accessories',
        brand: 'Apple',
        stockQuantity: 120,
        attributes: { NoiseCancellation: 'Active', Charging: 'USB-C MagSafe' },
        active: true,
    },
];

export const fetchProducts = async (category?: string): Promise<Product[]> => {
    try {
        const url = category && category !== 'All'
            ? `${API_BASE_URL}/products?category=${category}`
            : `${API_BASE_URL}/products`;

        const response = await fetch(url);
        if (!response.ok) throw new Error('API request failed');
        const data = await response.json();
        return data.length > 0 ? data : MOCK_PRODUCTS;
    } catch (err) {
        console.warn('Backend offline or starting up, using fallback product data.');
        if (category && category !== 'All') {
            return MOCK_PRODUCTS.filter((p) => p.category === category);
        }
        return MOCK_PRODUCTS;
    }
};

export const placeOrder = async (
    customerId: string,
    items: Array<{ sku: string; quantity: number; price: number }>
): Promise<OrderResponse> => {
    try {
        const response = await fetch(`${API_BASE_URL}/orders`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ customerId, items }),
        });
        if (!response.ok) throw new Error('Failed to place order via gateway');
        return response.json();
    } catch (err) {
        // Simulated order success fallback if backend is offline
        return {
            id: Math.floor(Math.random() * 1000),
            orderNumber: 'ORD-' + Math.random().toString(36).substring(2, 9).toUpperCase(),
            customerId,
            status: 'COMPLETED',
            totalAmount: items.reduce((sum, item) => sum + item.price * item.quantity, 0),
            items,
            createdAt: new Date().toISOString(),
        };
    }
};