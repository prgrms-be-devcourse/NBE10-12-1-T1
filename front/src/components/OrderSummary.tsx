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

interface OrderForm {
  email: string;
  address: string;
  zipcode: string;
}

interface Props {
  cart: CartItem[];
  onUpdateQuantity: (productId: number, delta: number) => void;
  total: number;
  onCheckout: (form: OrderForm) => void;
}

export default function OrderSummary({ cart, onUpdateQuantity, total, onCheckout }: Props) {
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
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
          onClick={() => setShowPostcode(false)}
        >
          <div
            className="bg-white rounded-xl overflow-hidden shadow-2xl w-[500px] h-[500px] relative"
            onClick={(e) => e.stopPropagation()}
          >
            <button
              onClick={() => setShowPostcode(false)}
              className="absolute top-3 right-3 z-10 w-7 h-7 flex items-center justify-center rounded-full bg-zinc-100 hover:bg-zinc-200 text-zinc-600 text-sm cursor-pointer"
            >
              ✕
            </button>
            <div ref={postcodeContainerRef} className="w-full h-full" />
          </div>
        </div>
      )}

      <div className="bg-white rounded-xl p-6 shadow-sm">
        <h2 className="font-bold mb-4">Summary</h2>

        {/* 장바구니 목록 */}
        <div className="flex flex-col gap-2.5 mb-6 min-h-[20px]">
          {cart.map((item) => (
            <div key={item.product.id} className="flex items-center justify-between">
              <span className="text-sm truncate mr-2">{item.product.origin}</span>
              <div className="flex items-center gap-1 flex-shrink-0">
                <button
                  onClick={() => onUpdateQuantity(item.product.id, -1)}
                  className="w-5 h-5 flex items-center justify-center rounded bg-zinc-100 hover:bg-zinc-200 text-sm leading-none cursor-pointer"
                >
                  −
                </button>
                <span className="inline-flex items-center justify-center px-2 h-5 rounded-full bg-zinc-900 text-white text-xs font-medium min-w-[38px]">
                  {item.quantity}개
                </span>
                <button
                  onClick={() => onUpdateQuantity(item.product.id, 1)}
                  className="w-5 h-5 flex items-center justify-center rounded bg-zinc-100 hover:bg-zinc-200 text-sm leading-none cursor-pointer"
                >
                  +
                </button>
              </div>
            </div>
          ))}
        </div>

        {/* 입력 폼 */}
        <div className="flex flex-col gap-4">
          {/* 이메일 */}
          <div>
            <label className="block text-sm mb-1.5">이메일</label>
            <div className="flex items-center gap-1">
              <input
                type="text"
                value={emailId}
                onChange={(e) => setEmailId(e.target.value)}
                className="flex-1 min-w-0 border border-zinc-200 rounded px-3 py-2 text-sm outline-none focus:border-zinc-400"
                placeholder="아이디"
              />
              <span className="text-sm text-zinc-500 flex-shrink-0">@</span>
              {isCustomDomain ? (
                <>
                  <input
                    type="text"
                    value={customDomain}
                    onChange={(e) => setCustomDomain(e.target.value)}
                    className="flex-1 min-w-0 border border-zinc-200 rounded px-3 py-2 text-sm outline-none focus:border-zinc-400"
                    placeholder="도메인 입력"
                    autoFocus
                  />
                  <button
                    onClick={() => { setIsCustomDomain(false); setCustomDomain(''); }}
                    className="text-xs text-zinc-400 hover:text-zinc-600 flex-shrink-0 cursor-pointer"
                  >
                    취소
                  </button>
                </>
              ) : (
                <select
                  value={emailDomain}
                  onChange={handleDomainChange}
                  className="border border-zinc-200 rounded px-2 py-2 text-sm outline-none focus:border-zinc-400 bg-white cursor-pointer"
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
            <label className="block text-sm mb-1.5">주소</label>
            <div className="flex gap-1.5 mb-1.5">
              <input
                type="text"
                value={address}
                readOnly
                className="flex-1 border border-zinc-200 rounded px-3 py-2 text-sm bg-zinc-50 outline-none cursor-default"
                placeholder="주소를 검색해주세요"
              />
              <button
                onClick={() => setShowPostcode(true)}
                className="px-3 py-2 border border-zinc-300 rounded text-sm whitespace-nowrap hover:bg-zinc-50 cursor-pointer"
              >
                찾기
              </button>
            </div>
            <input
              type="text"
              value={addressDetail}
              onChange={(e) => setAddressDetail(e.target.value)}
              className="w-full border border-zinc-200 rounded px-3 py-2 text-sm outline-none focus:border-zinc-400"
              placeholder="상세주소"
            />
          </div>

          {/* 우편번호 */}
          <div>
            <label className="block text-sm mb-1.5">우편번호</label>
            <input
              type="text"
              value={zipcode}
              readOnly
              className="w-full border border-zinc-200 rounded px-3 py-2 text-sm bg-zinc-50 outline-none cursor-default"
              placeholder="주소 검색 시 자동 입력"
            />
          </div>
        </div>

        {/* 배송 안내 */}
        <p className="text-xs text-zinc-400 mt-5 leading-relaxed">
          당일 오후 2시 이후의 주문은 다음날 배송을 시작합니다.
        </p>

        {/* 총금액 */}
        <div className="flex justify-between items-center mt-4">
          <span className="text-sm font-bold">총금액</span>
          <span className="font-bold text-base">{total.toLocaleString()}원</span>
        </div>

        {/* 결제하기 */}
        <button
          onClick={handleCheckout}
          className="w-full mt-4 py-3 bg-zinc-900 text-white rounded-lg text-sm font-medium hover:bg-zinc-700 transition-colors cursor-pointer"
        >
          결제하기
        </button>
      </div>
    </>
  );
}
