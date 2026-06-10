'use client';

import { useCallback, useEffect, useState } from 'react';
import ProductList from './ProductList';
import LoginModal from './LoginModal';
import OrderSummary from './OrderSummary';
import ProductFormModal from './ProductFormModal';
import AdminOrderView from './AdminOrderView';
import OrderConfirmModal, { type OrderConfirmData } from './OrderConfirmModal';
import type { Product, CartItem, AdminOrder } from '@/types/order';

const API = 'http://localhost:8080/api/v1';

function toProduct(p: { id: number; name: string; price: number; stock: number; imgUrl: string | null }) {
  return { id: p.id, name: p.name, price: p.price, stock: p.stock, imgUrl: p.imgUrl ?? '' };
}

function toAdminOrder(o: { id: number; deliveryId: number; email: string; address: string; totalPrice: number; status: string; createdAt: string }): AdminOrder {
  return { id: o.id, deliveryId: o.deliveryId, email: o.email, address: o.address, totalPrice: o.totalPrice, status: o.status, createdAt: o.createdAt };
}


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
  const [cart, setCart] = useState<CartItem[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [isLoginOpen, setIsLoginOpen] = useState(false);
  const [adminTab, setAdminTab] = useState<'products' | 'orders'>('products');
  const [flyItems, setFlyItems] = useState<FlyItem[]>([]);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [isAddingProduct, setIsAddingProduct] = useState(false);
  const [adminOrders, setAdminOrders] = useState<AdminOrder[]>([]);
  const [orderConfirm, setOrderConfirm] = useState<OrderConfirmData | null>(null);
  const [checkoutError, setCheckoutError] = useState<string>('');

  const fetchProducts = useCallback((admin: boolean) => {
    fetch(`${API}${admin ? '/admin/products' : '/products'}`)
      .then((r) => r.json())
      .then((json) => setProducts(json.data.map(toProduct)))
      .catch(console.error);
  }, []);

  useEffect(() => { fetchProducts(false); }, [fetchProducts]);

  const fetchAdminOrders = useCallback(() => {
    fetch(`${API}/admin/orders`)
      .then((r) => r.json())
      .then((json) => setAdminOrders(json.data.map(toAdminOrder)))
      .catch(console.error);
  }, []);

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
        body: JSON.stringify({ name: data.name, price: data.price, stock: data.stock, imgUrl: data.imgUrl }),
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
        body: JSON.stringify({ name: data.name, price: data.price, stock: data.stock, imgUrl: data.imgUrl }),
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

  const handleCheckout = async (form: { email: string; address: string; zipcode: string }) => {
    const fullAddress = `${form.zipcode} ${form.address}`.trim();
    const body = {
      email: form.email,
      address: fullAddress,
      orderItems: cart.map((item) => ({ productId: item.product.id, amount: item.quantity })),
    };
    try {
      const res = await fetch(`${API}/orders`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });
      const json = await res.json();
      if (res.ok) {
        setCheckoutError('');
        setOrderConfirm(json.data);
        setCart([]);
        setIsOpen(false);
      } else if (res.status === 409) {
        setCheckoutError(json.message || '재고가 부족한 상품이 있습니다.');
      }
    } catch (err) {
      console.error('주문 생성 실패:', err);
    }
  };

  const handleLogin = (adminStatus: boolean) => {
    setIsAdmin(adminStatus);
    setIsLoginOpen(false);
    if (adminStatus) {
      setCart([]);
      setIsOpen(false);
      fetchProducts(true);
      fetchAdminOrders();
    }
  };

  const total = cart.reduce(
    (sum, item) => sum + item.product.price * item.quantity,
    0
  );

  return (
    <div className={`h-screen pt-6 pb-4 px-6 flex flex-col overflow-hidden${isAdmin ? ' admin-mode' : ''}`} style={{ background: isAdmin ? '#302820' : 'var(--bg)', transition: 'background 0.4s ease' }}>
      <div className="w-full px-4 flex flex-col flex-1 overflow-hidden" style={{ maxWidth: 1600, margin: '0 auto' }}>
        {/* 헤더 */}
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-3">
            {/* eslint-disable-next-line @next/next/no-img-element */}
            <img src="/logo.png" alt="Beantage logo" style={{ height: 80, width: 'auto' }} />
            <h1
              className="text-5xl font-bold"
              style={{ fontFamily: 'var(--font-display)', color: isAdmin ? '#f5f0e8' : 'var(--ink)', transition: 'color 0.4s ease' }}
            >
              Beantage
            </h1>
            {isAdmin && (
              <span
                className="text-xl font-bold tracking-widest px-4 py-2 rounded-xl self-center"
                style={{
                  background: 'var(--accent)',
                  color: 'white',
                  fontFamily: 'var(--font-body)',
                  letterSpacing: '0.15em',
                  marginTop: 8,
                }}
              >
                ADMIN
              </span>
            )}
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
                  setAdminOrders([]);
                  fetchProducts(false);
                } else {
                  setIsLoginOpen(true);
                }
              }}
              className="px-5 py-2.5 rounded-xl text-sm font-semibold cursor-pointer transition-all"
              style={{
                background: isAdmin ? '#4a3e2e' : 'var(--surface)',
                border: isAdmin ? '1px solid #6a5840' : '1px solid var(--line)',
                color: isAdmin ? '#c8b89a' : 'var(--ink-soft)',
                fontFamily: 'var(--font-body)',
                transition: 'background 0.4s ease, border-color 0.4s ease, color 0.4s ease',
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.background = isAdmin ? '#6a5840' : 'var(--ink)';
                e.currentTarget.style.color = isAdmin ? '#f5f0e8' : 'var(--bg)';
                e.currentTarget.style.borderColor = isAdmin ? '#8a7858' : 'var(--ink)';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.background = isAdmin ? '#4a3e2e' : 'var(--surface)';
                e.currentTarget.style.color = isAdmin ? '#c8b89a' : 'var(--ink-soft)';
                e.currentTarget.style.borderColor = isAdmin ? '#6a5840' : 'var(--line)';
              }}
            >
              {isAdmin ? '로그아웃' : '로그인'}
            </button>
          </div>
        </div>

        {/* 어드민 탭 */}
        {isAdmin && (
          <div className="flex gap-1 mb-3" style={{ borderBottom: '2px solid #504235', paddingBottom: 0 }}>
            {(['products', 'orders'] as const).map((tab) => (
              <button
                key={tab}
                onClick={() => { setAdminTab(tab); if (tab === 'orders') fetchAdminOrders(); }}
                className="px-5 py-2.5 text-sm font-semibold cursor-pointer transition-all rounded-t-lg"
                style={{
                  background: adminTab === tab ? '#3c3228' : 'transparent',
                  color: adminTab === tab ? '#f5f0e8' : '#a08060',
                  border: adminTab === tab ? '1px solid #504235' : '1px solid transparent',
                  borderBottom: adminTab === tab ? '2px solid #3c3228' : '1px solid transparent',
                  marginBottom: adminTab === tab ? -2 : 0,
                  fontFamily: 'var(--font-body)',
                }}
              >
                {tab === 'products' ? '상품 관리' : `주문 내역 ${adminOrders.length > 0 ? `(${adminOrders.length})` : ''}`}
              </button>
            ))}
          </div>
        )}

        {/* 메인 레이아웃 */}
        <div style={{ display: 'flex', flex: 1, overflow: 'hidden' }}>
          <div style={{ flex: 1, minWidth: 0, display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
            {isAdmin && adminTab === 'orders' ? (
              <AdminOrderView orders={adminOrders} />
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

      {orderConfirm && (
        <OrderConfirmModal data={orderConfirm} onClose={() => setOrderConfirm(null)} />
      )}

      {checkoutError && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center"
          style={{ background: 'rgba(46,31,18,0.5)' }}
          onClick={() => setCheckoutError('')}
        >
          <div
            style={{
              background: 'var(--surface)',
              borderRadius: 'var(--radius)',
              boxShadow: '0 24px 72px rgba(46,31,18,.22)',
              width: 400,
              padding: '40px',
              position: 'relative',
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex items-center gap-3 mb-5">
              <div
                className="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0"
                style={{ background: '#fef2f2', border: '1px solid #fecaca' }}
              >
                <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
                  <path d="M9 5v5" stroke="#dc2626" strokeWidth="2" strokeLinecap="round"/>
                  <circle cx="9" cy="13" r="1" fill="#dc2626"/>
                  <circle cx="9" cy="9" r="7.5" stroke="#dc2626" strokeWidth="1.5"/>
                </svg>
              </div>
              <div>
                <h2 className="text-base font-bold" style={{ color: 'var(--ink)' }}>재고 부족</h2>
                <p className="text-xs mt-0.5" style={{ color: 'var(--muted)' }}>주문을 완료할 수 없습니다</p>
              </div>
            </div>

            <p className="text-sm mb-6" style={{ color: 'var(--ink-soft)', lineHeight: 1.6 }}>
              {checkoutError}
            </p>

            <button
              onClick={() => setCheckoutError('')}
              className="w-full py-3.5 rounded-xl text-sm font-semibold cursor-pointer transition-all"
              style={{ background: 'var(--ink)', color: 'var(--bg)', border: 'none' }}
              onMouseEnter={(e) => { e.currentTarget.style.filter = 'brightness(1.15)'; }}
              onMouseLeave={(e) => { e.currentTarget.style.filter = 'none'; }}
            >
              확인
            </button>
          </div>
        </div>
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
