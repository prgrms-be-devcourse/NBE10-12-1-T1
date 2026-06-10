import { useState } from 'react';
import type { Product } from '@/types/order';
import ConfirmModal from './ConfirmModal';

interface Props {
  products: Product[];
  onAdd: (product: Product, e: React.MouseEvent<HTMLButtonElement>) => void;
  isAdmin?: boolean;
  onAddProduct?: () => void;
  onEditProduct?: (product: Product) => void;
  onDeleteProduct?: (productId: number) => void;
}

const MAX_CARDS = 40;

const DEFAULT_IMAGES = [
  '/스크린샷 2026-06-10 오후 3.36.08.png',
  '/스크린샷 2026-06-10 오후 3.36.27.png',
  '/스크린샷 2026-06-10 오후 3.40.17.png',
];

function ProductImage({ imgUrl, id, name }: { imgUrl: string; id: number; name: string }) {
  const src = imgUrl || DEFAULT_IMAGES[id % DEFAULT_IMAGES.length];
  return (
    // eslint-disable-next-line @next/next/no-img-element
    <img
      src={src}
      alt={name}
      className="w-full object-cover"
      style={{ aspectRatio: '4 / 4.12' }}
    />
  );
}

export default function ProductList({ products, onAdd, isAdmin, onAddProduct, onEditProduct, onDeleteProduct }: Props) {
  const [pendingDelete, setPendingDelete] = useState<{ id: number; name: string } | null>(null);
  const ghostCount = Math.max(0, MAX_CARDS - products.length);

  return (
    <div
      className="rounded-2xl p-6 flex-1 overflow-y-auto"
      style={{
        background: isAdmin ? '#3c3228' : 'var(--surface)',
        border: isAdmin ? '1px solid #504235' : '1px solid var(--line)',
        boxShadow: '0 1px 2px rgba(46,31,18,.04), 0 8px 28px rgba(46,31,18,.06)',
      }}
    >
      {isAdmin && (
        <div className="flex justify-end mb-4">
          <button
            onClick={onAddProduct}
            className="flex items-center gap-1.5 px-4 py-2 rounded-xl text-sm font-semibold cursor-pointer transition-all"
            style={{ background: 'var(--accent)', color: 'white', border: 'none' }}
            onMouseEnter={(e) => { e.currentTarget.style.filter = 'brightness(1.1)'; }}
            onMouseLeave={(e) => { e.currentTarget.style.filter = 'none'; }}
          >
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <line x1="7" y1="1" x2="7" y2="13" stroke="white" strokeWidth="2" strokeLinecap="round"/>
              <line x1="1" y1="7" x2="13" y2="7" stroke="white" strokeWidth="2" strokeLinecap="round"/>
            </svg>
            상품 추가
          </button>
        </div>
      )}

      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
          gap: 20,
        }}
      >
        {products.map((product) => (
          <div
            key={product.id}
            className="rounded-xl overflow-hidden relative group"
            style={{
              background: 'var(--bg)',
              border: '1px solid var(--line)',
              transition: 'border-color 0.15s, box-shadow 0.15s, transform 0.15s',
            }}
            onMouseEnter={(e) => {
              (e.currentTarget as HTMLDivElement).style.borderColor = 'var(--accent)';
              (e.currentTarget as HTMLDivElement).style.boxShadow = '0 4px 16px rgba(154,91,52,.15)';
              (e.currentTarget as HTMLDivElement).style.transform = 'translateY(-2px)';
            }}
            onMouseLeave={(e) => {
              (e.currentTarget as HTMLDivElement).style.borderColor = 'var(--line)';
              (e.currentTarget as HTMLDivElement).style.boxShadow = 'none';
              (e.currentTarget as HTMLDivElement).style.transform = 'translateY(0)';
            }}
          >
            <button
              onClick={(e) => onAdd(product, e)}
              className="w-full text-left cursor-pointer"
              style={{ background: 'none', border: 'none', padding: 0, display: 'block' }}
            >
              <ProductImage imgUrl={product.imgUrl} id={product.id} name={product.name} />
              <div className="p-4" style={{ minHeight: 110 }}>
                <p
                  className="text-base font-bold mb-1 truncate"
                  style={{ fontFamily: 'var(--font-body)', color: 'var(--ink)', fontWeight: 800 }}
                >
                  {product.name}
                </p>
                {isAdmin && product.stock !== undefined && (
                  <p
                    className="text-sm font-semibold mb-1 truncate"
                    style={{ color: 'var(--ink-soft)' }}
                  >
                    재고 {product.stock}개
                  </p>
                )}
                <p
                  className="text-base font-bold"
                  style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
                >
                  {product.price.toLocaleString()}원
                </p>
              </div>
            </button>

            {isAdmin && (
              <div
                className="absolute inset-0 flex items-center justify-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity"
                style={{ background: 'rgba(46,31,18,0.62)', borderRadius: 'inherit' }}
              >
                <button
                  onClick={(e) => { e.stopPropagation(); onEditProduct?.(product); }}
                  className="px-4 py-2 rounded-lg text-xs font-bold cursor-pointer transition-all"
                  style={{ background: 'var(--surface)', color: 'var(--ink)', border: 'none' }}
                  onMouseEnter={(e) => { e.currentTarget.style.background = 'white'; }}
                  onMouseLeave={(e) => { e.currentTarget.style.background = 'var(--surface)'; }}
                >
                  수정
                </button>
                <button
                  onClick={(e) => { e.stopPropagation(); setPendingDelete({ id: product.id, name: product.name }); }}
                  className="px-4 py-2 rounded-lg text-xs font-bold cursor-pointer transition-all"
                  style={{ background: '#e53e3e', color: 'white', border: 'none' }}
                  onMouseEnter={(e) => { e.currentTarget.style.background = '#c53030'; }}
                  onMouseLeave={(e) => { e.currentTarget.style.background = '#e53e3e'; }}
                >
                  삭제
                </button>
              </div>
            )}
          </div>
        ))}

        {Array.from({ length: ghostCount }).map((_, i) => (
          <div
            key={`ghost-${i}`}
            aria-hidden="true"
            className="rounded-xl overflow-hidden"
            style={{
              background: 'var(--bg)',
              border: '1px solid var(--line)',
              opacity: 0.35,
              pointerEvents: 'none',
            }}
          >
            <div
              className="w-full flex items-center justify-center"
              style={{ aspectRatio: '4 / 3', background: 'var(--surface-2)' }}
            >
              <svg width="28" height="28" viewBox="0 0 36 36" fill="none">
                <line x1="6" y1="6" x2="30" y2="30" stroke="var(--line)" strokeWidth="2" strokeLinecap="round"/>
                <line x1="30" y1="6" x2="6" y2="30" stroke="var(--line)" strokeWidth="2" strokeLinecap="round"/>
              </svg>
            </div>
            <div className="p-2.5 flex flex-col gap-1.5">
              <div className="h-2 rounded-full" style={{ background: 'var(--line)', width: '55%' }} />
              <div className="h-2 rounded-full" style={{ background: 'var(--line)', width: '75%' }} />
              <div className="h-2.5 rounded-full mt-0.5" style={{ background: 'var(--line)', width: '40%' }} />
            </div>
          </div>
        ))}
      </div>

      {pendingDelete !== null && (
        <ConfirmModal
          message={`'${pendingDelete.name}' 상품을 삭제하시겠습니까?`}
          onConfirm={() => { onDeleteProduct?.(pendingDelete.id); setPendingDelete(null); }}
          onCancel={() => setPendingDelete(null)}
        />
      )}
    </div>
  );
}
