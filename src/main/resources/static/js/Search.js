/* ================================================================
   Knowly — Search.js
   Pure UI interactions. No API calls. No AJAX.
   Modules:
     1. Mobile nav toggle
     2. Nav avatar gradient
     3. Navbar scroll shadow
     4. Search box focus enhancement
     5. Expert score ring animation (SVG stroke-dasharray)
     6. Sort functionality (client-side reorder)
     7. Chip hover micro-interaction
     8. Card entrance animation (Intersection Observer)
     9. Whole-card click / keyboard navigation
================================================================ */

(function () {
  'use strict';

  /* ─────────────────────────────
     HELPERS
  ───────────────────────────── */
  function $(id)   { return document.getElementById(id); }
  function $$(s, ctx) { return Array.from((ctx || document).querySelectorAll(s)); }

  /* ─────────────────────────────
     2. NAV AVATAR GRADIENT
     Stable hue from first initial
  ───────────────────────────── */
  var gradPairs = [
    ['#2563EB', '#7C3AED'],
    ['#10B981', '#2563EB'],
    ['#F59E0B', '#EF4444'],
    ['#7C3AED', '#10B981'],
    ['#EF4444', '#F59E0B'],
    ['#0891B2', '#2563EB'],
    ['#059669', '#0891B2'],
    ['#DC2626', '#7C3AED'],
  ];
  function gradFor(ch) {
    if (!ch) return gradPairs[0];
    var i = (ch.toUpperCase().charCodeAt(0) - 65) % gradPairs.length;
    return gradPairs[Math.max(0, i)];
  }
  var navAvatar = $('navAvatar');
  if (navAvatar) {
    var initial = navAvatar.textContent.trim().charAt(0);
    var pair    = gradFor(initial);
    navAvatar.style.background = 'linear-gradient(135deg,' + pair[0] + ',' + pair[1] + ')';
  }

  /* ─────────────────────────────
     3. NAVBAR SCROLL SHADOW
  ───────────────────────────── */
  var navbar = $('navbar');
  if (navbar) {
    window.addEventListener('scroll', function () {
      navbar.style.boxShadow = window.scrollY > 6
        ? '0 1px 20px rgba(15,23,42,0.09)'
        : '';
    }, { passive: true });
  }

  /* ─────────────────────────────
     4. SEARCH INPUT FOCUS RING
     Shifts the box shadow smoothly
     on the landing and compact boxes
  ───────────────────────────── */
  ['sSearchBox', 'sCompactBox', 'sEmptyBox'].forEach(function (id) {
    var box = $(id);
    if (!box) return;
    var input = box.querySelector('.s-search-input');
    if (!input) return;
    input.addEventListener('focus', function () { box.setAttribute('data-focused', 'true'); });
    input.addEventListener('blur',  function () { box.removeAttribute('data-focused'); });
  });

  /* ─────────────────────────────
     5. EXPERT SCORE RING ANIMATION
     Ring is drawn with r=26 (viewBox 0 0 64 64),
     so circumference = 2π×26 ≈ 163.4
     (ring is already rotated -90° in CSS)
  ───────────────────────────── */
  var CIRCUMFERENCE = 2 * Math.PI * 26; // ≈ 163.4

  function ensureGradientDef(ring, card) {
    var idx    = $$('.s-expert-card').indexOf(card);
    var gradId = 'sg' + idx;

    if (!document.getElementById(gradId)) {
      var svgNS = 'http://www.w3.org/2000/svg';
      var defs  = document.createElementNS(svgNS, 'defs');
      var grad  = document.createElementNS(svgNS, 'linearGradient');
      grad.setAttribute('id', gradId);
      grad.setAttribute('x1', '0%');
      grad.setAttribute('y1', '0%');
      grad.setAttribute('x2', '100%');
      grad.setAttribute('y2', '0%');

      ['#2563EB', '#10B981'].forEach(function (col, ci) {
        var stop = document.createElementNS(svgNS, 'stop');
        stop.setAttribute('offset', ci === 0 ? '0%' : '100%');
        stop.setAttribute('stop-color', col);
        grad.appendChild(stop);
      });

      defs.appendChild(grad);
      var ringSvg = ring.closest('svg');
      if (ringSvg) ringSvg.insertBefore(defs, ringSvg.firstChild);
    }

    ring.setAttribute('stroke', 'url(#' + gradId + ')');
  }

  function animateRing(ring) {
    var pct  = parseFloat(ring.getAttribute('data-pct') || '0');
    var dash = (pct / 100) * CIRCUMFERENCE;
    var card = ring.closest('.s-expert-card');
    if (card) ensureGradientDef(ring, card);

    setTimeout(function () {
      ring.style.strokeDasharray = dash + ' ' + CIRCUMFERENCE;
    }, 200);
  }

  function animateAllRings() {
    $$('.s-ring-fill').forEach(animateRing);
  }

  /* Use IntersectionObserver to trigger the ring animation
     as each card scrolls into view */
  if ('IntersectionObserver' in window) {
    var cardObs = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (!entry.isIntersecting) return;
        var card  = entry.target;
        $$('.s-ring-fill', card).forEach(animateRing);
        cardObs.unobserve(card);
      });
    }, { threshold: 0.25 });

    $$('.s-expert-card').forEach(function (card) {
      /* Reset ring to zero so the animation is visible */
      $$('.s-ring-fill', card).forEach(function (r) {
        r.style.strokeDasharray = '0 ' + CIRCUMFERENCE;
        r.style.transition = 'stroke-dasharray 1s cubic-bezier(0.22,1,0.36,1)';
      });
      cardObs.observe(card);
    });
  } else {
    /* Fallback for older browsers */
    animateAllRings();
  }

  /* ─────────────────────────────
     6. CLIENT-SIDE SORT
  ───────────────────────────── */
  var sortSelect = $('sSortSelect');
  var expertList = document.querySelector('.s-expert-list');

  if (sortSelect && expertList) {
    sortSelect.addEventListener('change', function () {
      var cards  = Array.from(expertList.querySelectorAll('.s-expert-card'));
      var sortBy = sortSelect.value;

      cards.sort(function (a, b) {
        var aScore = parseFloat(a.getAttribute('data-score')          || '0');
        var bScore = parseFloat(b.getAttribute('data-score')          || '0');
        var aExp   = parseFloat(a.getAttribute('data-experience')     || '0');
        var bExp   = parseFloat(b.getAttribute('data-experience')     || '0');
        var aCert  = parseFloat(a.getAttribute('data-certifications') || '0');
        var bCert  = parseFloat(b.getAttribute('data-certifications') || '0');

        if (sortBy === 'score')          return bScore - aScore;
        if (sortBy === 'experience')     return bExp   - aExp;
        if (sortBy === 'certifications') return bCert  - aCert;
        /* newest — keep original DOM order, so no change needed */
        return 0;
      });

      /* Fade out, reorder, fade in.
         Note: appendChild() here MOVES the existing card nodes
         rather than recreating them, so the click/keyboard
         listeners attached in module 9 stay intact after sorting —
         no need to re-bind them. */
      expertList.style.opacity = '0';
      expertList.style.transition = 'opacity 0.2s ease';

      setTimeout(function () {
        cards.forEach(function (card) { expertList.appendChild(card); });
        expertList.style.opacity = '1';
      }, 200);
    });
  }

  /* ─────────────────────────────
     7. CHIP MICRO-INTERACTION
     Tiny scale pulse on click
  ───────────────────────────── */
  $$('.s-chip, .s-card-chip').forEach(function (chip) {
    chip.addEventListener('click', function (e) {
      /* Don't let a chip click trigger the card's own navigation */
      e.stopPropagation();
      chip.style.transform = 'scale(0.93)';
      setTimeout(function () { chip.style.transform = ''; }, 160);
    });
  });

  /* ─────────────────────────────
     8. CARD ENTRANCE STAGGER
  ───────────────────────────── */
  var expertCards = $$('.s-expert-card');
  expertCards.forEach(function (card, i) {
    card.style.opacity   = '0';
    card.style.transform = 'translateY(14px)';
    card.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
    card.style.transitionDelay = (i * 70) + 'ms';
  });

  if (expertCards.length && 'IntersectionObserver' in window) {
    var entranceObs = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (!entry.isIntersecting) return;
        entry.target.style.opacity   = '1';
        entry.target.style.transform = 'translateY(0)';
        entranceObs.unobserve(entry.target);
      });
    }, { threshold: 0.12 });

    expertCards.forEach(function (card) { entranceObs.observe(card); });
  } else {
    expertCards.forEach(function (card) {
      card.style.opacity   = '1';
      card.style.transform = 'translateY(0)';
    });
  }

  /* ─────────────────────────────
     9. WHOLE-CARD CLICK / KEYBOARD NAVIGATION
     Each .s-expert-card carries a data-url (rendered by
     Thymeleaf). Rather than wrapping the card in an <a>
     (which previously broke layout/image sizing), we bind
     navigation directly to the <li> itself.
  ───────────────────────────── */
  function goToCard(card) {
    var url = card.getAttribute('data-url');
    if (url) window.location.href = url;
  }

  $$('.s-expert-card').forEach(function (card) {
    if (!card.getAttribute('data-url')) return;

    card.addEventListener('click', function () {
      goToCard(card);
    });

    /* Keyboard support since the card is tabindex="0" / role="link" */
    card.addEventListener('keydown', function (e) {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        goToCard(card);
      }
    });
  });

})();