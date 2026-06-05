import type { Product } from '@/types/order';

interface Props {
  products: Product[];
  onAdd: (product: Product, e: React.MouseEvent<HTMLButtonElement>) => void;
}

const MAX_CARDS = 30;

function BeanPlaceholder({ origin }: { origin: string }) {
  const label = origin.split(' ')[0].slice(0, 6).toUpperCase();
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

export default function ProductList({ products, onAdd }: Props) {
  const visibleProducts = products.slice(0, MAX_CARDS);
  const ghostCount = MAX_CARDS - visibleProducts.length;

  return (
    <div
      className="rounded-2xl p-6 flex-1 overflow-hidden"
      style={{
        background: 'var(--surface)',
        border: '1px solid var(--line)',
        boxShadow: '0 1px 2px rgba(46,31,18,.04), 0 8px 28px rgba(46,31,18,.06)',
      }}
    >
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(160px, 1fr))',
          gap: 12,
        }}
      >
        {visibleProducts.map((product) => (
          <button
            key={product.id}
            onClick={(e) => onAdd(product, e)}
            className="text-left rounded-xl overflow-hidden cursor-pointer transition-all"
            style={{
              background: 'var(--bg)',
              border: '1px solid var(--line)',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.borderColor = 'var(--accent)';
              e.currentTarget.style.boxShadow = '0 4px 16px rgba(154,91,52,.15)';
              e.currentTarget.style.transform = 'translateY(-2px)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.borderColor = 'var(--line)';
              e.currentTarget.style.boxShadow = 'none';
              e.currentTarget.style.transform = 'translateY(0)';
            }}
          >
            <BeanPlaceholder origin={product.origin} />
            <div className="p-2.5">
              <p
                className="text-[10px] font-semibold tracking-[0.18em] uppercase mb-0.5 truncate"
                style={{ color: 'var(--muted)' }}
              >
                {product.name}
              </p>
              <p
                className="text-xs font-semibold leading-snug mb-1 truncate"
                style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
              >
                {product.origin}
              </p>
              <p
                className="text-sm font-bold"
                style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
              >
                {product.price.toLocaleString()}원
              </p>
            </div>
          </button>
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
