'use client';

import { useState } from 'react';
import ProductList from './ProductList';
import OrderSummary from './OrderSummary';
import type { Product, CartItem } from '@/types/order';

const DUMMY_PRODUCTS: Product[] = [
  { id: 1, name: '커피콩', origin: 'Columbia Nariñó',         price: 5000, imageUrl: '' },
  { id: 2, name: '커피콩', origin: 'Brazil Serra Do Caparaó', price: 6000, imageUrl: '' },
  { id: 3, name: '커피콩', origin: 'Ethiopia Yirgacheffe',    price: 7500, imageUrl: '' },
  { id: 4, name: '커피콩', origin: 'Guatemala Antigua',       price: 5500, imageUrl: '' },
  { id: 5, name: '커피콩', origin: 'Kenya AA',                price: 7000, imageUrl: '' },
  { id: 6, name: '커피콩', origin: 'TEST COFFEE',             price: 7000, imageUrl: '' },
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

  const hasCart = cart.length > 0;

  return (
    <div className="min-h-screen pt-6 pb-14 px-6 flex flex-col" style={{ background: 'var(--bg)' }}>
      <div className="w-full px-4 flex flex-col flex-1">
        {/* 헤더 */}
        <div className="flex items-center mb-3" style={{ gap: 0 }}>
          {/* eslint-disable-next-line @next/next/no-img-element */}
          <img src="/logo.png" alt="Beantage logo" style={{ height: 80, width: 'auto' }} />
          <h1
            className="text-5xl font-bold"
            style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
          >
            Beantage
          </h1>
        </div>

        {/* 메인 레이아웃 */}
        <div style={{ display: 'flex', gap: 24, alignItems: 'stretch', flex: 1 }}>
          <div style={{ flex: hasCart ? '3 3 0%' : '1 1 auto', minWidth: 0, display: 'flex', flexDirection: 'column' }}>
            <ProductList products={DUMMY_PRODUCTS} onAdd={addToCart} />
          </div>
          {hasCart && (
            <div style={{ flex: '2 2 0%', minWidth: 280 }}>
              <OrderSummary
                cart={cart}
                onUpdateQuantity={updateQuantity}
                total={total}
                onCheckout={handleCheckout}
              />
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
