'use client';

import { useEffect, useState } from 'react';
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

interface FlyItem {
  id: string;
  startX: number;
  startY: number;
}

function FlyingDot({ startX, startY }: { startX: number; startY: number }) {
  const [transform, setTransform] = useState(
    `translate(${startX - 14}px, ${startY - 14}px) scale(1)`
  );
  const [opacity, setOpacity] = useState(0.9);
  const [transitioning, setTransitioning] = useState(false);

  useEffect(() => {
    const targetX = window.innerWidth - 40 - 180;
    const targetY = window.innerHeight - 24;
    const raf = requestAnimationFrame(() =>
      requestAnimationFrame(() => {
        setTransitioning(true);
        setTransform(`translate(${targetX - 14}px, ${targetY - 14}px) scale(0.3)`);
        setOpacity(0);
      })
    );
    return () => cancelAnimationFrame(raf);
  }, []);

  return (
    <div
      style={{
        position: 'fixed',
        left: 0,
        top: 0,
        width: 28,
        height: 28,
        borderRadius: '50%',
        background: 'var(--accent)',
        opacity,
        transform,
        transition: transitioning
          ? 'transform 0.55s cubic-bezier(0.4, 0.2, 0.2, 1), opacity 0.4s ease 0.15s'
          : 'none',
        zIndex: 99,
        pointerEvents: 'none',
      }}
    />
  );
}

export default function OrderPage() {
  const [cart, setCart] = useState<CartItem[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [flyItems, setFlyItems] = useState<FlyItem[]>([]);

  const handleAddToCart = (product: Product, e: React.MouseEvent<HTMLButtonElement>) => {
    const rect = e.currentTarget.getBoundingClientRect();
    const flyId = `fly-${Date.now()}-${product.id}`;

    setFlyItems((prev) => [...prev, {
      id: flyId,
      startX: rect.left + rect.width / 2,
      startY: rect.top + rect.height / 2,
    }]);

    setTimeout(() => {
      setFlyItems((prev) => prev.filter((f) => f.id !== flyId));
    }, 750);

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
    <div className="h-screen pt-6 pb-4 px-6 flex flex-col overflow-hidden" style={{ background: 'var(--bg)' }}>
      <div className="w-full px-4 flex flex-col flex-1 overflow-hidden">
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
        <div style={{ display: 'flex', flex: 1, overflow: 'hidden' }}>
          <div style={{ flex: 1, minWidth: 0, display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
            <ProductList products={DUMMY_PRODUCTS} onAdd={handleAddToCart} />
          </div>
        </div>
      </div>

      {/* Dim 오버레이 */}
      {isOpen && (
        <div
          className="fixed inset-0 z-30"
          style={{
            background: 'rgba(46,31,18,0.45)',
            transition: 'opacity 0.3s ease',
          }}
          onClick={() => setIsOpen(false)}
        />
      )}

      {/* 날아가는 점들 */}
      {flyItems.map((f) => (
        <FlyingDot key={f.id} startX={f.startX} startY={f.startY} />
      ))}

      <OrderSummary
        cart={cart}
        isOpen={isOpen}
        onToggle={() => setIsOpen((prev) => !prev)}
        onUpdateQuantity={updateQuantity}
        total={total}
        onCheckout={handleCheckout}
      />
    </div>
  );
}
