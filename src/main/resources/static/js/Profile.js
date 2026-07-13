/**
 * Knowly · Profile.js
 * Vanilla JS — ES6+ — No dependencies
 */

document.addEventListener('DOMContentLoaded', () => {

  /* =========================================================
     Utilities
  ========================================================= */

  const prefersReducedMotion = () =>
    window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  function throttle(fn, wait) {
    let last = 0;
    return function (...args) {
      const now = Date.now();
      if (now - last >= wait) {
        last = now;
        fn.apply(this, args);
      }
    };
  }

  function debounce(fn, wait) {
    let timer;
    return function (...args) {
      clearTimeout(timer);
      timer = setTimeout(() => fn.apply(this, args), wait);
    };
  }

  /* =========================================================
     1. Mobile Navigation
  ========================================================= */

  function initNavbar() {
    const nav       = document.getElementById('pfNav');
    const toggle    = document.getElementById('pfNavToggle');
    const mobile    = document.getElementById('pfNavMobile');

    if (!nav || !toggle || !mobile) return;

    function openMenu() {
      toggle.classList.add('open');
      mobile.classList.add('open');
      toggle.setAttribute('aria-expanded', 'true');
    }

    function closeMenu() {
      toggle.classList.remove('open');
      mobile.classList.remove('open');
      toggle.setAttribute('aria-expanded', 'false');
    }

    function isOpen() {
      return mobile.classList.contains('open');
    }

    toggle.addEventListener('click', () => {
      isOpen() ? closeMenu() : openMenu();
    });

    // Close when a mobile link is clicked
    mobile.querySelectorAll('a').forEach(link => {
      link.addEventListener('click', closeMenu);
    });

    // Close on outside click
    document.addEventListener('click', e => {
      if (isOpen() && !nav.contains(e.target)) {
        closeMenu();
      }
    });

    // Close and adapt on resize
    window.addEventListener('resize', debounce(() => {
      if (window.innerWidth > 960) {
        closeMenu();
      }
    }, 200));

    // Sticky — add/remove scrolled class
    const onScroll = throttle(() => {
      nav.classList.toggle('pf-nav--scrolled', window.scrollY > 50);
    }, 100);

    window.addEventListener('scroll', onScroll, { passive: true });
  }

  /* =========================================================
     2. Scroll Reveal
  ========================================================= */

  function initReveal() {
    const targets = document.querySelectorAll('.pf-reveal');
    if (!targets.length) return;

    if (prefersReducedMotion()) {
      targets.forEach(el => el.classList.add('pf-visible'));
      return;
    }

    const observer = new IntersectionObserver(
      entries => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            entry.target.classList.add('pf-visible');
            observer.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.12, rootMargin: '0px 0px -40px 0px' }
    );

    targets.forEach(el => observer.observe(el));
  }

  /* =========================================================
     3. Profile Completion Bar Animation
  ========================================================= */

  function initCompletionBar() {
    const bar = document.getElementById('pfCompletionBar');
    if (!bar) return;

    const finalWidth = bar.style.width || '0%';

    if (prefersReducedMotion()) return;

    // Collapse to 0 immediately, then animate to final
    bar.style.transition = 'none';
    bar.style.width = '0%';

    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        bar.style.transition = 'width 1.2s cubic-bezier(0.16, 1, 0.3, 1)';
        bar.style.width = finalWidth;
      });
    });
  }

  /* =========================================================
     4. Skill Rating Stars
  ========================================================= */

  function initSkillStars() {
    const containers = document.querySelectorAll('.pf-skill-stars');
    if (!containers.length) return;

    containers.forEach(container => {
      const score = parseFloat(container.getAttribute('data-score')) || 0;
      const clamped = Math.min(Math.max(score, 0), 5);
      container.innerHTML = buildStarsSVG(clamped);
      container.setAttribute('aria-label', `Rating: ${clamped.toFixed(1)} out of 5`);
    });
  }

  function buildStarsSVG(score) {
    let html = '';
    for (let i = 1; i <= 5; i++) {
      const fill = getFillRatio(score, i);
      html += starSVG(i, fill);
    }
    return html;
  }

  function getFillRatio(score, position) {
    if (score >= position) return 1;
    if (score < position - 1) return 0;
    return score - (position - 1);
  }

  function starSVG(index, fillRatio) {
    const id = `star-grad-${index}-${Math.random().toString(36).slice(2, 7)}`;
    const filledStop   = '#F59E0B';
    const emptyStop    = '#E5E7EB';
    const fillPercent  = Math.round(fillRatio * 100);

    return `
      <svg width="14" height="14" viewBox="0 0 24 24"
           fill="none" xmlns="http://www.w3.org/2000/svg"
           class="pf-star" aria-hidden="true">
        <defs>
          <linearGradient id="${id}" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="${fillPercent}%"  stop-color="${filledStop}"/>
            <stop offset="${fillPercent}%"  stop-color="${emptyStop}"/>
          </linearGradient>
        </defs>
        <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"
              fill="url(#${id})" stroke="${filledStop}"
              stroke-width="0.5" stroke-linejoin="round"/>
      </svg>`;
  }

  /* =========================================================
     5. Skill Card Hover (CSS already handles transform;
        JS adds a subtle box-shadow pulse via class toggle)
  ========================================================= */

  function initSkillColors() {
    const icons = document.querySelectorAll('.pf-skill-icon[data-skill]');
    if (!icons.length) return;

    icons.forEach(icon => {
      const skillName = icon.getAttribute('data-skill') || '';
      const gradient  = generateSkillGradient(skillName);
      icon.style.background  = gradient;
      icon.style.boxShadow   = generateSkillShadow(skillName);
    });
  }

  /**
   * Deterministic gradient from skill name.
   * Same name → always same colours.
   */
  function hashStr(str) {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      hash = (hash << 5) - hash + str.charCodeAt(i);
      hash |= 0;
    }
    return Math.abs(hash);
  }

  const PALETTE = [
    ['#2563EB', '#7C3AED'],
    ['#0D9488', '#2563EB'],
    ['#7C3AED', '#DB2777'],
    ['#D97706', '#DC2626'],
    ['#059669', '#0891B2'],
    ['#2563EB', '#0D9488'],
    ['#9333EA', '#2563EB'],
    ['#DC2626', '#D97706'],
    ['#0891B2', '#10B981'],
    ['#4F46E5', '#7C3AED'],
  ];

  function generateSkillGradient(name) {
    const idx     = hashStr(name.toLowerCase()) % PALETTE.length;
    const [c1, c2] = PALETTE[idx];
    return `linear-gradient(135deg, ${c1}, ${c2})`;
  }

  function generateSkillShadow(name) {
    const idx = hashStr(name.toLowerCase()) % PALETTE.length;
    const c1  = PALETTE[idx][0];
    return `0 4px 12px ${hexToRgba(c1, 0.30)}`;
  }

  function hexToRgba(hex, alpha) {
    const r = parseInt(hex.slice(1, 3), 16);
    const g = parseInt(hex.slice(3, 5), 16);
    const b = parseInt(hex.slice(5, 7), 16);
    return `rgba(${r},${g},${b},${alpha})`;
  }

  /* =========================================================
     6. Ripple Effect
  ========================================================= */

  function initRipple() {
    // Inject keyframe + styles once
    if (!document.getElementById('pf-ripple-style')) {
      const style = document.createElement('style');
      style.id = 'pf-ripple-style';
      style.textContent = `
        .pf-ripple-host { position: relative; overflow: hidden; }
        .pf-ripple-wave {
          position: absolute;
          border-radius: 50%;
          transform: scale(0);
          animation: pf-ripple-anim 0.55s linear;
          background: rgba(255,255,255,0.28);
          pointer-events: none;
        }
        @keyframes pf-ripple-anim {
          to { transform: scale(4); opacity: 0; }
        }
      `;
      document.head.appendChild(style);
    }

    const selectors = [
      '.pf-btn',
      '.pf-btn-primary',
      '.pf-btn-outline',
      '.pf-skill-card',
      '.pf-project-card',
      '.pf-cert-card',
      '.pf-quick-link',
      '.pf-card-edit',
      '.pf-empty-action',
    ];

    document.querySelectorAll(selectors.join(',')).forEach(el => {
      if (!el.classList.contains('pf-ripple-host')) {
        el.classList.add('pf-ripple-host');
      }
      el.addEventListener('click', createRipple, { passive: true });
    });
  }

  function createRipple(e) {
    if (prefersReducedMotion()) return;
    const el   = e.currentTarget;
    const rect = el.getBoundingClientRect();
    const size = Math.max(rect.width, rect.height);

    const wave = document.createElement('span');
    wave.className = 'pf-ripple-wave';
    wave.style.cssText = `
      width:  ${size}px;
      height: ${size}px;
      left:   ${e.clientX - rect.left - size / 2}px;
      top:    ${e.clientY - rect.top  - size / 2}px;
    `;

    el.appendChild(wave);
    wave.addEventListener('animationend', () => wave.remove(), { once: true });
  }

  /* =========================================================
     7. Smooth Scroll
  ========================================================= */

  function initSmoothScroll() {
    if (prefersReducedMotion()) return;

    document.querySelectorAll('a[href^="#"]').forEach(link => {
      link.addEventListener('click', e => {
        const target = document.querySelector(link.getAttribute('href'));
        if (!target) return;
        e.preventDefault();
        target.scrollIntoView({ behavior: 'smooth', block: 'start' });
        target.setAttribute('tabindex', '-1');
        target.focus({ preventScroll: true });
      });
    });
  }

  /* =========================================================
     8. Avatar Image Fallback
  ========================================================= */

  function initAvatarFallback() {
    const avatar   = document.getElementById('pfAvatar');
    if (!avatar) return;

    const img      = avatar.querySelector('.pf-avatar-img');
    const letter   = avatar.querySelector('.pf-avatar-letter');

    if (!img || !letter) return;

    img.addEventListener('error', () => {
      img.style.display    = 'none';
      letter.style.display = 'flex';
    });
  }

  /* =========================================================
     9. Accessibility — keyboard interaction for cards
  ========================================================= */

  function initAccessibility() {
    const interactiveCards = document.querySelectorAll(
      '.pf-skill-card, .pf-project-card, .pf-cert-card'
    );

    interactiveCards.forEach(card => {
      // Only enhance if card doesn't already contain a focused link/button
      if (!card.querySelector('a, button')) {
        card.setAttribute('tabindex', '0');
        card.setAttribute('role', 'button');

        card.addEventListener('keydown', e => {
          if (e.key === 'Enter' || e.key === ' ') {
            e.preventDefault();
            card.click();
          }
        });
      }
    });
  }

  /* =========================================================
     Init — call all modules
  ========================================================= */

  function init() {
    initNavbar();
    initReveal();
    initCompletionBar();
    initSkillStars();
    initSkillColors();
    initRipple();
    initSmoothScroll();
    initAvatarFallback();
    initAccessibility();
  }

  init();

});