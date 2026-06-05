import type { Product } from '@/types/order';

interface Props {
  products: Product[];
  onAdd: (product: Product) => void;
}

export default function ProductList({ products, onAdd }: Props) {
  return (
    <div className="bg-white rounded-xl p-6 shadow-sm">
      <h2 className="font-bold mb-4">상품 목록</h2>
      <div className="divide-y divide-zinc-100">
        {products.map((product) => (
          <div key={product.id} className="flex items-center gap-4 py-4">
            <div className="w-12 h-16 bg-zinc-100 rounded flex-shrink-0" />
            <div className="flex-1 min-w-0">
              <p className="text-sm font-semibold">{product.name}</p>
              <p className="text-sm text-zinc-400">{product.origin}</p>
            </div>
            <span className="text-sm whitespace-nowrap">
              {product.price.toLocaleString()}원
            </span>
            <button
              onClick={() => onAdd(product)}
              className="px-4 py-1.5 border border-zinc-300 rounded text-sm hover:bg-zinc-50 active:bg-zinc-100 transition-colors cursor-pointer"
            >
              추가
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}
