'use client';

import { useState } from 'react';
import ProductList from './ProductList';
import OrderSummary from './OrderSummary';
import type { Product, CartItem } from '@/types/order';

const DUMMY_PRODUCTS: Product[] = [
  { id: 1, name: '커피콩', origin: 'Columbia Nariñó', price: 5000, imageUrl: '' },
  { id: 2, name: '커피콩', origin: 'Brazil Serra Do Caparaó', price: 6000, imageUrl: '' },
  { id: 3, name: '커피콩', origin: 'Ethiopia Yirgacheffe', price: 7500, imageUrl: '' },
  { id: 4, name: '커피콩', origin: 'Guatemala Antigua', price: 5500, imageUrl: '' },
  { id: 5, name: '커피콩', origin: 'Kenya AA', price: 7000, imageUrl: '' },
];

export default function OrderPage() {
  const [cart, setCart] = useState<CartItem[]>([]);

  const addToCart = (product: Product) => {
    setCart((prev) => {
      const existing = prev.find((item) => item.product.id === product.id);
      if (existing) {
        return prev.map((item) =>
          item.product.id === product.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
      }
      return [...prev, { product, quantity: 1 }];
    });
  };

  const updateQuantity = (productId: number, delta: number) => {
    setCart((prev) =>
      prev
        .map((item) =>
          item.product.id === productId
            ? { ...item, quantity: item.quantity + delta }
            : item
        )
        .filter((item) => item.quantity > 0)
    );
  };

  const total = cart.reduce(
    (sum, item) => sum + item.product.price * item.quantity,
    0
  );

  const handleCheckout = (form: { email: string; address: string; zipcode: string }) => {
    // TODO: 주문 생성 API 연동
    console.log('주문 데이터:', { cart, ...form, total });
  };

  return (
    <div className="min-h-screen py-14 px-6" style={{ background: 'var(--bg)' }}>
      <div className="max-w-4xl mx-auto">
        {/* 헤더 */}
        <div className="text-center mb-12">
          <p className="text-xs font-semibold tracking-[0.3em] uppercase mb-3" style={{ color: 'var(--accent)' }}>
            Specialty Coffee
          </p>
          <h1
            className="text-5xl font-bold tracking-tight"
            style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
          >
            Grids & Circle
          </h1>
          <div className="mt-4 w-12 h-px mx-auto" style={{ background: 'var(--line)' }} />
        </div>

        {/* 메인 레이아웃 */}
        <div className="grid grid-cols-5 gap-6 items-start">
          <div className="col-span-3">
            <ProductList products={DUMMY_PRODUCTS} onAdd={addToCart} />
          </div>
          <div className="col-span-2">
            <OrderSummary
              cart={cart}
              onUpdateQuantity={updateQuantity}
              total={total}
              onCheckout={handleCheckout}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
