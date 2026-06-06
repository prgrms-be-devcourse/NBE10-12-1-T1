'use client';

import { useState } from 'react';
import type { Product } from '@/types/order';

const inputStyle: React.CSSProperties = {
  background: 'var(--bg)',
  border: '1px solid var(--line)',
  color: 'var(--ink)',
  borderRadius: '10px',
  fontSize: '13.5px',
  padding: '10px 14px',
  outline: 'none',
  width: '100%',
  fontFamily: 'var(--font-body)',
};

interface Props {
  product?: Product;
  onSave: (data: Omit<Product, 'id'>) => void;
  onClose: () => void;
}

export default function ProductFormModal({ product, onSave, onClose }: Props) {
  const [name, setName] = useState(product?.name ?? '');
  const [price, setPrice] = useState(product?.price.toString() ?? '');
  const [stock, setStock] = useState(product?.stock?.toString() ?? '');

  const isEdit = !!product;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const parsedPrice = parseInt(price.replace(/,/g, ''), 10);
    const parsedStock = parseInt(stock, 10);
    if (!name.trim() || isNaN(parsedPrice) || parsedPrice <= 0 || isNaN(parsedStock) || parsedStock < 0) return;
    onSave({ name: name.trim(), price: parsedPrice, stock: parsedStock, imgUrl: product?.imgUrl ?? '' });
  };

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
          width: 420,
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

        <h2
          className="text-2xl font-bold mb-8"
          style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
        >
          {isEdit ? '상품 수정' : '상품 추가'}
        </h2>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <div>
            <label className="block text-xs font-semibold tracking-wide uppercase mb-2" style={{ color: 'var(--muted)' }}>
              상품명
            </label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              style={inputStyle}
              placeholder="예: 커피콩"
              onFocus={(e) => { e.currentTarget.style.borderColor = 'var(--accent)'; }}
              onBlur={(e) => { e.currentTarget.style.borderColor = 'var(--line)'; }}
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold tracking-wide uppercase mb-2" style={{ color: 'var(--muted)' }}>
              재고 (개)
            </label>
            <input
              type="number"
              value={stock}
              onChange={(e) => setStock(e.target.value)}
              style={inputStyle}
              placeholder="예: 100"
              min={0}
              onFocus={(e) => { e.currentTarget.style.borderColor = 'var(--accent)'; }}
              onBlur={(e) => { e.currentTarget.style.borderColor = 'var(--line)'; }}
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold tracking-wide uppercase mb-2" style={{ color: 'var(--muted)' }}>
              가격 (원)
            </label>
            <input
              type="number"
              value={price}
              onChange={(e) => setPrice(e.target.value)}
              style={inputStyle}
              placeholder="예: 7500"
              min={1}
              onFocus={(e) => { e.currentTarget.style.borderColor = 'var(--accent)'; }}
              onBlur={(e) => { e.currentTarget.style.borderColor = 'var(--line)'; }}
              required
            />
          </div>

          <div className="flex gap-3 mt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-3.5 rounded-xl text-sm font-semibold cursor-pointer transition-all"
              style={{ background: 'var(--surface-2)', color: 'var(--ink-soft)', border: '1px solid var(--line)' }}
              onMouseEnter={(e) => { e.currentTarget.style.background = 'var(--line)'; }}
              onMouseLeave={(e) => { e.currentTarget.style.background = 'var(--surface-2)'; }}
            >
              취소
            </button>
            <button
              type="submit"
              className="flex-1 py-3.5 rounded-xl text-sm font-semibold cursor-pointer transition-all"
              style={{ background: 'var(--ink)', color: 'var(--bg)' }}
              onMouseEnter={(e) => { e.currentTarget.style.filter = 'brightness(1.15)'; }}
              onMouseLeave={(e) => { e.currentTarget.style.filter = 'none'; }}
            >
              {isEdit ? '저장' : '추가'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
