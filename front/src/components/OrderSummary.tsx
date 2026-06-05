'use client';

import { useEffect, useRef, useState } from 'react';
import type { CartItem } from '@/types/order';

declare global {
  interface Window {
    daum: {
      Postcode: new (config: {
        oncomplete: (data: { roadAddress: string; jibunAddress: string; zonecode: string }) => void;
        onclose?: () => void;
        width?: string;
        height?: string;
      }) => { open: () => void; embed: (el: HTMLElement) => void };
    };
  }
}

const EMAIL_DOMAINS = ['gmail.com', 'naver.com', 'daum.net', 'kakao.com'];

const inputStyle: React.CSSProperties = {
  background: 'var(--surface)',
  border: '1px solid var(--line)',
  color: 'var(--ink)',
  borderRadius: '10px',
  fontSize: '13.5px',
  padding: '10px 14px',
  outline: 'none',
  width: '100%',
  fontFamily: 'var(--font-body)',
};

const readonlyInputStyle: React.CSSProperties = {
  ...inputStyle,
  background: 'var(--surface-2)',
  cursor: 'default',
  color: 'var(--ink-soft)',
};

interface OrderForm {
  email: string;
  address: string;
  zipcode: string;
}

interface Props {
  cart: CartItem[];
  isOpen: boolean;
  onToggle: () => void;
  onUpdateQuantity: (productId: number, delta: number) => void;
  total: number;
  onCheckout: (form: OrderForm) => void;
}

export default function OrderSummary({ cart, isOpen, onToggle, onUpdateQuantity, total, onCheckout }: Props) {
  const [emailId, setEmailId] = useState('');
  const [emailDomain, setEmailDomain] = useState(EMAIL_DOMAINS[0]);
  const [isCustomDomain, setIsCustomDomain] = useState(false);
  const [customDomain, setCustomDomain] = useState('');
  const [address, setAddress] = useState('');
  const [addressDetail, setAddressDetail] = useState('');
  const [zipcode, setZipcode] = useState('');
  const [showPostcode, setShowPostcode] = useState(false);
  const postcodeContainerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const script = document.createElement('script');
    script.src = 'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
    script.async = true;
    document.head.appendChild(script);
    return () => { document.head.removeChild(script); };
  }, []);

  useEffect(() => {
    if (!showPostcode || !postcodeContainerRef.current) return;
    new window.daum.Postcode({
      oncomplete: (data) => {
        setAddress(data.roadAddress || data.jibunAddress);
        setZipcode(data.zonecode);
        setShowPostcode(false);
      },
      onclose: () => setShowPostcode(false),
      width: '100%',
      height: '100%',
    }).embed(postcodeContainerRef.current);
  }, [showPostcode]);

  const handleDomainChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    if (e.target.value === '직접입력') {
      setIsCustomDomain(true);
      setCustomDomain('');
    } else {
      setEmailDomain(e.target.value);
    }
  };

  const handleCheckout = () => {
    const domain = isCustomDomain ? customDomain : emailDomain;
    onCheckout({
      email: `${emailId}@${domain}`,
      address: `${address} ${addressDetail}`.trim(),
      zipcode,
    });
  };

  return (
    <>
      {/* 주소 검색 모달 */}
      {showPostcode && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center"
          style={{ background: 'rgba(46,31,18,0.5)' }}
          onClick={() => setShowPostcode(false)}
        >
          <div
            className="overflow-hidden relative"
            style={{
              background: 'var(--surface)',
              borderRadius: 'var(--radius)',
              boxShadow: '0 20px 60px rgba(46,31,18,.25)',
              width: 500,
              height: 500,
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <button
              onClick={() => setShowPostcode(false)}
              className="absolute top-3 right-3 z-10 w-7 h-7 flex items-center justify-center rounded-full text-sm cursor-pointer transition-colors"
              style={{ background: 'var(--surface-2)', color: 'var(--ink-soft)' }}
            >
              ✕
            </button>
            <div ref={postcodeContainerRef} className="w-full h-full" />
          </div>
        </div>
      )}

      {/* Dock 패널 */}
      <div
        style={{
          position: 'fixed',
          bottom: 0,
          right: 40,
          width: 360,
          maxHeight: 'calc(100vh - 80px)',
          transform: isOpen ? 'translateY(0)' : 'translateY(calc(100% - 48px))',
          transition: 'transform 0.45s cubic-bezier(0.34, 1.2, 0.64, 1)',
          zIndex: 40,
          display: 'flex',
          flexDirection: 'column',
          filter: 'drop-shadow(0 -6px 24px rgba(46,31,18,.14))',
        }}
      >
        {/* 헤더 탭 (항상 노출) */}
        <div
          style={{
            background: 'var(--ink)',
            color: 'var(--bg)',
            borderRadius: '16px 16px 0 0',
            padding: '13px 20px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            flexShrink: 0,
            cursor: 'pointer',
            userSelect: 'none',
          }}
          onClick={onToggle}
        >
          <span
            style={{
              fontFamily: 'var(--font-display)',
              fontSize: 13,
              fontWeight: 700,
              letterSpacing: '0.18em',
            }}
          >
            SUMMARY
          </span>
          {cart.length > 0 && (
            <span
              style={{
                background: 'var(--accent)',
                color: 'white',
                borderRadius: 20,
                padding: '2px 9px',
                fontSize: 11,
                fontWeight: 700,
              }}
            >
              {cart.length}
            </span>
          )}
        </div>

        {/* 본문 */}
        <div
          style={{
            background: 'var(--surface)',
            borderLeft: '1px solid var(--line)',
            borderRight: '1px solid var(--line)',
            borderBottom: '1px solid var(--line)',
            overflowY: 'auto',
            flex: 1,
            padding: '20px 24px 24px',
          }}
        >
          {/* 장바구니 목록 */}
          <div className="flex flex-col gap-3 mb-5" style={{ maxHeight: 180, overflowY: 'auto' }}>
            {cart.map((item) => (
              <div key={item.product.id} className="flex items-center justify-between gap-2">
                <span
                  className="text-sm truncate"
                  style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
                >
                  {item.product.origin}
                </span>
                <div className="flex items-center gap-1.5 flex-shrink-0">
                  <button
                    onClick={() => onUpdateQuantity(item.product.id, -1)}
                    className="w-6 h-6 flex items-center justify-center rounded-lg text-sm cursor-pointer"
                    style={{ background: 'var(--surface-2)', color: 'var(--ink-soft)' }}
                  >
                    −
                  </button>
                  <span
                    className="inline-flex items-center justify-center h-6 px-2.5 rounded-full text-xs font-semibold min-w-[42px]"
                    style={{ background: 'var(--ink)', color: 'var(--bg)' }}
                  >
                    {item.quantity}개
                  </span>
                  <button
                    onClick={() => onUpdateQuantity(item.product.id, 1)}
                    className="w-6 h-6 flex items-center justify-center rounded-lg text-sm cursor-pointer"
                    style={{ background: 'var(--surface-2)', color: 'var(--ink-soft)' }}
                  >
                    +
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* 구분선 */}
          <div className="mb-5" style={{ borderTop: '1px solid var(--line)' }} />

          {/* 입력 폼 */}
          <div className="flex flex-col gap-4">
            {/* 이메일 */}
            <div>
              <label className="block text-xs font-semibold tracking-wide uppercase mb-2" style={{ color: 'var(--muted)' }}>
                이메일
              </label>
              <div className="flex items-center gap-1.5">
                <input
                  type="text"
                  value={emailId}
                  onChange={(e) => setEmailId(e.target.value)}
                  style={{ ...inputStyle, flex: 1, minWidth: 0 }}
                  placeholder="아이디"
                  onFocus={(e) => { e.currentTarget.style.borderColor = 'var(--accent)'; }}
                  onBlur={(e) => { e.currentTarget.style.borderColor = 'var(--line)'; }}
                />
                <span className="text-sm flex-shrink-0" style={{ color: 'var(--muted)' }}>@</span>
                {isCustomDomain ? (
                  <>
                    <input
                      type="text"
                      value={customDomain}
                      onChange={(e) => setCustomDomain(e.target.value)}
                      style={{ ...inputStyle, flex: 1, minWidth: 0 }}
                      placeholder="도메인 입력"
                      autoFocus
                      onFocus={(e) => { e.currentTarget.style.borderColor = 'var(--accent)'; }}
                      onBlur={(e) => { e.currentTarget.style.borderColor = 'var(--line)'; }}
                    />
                    <button
                      onClick={() => { setIsCustomDomain(false); setCustomDomain(''); }}
                      className="text-xs flex-shrink-0 cursor-pointer"
                      style={{ color: 'var(--muted)' }}
                    >
                      취소
                    </button>
                  </>
                ) : (
                  <select
                    value={emailDomain}
                    onChange={handleDomainChange}
                    style={{ ...inputStyle, width: 'auto', cursor: 'pointer', padding: '10px 10px' }}
                  >
                    {EMAIL_DOMAINS.map((d) => (
                      <option key={d} value={d}>{d}</option>
                    ))}
                    <option value="직접입력">직접입력</option>
                  </select>
                )}
              </div>
            </div>

            {/* 주소 */}
            <div>
              <label className="block text-xs font-semibold tracking-wide uppercase mb-2" style={{ color: 'var(--muted)' }}>
                주소
              </label>
              <div className="flex gap-2 mb-2">
                <input
                  type="text"
                  value={address}
                  readOnly
                  style={{ ...readonlyInputStyle, flex: 1 }}
                  placeholder="주소를 검색해주세요"
                />
                <button
                  onClick={() => setShowPostcode(true)}
                  className="px-4 rounded-xl text-sm font-semibold whitespace-nowrap cursor-pointer transition-all"
                  style={{ border: '1px solid var(--line)', background: 'var(--surface)', color: 'var(--ink-soft)' }}
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
                  찾기
                </button>
              </div>
              <input
                type="text"
                value={addressDetail}
                onChange={(e) => setAddressDetail(e.target.value)}
                style={inputStyle}
                placeholder="상세주소"
                onFocus={(e) => { e.currentTarget.style.borderColor = 'var(--accent)'; }}
                onBlur={(e) => { e.currentTarget.style.borderColor = 'var(--line)'; }}
              />
            </div>

            {/* 우편번호 */}
            <div>
              <label className="block text-xs font-semibold tracking-wide uppercase mb-2" style={{ color: 'var(--muted)' }}>
                우편번호
              </label>
              <input
                type="text"
                value={zipcode}
                readOnly
                style={readonlyInputStyle}
                placeholder="주소 검색 시 자동 입력"
              />
            </div>
          </div>

          {/* 배송 안내 */}
          <p
            className="text-xs mt-5 leading-relaxed"
            style={{ color: 'var(--muted)', paddingTop: '16px', borderTop: '1px solid var(--line)' }}
          >
            당일 오후 2시 이후의 주문은 다음날 배송을 시작합니다.
          </p>

          {/* 총금액 */}
          <div className="flex justify-between items-baseline mt-4">
            <span className="text-sm font-semibold" style={{ color: 'var(--ink-soft)' }}>총금액</span>
            <span
              className="text-2xl font-bold"
              style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
            >
              {total.toLocaleString()}원
            </span>
          </div>

          {/* 결제하기 */}
          <button
            onClick={handleCheckout}
            className="w-full mt-4 py-3.5 rounded-xl text-sm font-semibold transition-all cursor-pointer"
            style={{ background: 'var(--ink)', color: 'var(--bg)' }}
            onMouseEnter={(e) => { e.currentTarget.style.filter = 'brightness(1.15)'; }}
            onMouseLeave={(e) => { e.currentTarget.style.filter = 'none'; }}
          >
            결제하기
          </button>
        </div>
      </div>
    </>
  );
}
