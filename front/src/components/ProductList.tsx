import type { Product } from '@/types/order';

interface Props {
  products: Product[];
  onAdd: (product: Product) => void;
}

function BeanPlaceholder({ origin }: { origin: string }) {
  const label = origin.split(' ')[0].slice(0, 6).toUpperCase();
  return (
    <div
      className="w-16 h-16 rounded-xl flex-shrink-0 relative overflow-hidden flex flex-col items-center justify-center gap-1"
      style={{
        background: `repeating-linear-gradient(135deg,
          #e8d5c4 0px, #e8d5c4 11px,
          #dfc9b5 11px, #dfc9b5 22px)`,
      }}
    >
      <span
        className="text-[9px] font-bold tracking-wider opacity-50 px-1 text-center leading-tight"
        style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
      >
        {label}
      </span>
      <span
        className="text-[7px] tracking-wide px-1.5 py-0.5 rounded"
        style={{ background: 'rgba(255,255,255,0.5)', color: 'var(--ink-soft)' }}
      >
        원두 사진
      </span>
    </div>
  );
}

export default function ProductList({ products, onAdd }: Props) {
  return (
    <div
      className="rounded-2xl p-7"
      style={{
        background: 'var(--surface)',
        border: '1px solid var(--line)',
        boxShadow: '0 1px 2px rgba(46,31,18,.04), 0 8px 28px rgba(46,31,18,.06)',
      }}
    >
      <h2
        className="text-xs font-semibold tracking-[0.25em] uppercase mb-6"
        style={{ color: 'var(--accent)' }}
      >
        상품 목록
      </h2>

      <div style={{ borderTop: '1px solid var(--line)' }}>
        {products.map((product) => (
          <div
            key={product.id}
            className="flex items-center gap-4 py-5"
            style={{ borderBottom: '1px solid var(--line)' }}
          >
            <BeanPlaceholder origin={product.origin} />

            <div className="flex-1 min-w-0">
              <p
                className="text-[11px] font-semibold tracking-[0.2em] uppercase mb-0.5"
                style={{ color: 'var(--muted)' }}
              >
                {product.name}
              </p>
              <p
                className="text-sm font-semibold truncate"
                style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
              >
                {product.origin}
              </p>
            </div>

            <span
              className="text-sm font-bold whitespace-nowrap"
              style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
            >
              {product.price.toLocaleString()}원
            </span>

            <button
              onClick={() => onAdd(product)}
              className="px-4 py-1.5 rounded-full text-xs font-semibold transition-all cursor-pointer whitespace-nowrap"
              style={{
                border: '1px solid var(--line)',
                background: 'var(--surface)',
                color: 'var(--ink-soft)',
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
              추가
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}
