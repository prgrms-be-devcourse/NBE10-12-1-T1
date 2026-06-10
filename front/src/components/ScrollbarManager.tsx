'use client';

import { useEffect } from 'react';

export default function ScrollbarManager() {
  useEffect(() => {
    const timers = new Map<Element, ReturnType<typeof setTimeout>>();

    const handleScroll = (e: Event) => {
      const el = e.currentTarget as Element;
      el.classList.add('is-scrolling');
      clearTimeout(timers.get(el));
      timers.set(el, setTimeout(() => {
        el.classList.remove('is-scrolling');
        timers.delete(el);
      }, 800));
    };

    const attach = () => {
      document.querySelectorAll('.scroll-area').forEach((el) => {
        el.removeEventListener('scroll', handleScroll);
        el.addEventListener('scroll', handleScroll, { passive: true });
      });
    };

    attach();

    const observer = new MutationObserver(attach);
    observer.observe(document.body, { childList: true, subtree: true });

    return () => {
      observer.disconnect();
      document.querySelectorAll('.scroll-area').forEach((el) => {
        el.removeEventListener('scroll', handleScroll);
      });
      timers.forEach(clearTimeout);
    };
  }, []);

  return null;
}
