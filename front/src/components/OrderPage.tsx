'use client';

import { useCallback, useEffect, useState } from 'react';

const API = 'http://localhost:8080';

function toProduct(p: { id: number; name: string; price: number; stock: number; img_url: string | null }) {
  return { id: p.id, name: p.name, price: p.price, stock: p.stock, imgUrl: p.img_url ?? '' };
}
import ProductList from './ProductList';
import OrderSummary from './OrderSummary';
import LoginModal from './LoginModal';
import ProductFormModal from './ProductFormModal';
import AdminOrderView from './AdminOrderView';
import type { Product, CartItem, Order, OrderStatus } from '@/types/order';

const DUMMY_ORDERS: Order[] = [
  {
    id: 'ORD-0001',
    createdAt: new Date('2026-06-05T09:14:00'),
    items: [
      { product: { id: 1, name: '콜롬비아 나리뇨', price: 5000, stock: 0, imgUrl: '' }, quantity: 2 },
      { product: { id: 3, name: '에티오피아 예가체프', price: 7500, stock: 0, imgUrl: '' }, quantity: 1 },
    ],
    total: 17500,
    email: 'kim@gmail.com',
    address: '서울시 마포구 합정동 123-4 101호',
    zipcode: '04045',
    status: 'delivered',
  },
  {
    id: 'ORD-0002',
    createdAt: new Date('2026-06-05T14:32:00'),
    items: [
      { product: { id: 5, name: '케냐 AA', price: 7000, stock: 0, imgUrl: '' }, quantity: 1 },
    ],
    total: 7000,
    email: 'park@naver.com',
    address: '경기도 성남시 분당구 판교로 88',
    zipcode: '13529',
    status: 'delivered',
  },
  {
    id: 'ORD-0003',
    createdAt: new Date('2026-06-06T08:05:00'),
    items: [
      { product: { id: 2, name: '브라질 세하 두 카파라오', price: 6000, stock: 0, imgUrl: '' }, quantity: 3 },
      { product: { id: 4, name: '과테말라 안티구아', price: 5500, stock: 0, imgUrl: '' }, quantity: 2 },
    ],
    total: 29000,
    email: 'lee@kakao.com',
    address: '부산시 해운대구 우동 1234',
    zipcode: '48094',
    status: 'pending',
  },
  {
    id: 'ORD-0004',
    createdAt: new Date('2026-06-06T11:47:00'),
    items: [
      { product: { id: 3, name: '에티오피아 예가체프', price: 7500, stock: 0, imgUrl: '' }, quantity: 1 },
    ],
    total: 7500,
    email: 'choi@daum.net',
    address: '서울시 서초구 강남대로 123',
    zipcode: '06594',
    status: 'pending',
  },
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
  const [products, setProducts] = useState<Product[]>([]);
  const [orders, setOrders] = useState<Order[]>(DUMMY_ORDERS);
  const [cart, setCart] = useState<CartItem[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [adminTab, setAdminTab] = useState<'products' | 'orders'>('products');
  const [isLoginOpen, setIsLoginOpen] = useState(false);
  const [flyItems, setFlyItems] = useState<FlyItem[]>([]);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [isAddingProduct, setIsAddingProduct] = useState(false);

  const fetchProducts = useCallback((admin: boolean) => {
    fetch(`${API}${admin ? '/admin/products' : '/products'}`)
      .then((r) => r.json())
      .then((json) => setProducts(json.data.map(toProduct)))
      .catch(console.error);
  }, []);

  useEffect(() => { fetchProducts(false); }, [fetchProducts]);

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

  const handleSaveProduct = useCallback(async (data: Omit<Product, 'id'>) => {
    if (editingProduct) {
      const res = await fetch(`${API}/admin/products/${editingProduct.id}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: data.name, price: data.price, stock: data.stock, img_url: data.imgUrl }),
      });
      const json = await res.json();
      const updated = toProduct(json.data);
      setProducts((prev) => prev.map((p) => p.id === editingProduct.id ? updated : p));
      setCart((prev) => prev.map((item) =>
        item.product.id === editingProduct.id ? { ...item, product: updated } : item
      ));
      setEditingProduct(null);
    } else {
      const res = await fetch(`${API}/admin/products`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: data.name, price: data.price, stock: data.stock, img_url: data.imgUrl }),
      });
      const json = await res.json();
      setProducts((prev) => [...prev, toProduct(json.data)]);
      setIsAddingProduct(false);
    }
  }, [editingProduct]);

  const handleDeleteProduct = useCallback(async (productId: number) => {
    await fetch(`${API}/admin/products/${productId}`, { method: 'DELETE' });
    setProducts((prev) => prev.filter((p) => p.id !== productId));
    setCart((prev) => prev.filter((item) => item.product.id !== productId));
  }, []);

  const handleCheckout = (form: { email: string; address: string; zipcode: string }) => {
    const newOrder: Order = {
      id: `ORD-${String(orders.length + 1).padStart(4, '0')}`,
      createdAt: new Date(),
      items: cart.map((item) => ({ ...item })),
      total,
      email: form.email,
      address: form.address,
      zipcode: form.zipcode,
      status: 'pending',
    };
    setOrders((prev) => [newOrder, ...prev]);
    setCart([]);
    setIsOpen(false);
    // TODO: 주문 생성 API 연동
    console.log('주문 저장:', newOrder);
  };


  const handleLogin = (adminStatus: boolean) => {
    setIsAdmin(adminStatus);
    setIsLoginOpen(false);
    if (adminStatus) {
      setCart([]);
      setIsOpen(false);
      fetchProducts(true);
    }
  };

  const total = cart.reduce(
    (sum, item) => sum + item.product.price * item.quantity,
    0
  );

  return (
    <div className="h-screen pt-6 pb-4 px-6 flex flex-col overflow-hidden" style={{ background: 'var(--bg)' }}>
      <div className="w-full px-4 flex flex-col flex-1 overflow-hidden">
        {/* 헤더 */}
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center">
            {/* eslint-disable-next-line @next/next/no-img-element */}
            <img src="/logo.png" alt="Beantage logo" style={{ height: 80, width: 'auto' }} />
            <h1
              className="text-5xl font-bold"
              style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
            >
              Beantage
            </h1>
          </div>
          <div className="flex items-center gap-3">
            {isAdmin && (
              <span
                className="text-xs font-semibold px-3 py-1 rounded-full"
                style={{ background: 'var(--accent)', color: 'white', fontFamily: 'var(--font-body)' }}
              >
                관리자
              </span>
            )}
            <button
              onClick={() => {
                if (isAdmin) {
                  setIsAdmin(false);
                  setAdminTab('products');
                  fetchProducts(false);
                } else {
                  setIsLoginOpen(true);
                }
              }}
              className="px-5 py-2.5 rounded-xl text-sm font-semibold cursor-pointer transition-all"
              style={{
                background: 'var(--surface)',
                border: '1px solid var(--line)',
                color: 'var(--ink-soft)',
                fontFamily: 'var(--font-body)',
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.background = 'var(--ink)';
                e.currentTarget.style.color = 'var(--bg)';
                e.currentTarget.style.borderColor = 'var(--ink)';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.background = 'var(--surface)';
                e.currentTarget.style.color = 'var(--ink-soft)';
                e.currentTarget.style.borderColor = 'var(--line)';
              }}
            >
              {isAdmin ? '로그아웃' : '로그인'}
            </button>
          </div>
        </div>

        {/* 어드민 탭 */}
        {isAdmin && (
          <div className="flex gap-1 mb-3" style={{ borderBottom: '2px solid var(--line)', paddingBottom: 0 }}>
            {(['products', 'orders'] as const).map((tab) => (
              <button
                key={tab}
                onClick={() => setAdminTab(tab)}
                className="px-5 py-2.5 text-sm font-semibold cursor-pointer transition-all rounded-t-lg"
                style={{
                  background: adminTab === tab ? 'var(--surface)' : 'transparent',
                  color: adminTab === tab ? 'var(--ink)' : 'var(--muted)',
                  border: adminTab === tab ? '1px solid var(--line)' : '1px solid transparent',
                  borderBottom: adminTab === tab ? '2px solid var(--surface)' : '1px solid transparent',
                  marginBottom: adminTab === tab ? -2 : 0,
                  fontFamily: 'var(--font-body)',
                }}
              >
                {tab === 'products' ? '상품 관리' : `주문 내역 ${orders.length > 0 ? `(${orders.length})` : ''}`}
              </button>
            ))}
          </div>
        )}

        {/* 메인 레이아웃 */}
        <div style={{ display: 'flex', flex: 1, overflow: 'hidden' }}>
          <div style={{ flex: 1, minWidth: 0, display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
            {isAdmin && adminTab === 'orders' ? (
              <AdminOrderView orders={orders} />
            ) : (
              <ProductList
                products={products}
                onAdd={handleAddToCart}
                isAdmin={isAdmin}
                onAddProduct={() => setIsAddingProduct(true)}
                onEditProduct={(product) => setEditingProduct(product)}
                onDeleteProduct={handleDeleteProduct}
              />
            )}
          </div>
        </div>
      </div>

      {/* Dim 오버레이 */}
      {!isAdmin && isOpen && (
        <div
          className="fixed inset-0 z-30"
          style={{ background: 'rgba(46,31,18,0.45)', transition: 'opacity 0.3s ease' }}
          onClick={() => setIsOpen(false)}
        />
      )}

      {/* 날아가는 점들 */}
      {!isAdmin && flyItems.map((f) => (
        <FlyingDot key={f.id} startX={f.startX} startY={f.startY} />
      ))}

      {!isAdmin && (
        <OrderSummary
          cart={cart}
          isOpen={isOpen}
          onToggle={() => setIsOpen((prev) => !prev)}
          onUpdateQuantity={updateQuantity}
          total={total}
          onCheckout={handleCheckout}
        />
      )}

      {isLoginOpen && (
        <LoginModal onClose={() => setIsLoginOpen(false)} onLogin={handleLogin} />
      )}

      {(isAddingProduct || editingProduct) && (
        <ProductFormModal
          product={editingProduct ?? undefined}
          onSave={handleSaveProduct}
          onClose={() => { setIsAddingProduct(false); setEditingProduct(null); }}
        />
      )}
    </div>
  );
}
