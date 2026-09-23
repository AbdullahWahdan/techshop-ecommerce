import http from 'k6/http';
import { check, sleep } from 'k6';

// Benchmark Target: Handle 2000 concurrent virtual users (VUs)
export const options = {
    stages: [
        { duration: '30s', target: 500 },   // Ramp up to 500 users
        { duration: '1m', target: 2000 },  // Ramp up to 2000 users!
        { duration: '2m', target: 2000 },  // Sustain 2000 concurrent users for 2 mins
        { duration: '30s', target: 0 },     // Ramp down to 0 users
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],  // 95% of requests must complete under 500ms
        http_req_failed: ['rate<0.01'],    // Error rate must be less than 1%
    },
};

const BASE_URL = 'http://localhost:8000/api/v1';

export default function () {
    // Scenario 1: Browse Product Catalog (Hits Redis Cache)
    const productsRes = http.get(`${BASE_URL}/products`);
    check(productsRes, {
        'Products status is 200': (r) => r.status === 200,
        'Response time < 200ms': (r) => r.timings.duration < 200,
    });

    sleep(1);

    // Scenario 2: Check Inventory Stock
    const inventoryRes = http.get(`${BASE_URL}/inventory/APP-IPH15P-256`);
    check(inventoryRes, {
        'Inventory status is 200': (r) => r.status === 200,
    });

    sleep(1);
}