'use client';

interface Props {
  message: string;
  children?: React.ReactNode;
  onConfirm: () => void;
  onCancel: () => void;
}

export default function ConfirmModal({ message, children, onConfirm, onCancel }: Props) {
  return (
    <div
      className="fixed inset-0 z-[60] flex items-center justify-center"
      style={{ background: 'rgba(46,31,18,0.5)' }}
    >
      <div
        style={{
          background: 'var(--surface)',
          borderRadius: 'var(--radius)',
          boxShadow: '0 24px 72px rgba(46,31,18,.22)',
          width: 380,
          padding: '32px',
        }}
      >
        <p
          className="text-base font-semibold mb-4 text-center"
          style={{ color: 'var(--ink)', fontFamily: 'var(--font-body)' }}
        >
          {message}
        </p>
        {children && <div className="mb-6">{children}</div>}
        <div className="flex gap-3">
          <button
            onClick={onCancel}
            className="flex-1 py-3 rounded-xl text-sm font-semibold cursor-pointer transition-all"
            style={{ background: 'var(--surface-2)', color: 'var(--ink-soft)', border: '1px solid var(--line)' }}
            onMouseEnter={(e) => { e.currentTarget.style.background = 'var(--line)'; }}
            onMouseLeave={(e) => { e.currentTarget.style.background = 'var(--surface-2)'; }}
          >
            취소
          </button>
          <button
            onClick={onConfirm}
            className="flex-1 py-3 rounded-xl text-sm font-semibold cursor-pointer transition-all"
            style={{ background: 'var(--ink)', color: 'var(--bg)' }}
            onMouseEnter={(e) => { e.currentTarget.style.filter = 'brightness(1.15)'; }}
            onMouseLeave={(e) => { e.currentTarget.style.filter = 'none'; }}
          >
            확인
          </button>
        </div>
      </div>
    </div>
  );
}
