import { useEffect, useState } from 'react';
import { fetchProducts, placeOrder } from './services/api';
import type { Product, CartItem, OrderResponse } from './types';
import { ShoppingCart, Zap, CheckCircle } from 'lucide-react';

export default function App() {
  const [products, setProducts] = useState<Product[]>([]);
  const [cart, setCart] = useState<CartItem[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [activeCategory, setActiveCategory] = useState<string>('All');
  const [orderSuccess, setOrderSuccess] = useState<OrderResponse | null>(null);

  useEffect(() => {
    loadProducts();
  }, [activeCategory]);

  const loadProducts = async () => {
    setLoading(true);
    try {
      const data = await fetchProducts(activeCategory === 'All' ? undefined : activeCategory);
      setProducts(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const addToCart = (product: Product) => {
    setCart((prev) => {
      const existing = prev.find((item) => item.product.id === product.id);
      if (existing) {
        return prev.map((item) =>
          item.product.id === product.id ? { ...item, quantity: item.quantity + 1 } : item
        );
      }
      return [...prev, { product, quantity: 1 }];
    });
  };

  const handleCheckout = async () => {
    if (cart.length === 0) return;
    try {
      const orderItems = cart.map((item) => ({
        sku: item.product.sku,
        quantity: item.quantity,
        price: item.product.price,
      }));
      const order = await placeOrder('CUST-1001', orderItems);
      setOrderSuccess(order);
      setCart([]);
    } catch (err) {
      alert('Checkout failed! Insufficient stock or server error.');
    }
  };

  const cartTotal = cart.reduce((sum, item) => sum + item.product.price * item.quantity, 0);

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900">
      {/* Header Navigation */}
      <header className="bg-slate-900 text-white sticky top-0 z-50 shadow-md">
        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
          <div className="flex items-center space-x-2">
            <Zap className="h-7 w-7 text-indigo-400" />
            <h1 className="text-2xl font-bold tracking-tight">TechShop Enterprise</h1>
          </div>
          <div className="flex items-center space-x-4">
            <div className="relative">
              <ShoppingCart className="h-6 w-6 text-slate-200" />
              {cart.length > 0 && (
                <span className="absolute -top-2 -right-2 bg-indigo-500 text-white text-xs font-bold rounded-full h-5 w-5 flex items-center justify-center">
                  {cart.reduce((sum, i) => sum + i.quantity, 0)}
                </span>
              )}
            </div>
            <span className="font-semibold text-indigo-300">${cartTotal.toFixed(2)}</span>
            {cart.length > 0 && (
              <button
                onClick={handleCheckout}
                className="bg-indigo-600 hover:bg-indigo-500 text-white font-medium px-4 py-2 rounded-lg transition-all shadow-sm"
              >
                Checkout Now
              </button>
            )}
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 py-8">
        {/* Category Filters */}
        <div className="flex space-x-3 mb-8">
          {['All', 'Smartphones', 'Laptops', 'Accessories'].map((cat) => (
            <button
              key={cat}
              onClick={() => setActiveCategory(cat)}
              className={`px-4 py-2 rounded-full font-medium transition-all ${activeCategory === cat
                  ? 'bg-indigo-600 text-white shadow'
                  : 'bg-white text-slate-700 hover:bg-slate-100 border border-slate-200'
                }`}
            >
              {cat}
            </button>
          ))}
        </div>

        {/* Order Success Alert */}
        {orderSuccess && (
          <div className="mb-8 p-4 bg-emerald-50 border border-emerald-200 rounded-xl flex items-center justify-between text-emerald-800">
            <div className="flex items-center space-x-3">
              <CheckCircle className="h-6 w-6 text-emerald-600" />
              <div>
                <p className="font-bold">Order Placed Successfully!</p>
                <p className="text-sm">
                  Order Number: <span className="font-mono bg-emerald-100 px-2 py-0.5 rounded">{orderSuccess.orderNumber}</span> — Total: ${orderSuccess.totalAmount.toFixed(2)}
                </p>
              </div>
            </div>
            <button onClick={() => setOrderSuccess(null)} className="text-sm font-semibold hover:underline">Dismiss</button>
          </div>
        )}

        {/* Product Grid */}
        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {[1, 2, 3].map((n) => (
              <div key={n} className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100 animate-pulse h-64"></div>
            ))}
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {products.map((product) => (
              <div key={product.id} className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100 hover:shadow-md transition-all flex flex-col justify-between">
                <div>
                  <div className="flex justify-between items-start mb-2">
                    <span className="text-xs font-semibold uppercase tracking-wider text-indigo-600 bg-indigo-50 px-2.5 py-1 rounded-full">{product.category}</span>
                    <span className="text-xs font-mono text-slate-400">{product.sku}</span>
                  </div>
                  <h2 className="text-xl font-bold text-slate-900 mb-2">{product.name}</h2>
                  <p className="text-slate-600 text-sm mb-4">{product.description}</p>

                  {/* Tech Specs */}
                  {product.attributes && (
                    <div className="bg-slate-50 p-3 rounded-lg text-xs space-y-1 mb-4">
                      {Object.entries(product.attributes).map(([key, val]) => (
                        <div key={key} className="flex justify-between">
                          <span className="text-slate-500 font-medium">{key}:</span>
                          <span className="text-slate-800 font-semibold">{val}</span>
                        </div>
                      ))}
                    </div>
                  )}
                </div>

                <div className="flex items-center justify-between pt-4 border-t border-slate-100">
                  <div>
                    <span className="text-2xl font-extrabold text-slate-900">${product.price.toFixed(2)}</span>
                    <p className="text-xs text-slate-500">In Stock: {product.stockQuantity}</p>
                  </div>
                  <button
                    onClick={() => addToCart(product)}
                    className="bg-slate-900 hover:bg-indigo-600 text-white font-medium px-4 py-2.5 rounded-xl transition-all shadow-sm flex items-center space-x-2"
                  >
                    <ShoppingCart className="h-4 w-4" />
                    <span>Add to Cart</span>
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}