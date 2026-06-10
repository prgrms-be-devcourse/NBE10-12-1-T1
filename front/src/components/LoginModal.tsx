'use client';

import { useState } from 'react';

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
  onClose: () => void;
  onLogin: (isAdmin: boolean) => void;
}

export default function LoginModal({ onClose, onLogin }: Props) {
  const [id, setId] = useState('admin');
  const [password, setPassword] = useState('admin');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onLogin(true);
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
          width: 400,
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
          className="text-2xl font-bold mb-1"
          style={{ fontFamily: 'var(--font-display)', color: 'var(--ink)' }}
        >
          로그인
        </h2>
        <p className="text-sm mb-8" style={{ color: 'var(--muted)' }}>
          Beantage에 오신 것을 환영합니다
        </p>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <div>
            <label
              className="block text-xs font-semibold tracking-wide uppercase mb-2"
              style={{ color: 'var(--muted)' }}
            >
              아이디
            </label>
            <input
              type="text"
              value={id}
              onChange={(e) => setId(e.target.value)}
              style={inputStyle}
              placeholder="아이디"
              onFocus={(e) => { e.currentTarget.style.borderColor = 'var(--accent)'; }}
              onBlur={(e) => { e.currentTarget.style.borderColor = 'var(--line)'; }}
              required
            />
          </div>

          <div>
            <label
              className="block text-xs font-semibold tracking-wide uppercase mb-2"
              style={{ color: 'var(--muted)' }}
            >
              비밀번호
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              style={inputStyle}
              placeholder="비밀번호"
              onFocus={(e) => { e.currentTarget.style.borderColor = 'var(--accent)'; }}
              onBlur={(e) => { e.currentTarget.style.borderColor = 'var(--line)'; }}
              required
            />
          </div>


          <button
            type="submit"
            className="w-full mt-2 py-3.5 rounded-xl text-sm font-semibold transition-all cursor-pointer"
            style={{ background: 'var(--ink)', color: 'var(--bg)' }}
            onMouseEnter={(e) => { e.currentTarget.style.filter = 'brightness(1.15)'; }}
            onMouseLeave={(e) => { e.currentTarget.style.filter = 'none'; }}
          >
            로그인
          </button>
        </form>
      </div>
    </div>
  );
}
