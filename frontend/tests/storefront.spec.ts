import { test, expect } from '@playwright/test';

test('User can browse products, add to cart, and checkout', async ({ page }) => {
    // 1. Navigate to Storefront
    await page.goto('http://localhost:5173');

    // 2. Verify Header Title
    await expect(page.locator('h1')).toContainText('TechShop Enterprise');

    // 3. Click Add to Cart on first product
    const addToCartButton = page.locator('button:has-text("Add to Cart")').first();
    await addToCartButton.click();

    // 4. Click Checkout Now
    const checkoutButton = page.locator('button:has-text("Checkout Now")');
    await checkoutButton.click();

    // 5. Verify Order Success Banner
    await expect(page.locator('text=Order Placed Successfully!')).toBeVisible();
});