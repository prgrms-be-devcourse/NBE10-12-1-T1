'use client';

import { useState } from 'react';
import type { AdminOrder } from '@/types/order';

const API = 'http://localhost:8080/api/v1';

const STATUS_STYLE: Record<string, { background: string; color: string }> = {
  '결제 완료':    { background: '#fef9c3', color: '#854d0e' },
  '상품 준비 중': { background: '#dbeafe', color: '#1e40af' },
  '배송 중':     { background: '#ede9fe', color: '#5b21b6' },
  '배송 완료':   { background: '#dcfce7', color: '#166534' },
};

function formatAddress(address: string) {
  const match = address.match(/^(\d{5})\s(.+)$/);
  if (match) return { zipcode: match[1], street: match[2] };
  return { zipcode: null, street: address };
}

interface OrderItem {
  name: string;
  amount: number;
  price: number;
}

interface Props {
  orders: AdminOrder[];
}

export default function AdminOrderView({ orders }: Props) {
  const [expandedId, setExpandedId] = useState<number | null>(null);
  const [itemsCache, setItemsCache] = useState<Record<number, OrderItem[]>>({});

  const handleExpand = async (orderId: number) => {
    if (expandedId === orderId) {
      setExpandedId(null);
      return;
    }
    setExpandedId(orderId);
    if (!itemsCache[orderId]) {
      try {
        const res = await fetch(`${API}/admin/orders/${orderId}/order-items`);
        const json = await res.json();
        setItemsCache((prev) => ({ ...prev, [orderId]: json.data }));
      } catch {
        setItemsCache((prev) => ({ ...prev, [orderId]: [] }));
      }
    }
  };

  // delivery_id 기준으로 그루핑
  const groups = orders.reduce<Record<number, AdminOrder[]>>((acc, order) => {
    if (!acc[order.deliveryId]) acc[order.deliveryId] = [];
    acc[order.deliveryId].push(order);
    return acc;
  }, {});

  const groupEntries = Object.entries(groups).sort(([a], [b]) => Number(b) - Number(a));

  return (
    <div
      className="rounded-2xl flex-1 overflow-hidden flex flex-col"
      style={{
        background: 'var(--surface)',
        border: '1px solid var(--line)',
        boxShadow: '0 1px 2px rgba(46,31,18,.04), 0 8px 28px rgba(46,31,18,.06)',
      }}
    >
      {/* 툴바 */}
      <div
        className="flex items-center px-6 py-4 flex-shrink-0"
        style={{ borderBottom: '1px solid var(--line)' }}
      >
        <span className="text-sm font-semibold" style={{ color: 'var(--ink)' }}>
          전체 {orders.length}건
        </span>
        <span className="text-sm font-semibold ml-2" style={{ color: 'var(--muted)' }}>
          · 배송요청건 {groupEntries.length}건
        </span>
      </div>

      {/* 주문 목록 */}
      <div className="flex-1 overflow-y-auto scroll-area px-6 py-4">
        {orders.length === 0 ? (
          <div className="h-full flex items-center justify-center">
            <p className="text-sm" style={{ color: 'var(--muted)' }}>아직 접수된 주문이 없습니다.</p>
          </div>
        ) : (
          <div className="flex flex-col gap-4">
            {groupEntries.map(([deliveryId, groupOrders]) => {
              const isCombined = groupOrders.length > 1;
              return (
                <div key={deliveryId}>
                  {/* 합배송 헤더 */}
                  {isCombined && (
                    <div
                      className="flex items-center gap-2 px-3 py-1.5 rounded-t-xl mb-0"
                      style={{ background: 'var(--accent)', borderRadius: '10px 10px 0 0' }}
                    >
                      <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
                        <rect x="1" y="1" width="4" height="4" rx="1" fill="white" opacity="0.8"/>
                        <rect x="7" y="1" width="4" height="4" rx="1" fill="white" opacity="0.8"/>
                        <rect x="1" y="7" width="4" height="4" rx="1" fill="white" opacity="0.8"/>
                        <rect x="7" y="7" width="4" height="4" rx="1" fill="white" opacity="0.8"/>
                      </svg>
                      <span className="text-xs font-bold" style={{ color: 'white', letterSpacing: '0.05em' }}>
                        합배송 #{deliveryId} · {groupOrders.length}건
                      </span>
                    </div>
                  )}

                  {/* 그룹 내 주문들 */}
                  <div
                    style={{
                      borderRadius: isCombined ? '0 0 10px 10px' : '10px',
                      border: isCombined ? `1px solid var(--accent)` : '1px solid var(--line)',
                      borderTop: isCombined ? 'none' : '1px solid var(--line)',
                      overflow: 'hidden',
                    }}
                  >
                    {groupOrders.map((order, idx) => {
                      const isExpanded = expandedId === order.id;
                      const items = itemsCache[order.id];
                      return (
                        <div
                          key={order.id}
                          style={{
                            background: 'var(--bg)',
                            borderTop: idx > 0 ? '1px solid var(--line)' : 'none',
                          }}
                        >
                          <button
                            className="w-full text-left cursor-pointer"
                            style={{ background: 'none', border: 'none', padding: '14px 18px' }}
                            onClick={() => handleExpand(order.id)}
                          >
                            <div className="flex items-center gap-4">
                              <div style={{ minWidth: 80 }}>
                                <p className="text-sm font-bold" style={{ color: 'var(--ink)', fontFamily: 'var(--font-display)' }}>
                                  #{order.id}
                                </p>
                                <p className="text-xs mt-0.5" style={{ color: 'var(--muted)' }}>
                                  {order.createdAt.slice(0, 16).replace('T', ' ')}
                                </p>
                              </div>

                              <p className="text-sm flex-1 truncate" style={{ color: 'var(--ink-soft)' }}>
                                {order.email}
                              </p>

                              <p className="text-sm flex-1 truncate" style={{ color: 'var(--muted)' }}>
                                {(() => { const { zipcode, street } = formatAddress(order.address); return <>{zipcode && <span className="font-semibold">({zipcode}) </span>}{street}</>; })()}
                              </p>

                              <p className="text-base font-bold flex-shrink-0" style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}>
                                {order.totalPrice.toLocaleString()}원
                              </p>

                              <span
                                className="text-sm font-semibold px-2.5 py-1 rounded-full flex-shrink-0"
                                style={STATUS_STYLE[order.status] ?? { background: 'var(--line)', color: 'var(--ink-soft)' }}
                              >
                                {order.status}
                              </span>

                              <svg
                                width="14" height="14" viewBox="0 0 14 14" fill="none"
                                style={{ flexShrink: 0, transform: isExpanded ? 'rotate(180deg)' : 'rotate(0)', transition: 'transform 0.2s' }}
                              >
                                <path d="M2 5l5 5 5-5" stroke="var(--muted)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                              </svg>
                            </div>
                          </button>

                          {isExpanded && (
                            <div
                              style={{
                                borderTop: '1px solid var(--line)',
                                padding: '14px 18px',
                                display: 'flex',
                                flexDirection: 'column',
                                gap: 12,
                              }}
                            >
                              {items === undefined ? (
                                <p className="text-sm" style={{ color: 'var(--muted)' }}>불러오는 중...</p>
                              ) : items.length === 0 ? (
                                <p className="text-sm" style={{ color: 'var(--muted)' }}>주문 아이템 없음</p>
                              ) : (
                                <div className="flex flex-col gap-2">
                                  {items.map((item, i) => (
                                    <div key={i} className="flex justify-between items-center">
                                      <span className="text-base" style={{ color: 'var(--ink)', fontFamily: 'var(--font-display)' }}>
                                        {item.name}
                                      </span>
                                      <span className="text-sm" style={{ color: 'var(--ink-soft)' }}>
                                        {item.amount}개 · {item.price.toLocaleString()}원
                                      </span>
                                    </div>
                                  ))}
                                </div>
                              )}
                              <div style={{ borderTop: '1px solid var(--line)', paddingTop: 12 }}>
                                <p className="text-xs font-semibold tracking-wider uppercase mb-1" style={{ color: 'var(--muted)' }}>배송지</p>
                                {(() => { const { zipcode, street } = formatAddress(order.address); return (
                                  <p className="text-base" style={{ color: 'var(--ink)' }}>
                                    {zipcode && <span className="font-semibold" style={{ color: 'var(--muted)' }}>({zipcode}) </span>}{street}
                                  </p>
                                ); })()}
                              </div>
                            </div>
                          )}
                        </div>
                      );
                    })}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
