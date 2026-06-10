import type { Product } from '@/types/order';

interface Props {
  products: Product[];
  onAdd: (product: Product, e: React.MouseEvent<HTMLButtonElement>) => void;
  isAdmin?: boolean;
  onAddProduct?: () => void;
  onEditProduct?: (product: Product) => void;
  onDeleteProduct?: (productId: number) => void;
}

const MAX_CARDS = 40;

function BeanPlaceholder({ name }: { name: string }) {
  const label = name.slice(0, 6).toUpperCase();
  return (
    <div
      className="w-full relative overflow-hidden flex flex-col items-center justify-center gap-1"
      style={{
        aspectRatio: '4 / 3',
        background: `repeating-linear-gradient(135deg,
          #e8d5c4 0px, #e8d5c4 11px,
          #dfc9b5 11px, #dfc9b5 22px)`,
      }}
    >
      <span
        className="text-xs font-bold tracking-wider opacity-50 text-center leading-tight"
        style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
      >
        {label}
      </span>
      <span
        className="text-[9px] tracking-wide px-1.5 py-0.5 rounded"
        style={{ background: 'rgba(255,255,255,0.5)', color: 'var(--ink-soft)' }}
      >
        원두 사진
      </span>
    </div>
  );
}

export default function ProductList({ products, onAdd, isAdmin, onAddProduct, onEditProduct, onDeleteProduct }: Props) {
  const ghostCount = Math.max(0, MAX_CARDS - products.length);

  return (
    <div
      className="rounded-2xl p-6 flex-1 overflow-y-auto"
      style={{
        background: 'var(--surface)',
        border: '1px solid var(--line)',
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
              <BeanPlaceholder name={product.name} />
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
                  onClick={(e) => { e.stopPropagation(); onDeleteProduct?.(product.id); }}
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
    </div>
  );
}
