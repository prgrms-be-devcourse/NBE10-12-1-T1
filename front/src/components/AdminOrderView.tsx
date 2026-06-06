'use client';

import { useState } from 'react';
import type { Order, OrderStatus } from '@/types/order';

const STATUS_LABEL: Record<OrderStatus, string> = {
  pending:   '배송처리전',
  delivered: '배송완료',
};

const STATUS_STYLE: Record<OrderStatus, React.CSSProperties> = {
  pending:   { background: '#fef9c3', color: '#854d0e' },
  delivered: { background: 'var(--surface-2)', color: 'var(--ink-soft)' },
};

interface Props {
  orders: Order[];
}

export default function AdminOrderView({ orders }: Props) {
  const [expandedId, setExpandedId] = useState<string | null>(null);

  const pendingCount = orders.filter((o) => o.status === 'pending').length;

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
        {pendingCount > 0 && (
          <span className="text-sm ml-2" style={{ color: '#854d0e' }}>
            · 배송처리전 {pendingCount}건
          </span>
        )}
      </div>

      {/* 주문 목록 */}
      <div className="flex-1 overflow-y-auto px-6 py-4">
        {orders.length === 0 ? (
          <div className="h-full flex items-center justify-center">
            <p className="text-sm" style={{ color: 'var(--muted)' }}>아직 접수된 주문이 없습니다.</p>
          </div>
        ) : (
          <div className="flex flex-col gap-3">
            {orders.map((order) => {
              const isExpanded = expandedId === order.id;
              const itemSummary = order.items
                .map((i) => `${i.product.origin.split(' ')[0]} ×${i.quantity}`)
                .join(', ');

              return (
                <div
                  key={order.id}
                  style={{
                    background: 'var(--bg)',
                    border: `1px solid ${order.status === 'pending' ? '#fde68a' : 'var(--line)'}`,
                    borderRadius: 'var(--radius)',
                    overflow: 'hidden',
                  }}
                >
                  {/* 요약 행 */}
                  <button
                    className="w-full text-left cursor-pointer"
                    style={{ background: 'none', border: 'none', padding: '14px 18px' }}
                    onClick={() => setExpandedId(isExpanded ? null : order.id)}
                  >
                    <div className="flex items-center gap-4">
                      <div style={{ minWidth: 110 }}>
                        <p className="text-xs font-bold tracking-wider" style={{ color: 'var(--ink)', fontFamily: 'var(--font-display)' }}>
                          {order.id}
                        </p>
                        <p className="text-[11px] mt-0.5" style={{ color: 'var(--muted)' }}>
                          {order.createdAt.toLocaleDateString('ko-KR', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })}
                        </p>
                      </div>

                      <p className="text-xs flex-1 truncate" style={{ color: 'var(--ink-soft)' }}>
                        {order.email}
                      </p>

                      <p className="text-xs flex-1 truncate" style={{ color: 'var(--muted)' }}>
                        {itemSummary}
                      </p>

                      <p className="text-sm font-bold flex-shrink-0" style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}>
                        {order.total.toLocaleString()}원
                      </p>

                      <span
                        className="text-xs font-semibold px-2.5 py-1 rounded-full flex-shrink-0"
                        style={{ ...STATUS_STYLE[order.status] }}
                      >
                        {STATUS_LABEL[order.status]}
                      </span>

                      <svg
                        width="14" height="14" viewBox="0 0 14 14" fill="none"
                        style={{ flexShrink: 0, transform: isExpanded ? 'rotate(180deg)' : 'rotate(0)', transition: 'transform 0.2s' }}
                      >
                        <path d="M2 5l5 5 5-5" stroke="var(--muted)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                      </svg>
                    </div>
                  </button>

                  {/* 상세 내용 */}
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
                      <div className="flex flex-col gap-2">
                        {order.items.map((item) => (
                          <div key={item.product.id} className="flex justify-between items-center">
                            <span className="text-sm" style={{ color: 'var(--ink)', fontFamily: 'var(--font-display)' }}>
                              {item.product.origin}
                            </span>
                            <span className="text-sm" style={{ color: 'var(--ink-soft)' }}>
                              {item.quantity}개 · {(item.product.price * item.quantity).toLocaleString()}원
                            </span>
                          </div>
                        ))}
                      </div>

                      <div style={{ borderTop: '1px solid var(--line)', paddingTop: 12 }}>
                        <p className="text-[11px] font-semibold tracking-wider uppercase mb-1" style={{ color: 'var(--muted)' }}>배송지</p>
                        <p className="text-sm" style={{ color: 'var(--ink)' }}>{order.address}</p>
                        <p className="text-xs mt-0.5" style={{ color: 'var(--muted)' }}>{order.zipcode}</p>
                      </div>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
