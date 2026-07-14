/* Settings.js — Knowly
   Vanilla ES6, no libraries, IIFE pattern consistent with main.js / EditProfile.js
*/
(function () {
  'use strict';

  /* ============================================================
     HELPERS
  ============================================================ */
  const $ = id => document.getElementById(id);
  const $$ = (sel, ctx = document) => [...ctx.querySelectorAll(sel)];

  /* ============================================================
     SCROLL REVEAL
  ============================================================ */
  const revealEls = $$('.reveal');
  if ('IntersectionObserver' in window) {
    const io = new IntersectionObserver(entries => {
      entries.forEach(entry => {
        if (!entry.isIntersecting) return;
        const el  = entry.target;
        const sib = $$('.reveal', el.parentElement);
        const idx = sib.indexOf(el);
        const raw = getComputedStyle(el).getPropertyValue('--delay').trim();
        const ms  = raw ? parseFloat(raw) * 80 : idx * 80;
        setTimeout(() => el.classList.add('visible'), ms);
        io.unobserve(el);
      });
    }, { threshold: 0.1, rootMargin: '0px 0px -28px 0px' });
    revealEls.forEach(el => io.observe(el));
  } else {
    revealEls.forEach(el => el.classList.add('visible'));
  }

  /* ============================================================
     STICKY NAVBAR SHADOW
  ============================================================ */
  const navbar = $('navbar');
  if (navbar) {
    window.addEventListener('scroll', () => {
      navbar.style.boxShadow = window.scrollY > 10
        ? '0 1px 16px rgba(17,24,39,0.08)'
        : '';
    }, { passive: true });
  }
})();
