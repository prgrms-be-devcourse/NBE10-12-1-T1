'use client';

export interface OrderConfirmData {
  id: number;
  email: string;
  address: string;
  orderItems: { name: string; amount: number; price: number }[];
  totalPrice: number;
  createdAt: string;
}

interface Props {
  data: OrderConfirmData;
  onClose: () => void;
}

export default function OrderConfirmModal({ data, onClose }: Props) {
  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center"
      style={{ background: 'rgba(46,31,18,0.5)' }}
      onClick={onClose}
    >
      <div
        style={{
          background: 'var(--surface)',
          borderRadius: 'var(--radius)',
          boxShadow: '0 24px 72px rgba(46,31,18,.22)',
          width: 460,
          padding: '40px',
          position: 'relative',
        }}
        onClick={(e) => e.stopPropagation()}
      >
        <button
          onClick={onClose}
          className="absolute top-4 right-4 w-7 h-7 flex items-center justify-center rounded-full text-sm cursor-pointer"
          style={{ background: 'var(--surface-2)', color: 'var(--ink-soft)' }}
          onMouseEnter={(e) => { e.currentTarget.style.background = 'var(--line)'; }}
          onMouseLeave={(e) => { e.currentTarget.style.background = 'var(--surface-2)'; }}
        >
          ✕
        </button>

        {/* 헤더 */}
        <div className="flex items-center gap-3 mb-6">
          <div
            className="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0"
            style={{ background: 'var(--accent)' }}
          >
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
              <path d="M3 9l4.5 4.5L15 5" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          <div>
            <h2
              className="text-xl font-bold"
              style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
            >
              주문이 완료되었습니다
            </h2>
            <p className="text-xs mt-0.5" style={{ color: 'var(--muted)' }}>
              주문번호 #{data.id} · {data.createdAt.slice(0, 16).replace('T', ' ')}
            </p>
          </div>
        </div>

        {/* 주문 아이템 */}
        <div
          className="rounded-xl p-4 mb-4 flex flex-col gap-2"
          style={{ background: 'var(--bg)', border: '1px solid var(--line)' }}
        >
          {data.orderItems.map((item, i) => (
            <div key={i} className="flex justify-between items-center">
              <span className="text-sm" style={{ color: 'var(--ink)', fontFamily: 'var(--font-display)' }}>
                {item.name}
              </span>
              <span className="text-sm" style={{ color: 'var(--ink-soft)' }}>
                {item.amount}개 · {item.price.toLocaleString()}원
              </span>
            </div>
          ))}
          <div
            className="flex justify-between items-center pt-2 mt-1"
            style={{ borderTop: '1px solid var(--line)' }}
          >
            <span className="text-sm font-semibold" style={{ color: 'var(--ink)' }}>합계</span>
            <span
              className="text-base font-bold"
              style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
            >
              {data.totalPrice.toLocaleString()}원
            </span>
          </div>
        </div>

        {/* 배송 정보 */}
        <div
          className="rounded-xl p-4 mb-6"
          style={{ background: 'var(--bg)', border: '1px solid var(--line)' }}
        >
          <p className="text-[11px] font-semibold tracking-wider uppercase mb-1.5" style={{ color: 'var(--muted)' }}>
            배송지
          </p>
          <p className="text-sm" style={{ color: 'var(--ink)' }}>{data.address}</p>
          <p className="text-xs mt-1" style={{ color: 'var(--muted)' }}>{data.email}</p>
        </div>

        <button
          onClick={onClose}
          className="w-full py-3.5 rounded-xl text-sm font-semibold cursor-pointer transition-all"
          style={{ background: 'var(--ink)', color: 'var(--bg)' }}
          onMouseEnter={(e) => { e.currentTarget.style.filter = 'brightness(1.15)'; }}
          onMouseLeave={(e) => { e.currentTarget.style.filter = 'none'; }}
        >
          확인
        </button>
      </div>
    </div>
  );
}
