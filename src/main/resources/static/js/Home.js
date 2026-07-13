/* ================================================================
   Knowly — Home.js (Post-Login Dashboard)
   Modules:
     1. Dynamic greeting (Morning / Afternoon / Evening)
     2. Mobile nav toggle
     3. Scroll-reveal (Intersection Observer)
     4. Avatar gradient by initial
     5. Completion bar animation
     6. Navbar scroll shadow
     7. Trend chip stagger entrance
     8. Expert card stagger entrance
================================================================ */

(function () {
  'use strict';

  /* ──────────────────────────────────────────────────────────
     HELPERS
  ────────────────────────────────────────────────────────── */
  function $(id) { return document.getElementById(id); }
  function $$(s, ctx) { return Array.from((ctx || document).querySelectorAll(s)); }

  /* ──────────────────────────────────────────────────────────
     1. DYNAMIC GREETING
  ────────────────────────────────────────────────────────── */
  var greetingEl = $('greetingText');
  if (greetingEl) {
    var hour = new Date().getHours();
    var greeting = hour < 12 ? 'Good morning,'
                 : hour < 17 ? 'Good afternoon,'
                              : 'Good evening,';
    greetingEl.textContent = greeting;
  }

  /* ──────────────────────────────────────────────────────────
     2. MOBILE NAV TOGGLE
  ────────────────────────────────────────────────────────── */
  var navToggle = $('navToggle');
  var navMobile = $('navMobile');

  if (navToggle && navMobile) {
    navToggle.addEventListener('click', function () {
      var open = navMobile.classList.toggle('open');
      navToggle.classList.toggle('open', open);
      navToggle.setAttribute('aria-expanded', open ? 'true' : 'false');
    });

    $$('a', navMobile).forEach(function (link) {
      link.addEventListener('click', function () {
        navMobile.classList.remove('open');
        navToggle.classList.remove('open');
        navToggle.setAttribute('aria-expanded', 'false');
      });
    });
  }

  /* ──────────────────────────────────────────────────────────
     3. SCROLL REVEAL — Intersection Observer
     Same pattern as main.js / Profile.js
  ────────────────────────────────────────────────────────── */
  var revealEls = $$('.reveal');

  if ('IntersectionObserver' in window) {
    var revealObs = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (!entry.isIntersecting) return;
        var el    = entry.target;
        var delay = parseFloat(getComputedStyle(el).getPropertyValue('--d').trim() || '0') * 80;
        setTimeout(function () { el.classList.add('visible'); }, delay);
        revealObs.unobserve(el);
      });
    }, { threshold: 0.1, rootMargin: '0px 0px -24px 0px' });

    revealEls.forEach(function (el) { revealObs.observe(el); });
  } else {
    revealEls.forEach(function (el) { el.classList.add('visible'); });
  }

  /* ──────────────────────────────────────────────────────────
     4. AVATAR GRADIENT BY INITIAL
     Consistent hue per first character — same pairs as Profile.js
  ────────────────────────────────────────────────────────── */
  var gradientPairs = [
    ['#2563EB', '#7C3AED'],
    ['#10B981', '#2563EB'],
    ['#F59E0B', '#EF4444'],
    ['#7C3AED', '#10B981'],
    ['#EF4444', '#F59E0B'],
    ['#0891B2', '#2563EB'],
    ['#059669', '#0891B2'],
    ['#DC2626', '#7C3AED'],
  ];

  function gradientForInitial(ch) {
    if (!ch) return gradientPairs[0];
    var idx = (ch.toUpperCase().charCodeAt(0) - 65) % gradientPairs.length;
    return gradientPairs[Math.max(0, idx)];
  }

  /* nav avatar */
  var navAvatar = $('navAvatar');
  if (navAvatar) {
    var initial = (navAvatar.querySelector('.avatar-initial') || navAvatar).textContent.trim().charAt(0);
    var pair    = gradientForInitial(initial);
    navAvatar.style.background = 'linear-gradient(135deg,' + pair[0] + ',' + pair[1] + ')';
  }

  /* sidebar completion avatar */
  var sideAvatar = $('sideAvatar');
  if (sideAvatar) {
    var sideInitial = sideAvatar.textContent.trim().charAt(0);
    var sidePair    = gradientForInitial(sideInitial);
    sideAvatar.style.background = 'linear-gradient(135deg,' + sidePair[0] + ',' + sidePair[1] + ')';
  }

  /* ──────────────────────────────────────────────────────────
     5. COMPLETION BAR ANIMATION
     Reads aria-valuenow from the wrapper, animates fill width.
  ────────────────────────────────────────────────────────── */
  var completionWrap = document.querySelector('.completion-bar-wrap');
  var completionFill = $('completionFill');

  if (completionWrap && completionFill && 'IntersectionObserver' in window) {
    var pct = Math.min(100, Math.max(0, parseInt(completionWrap.getAttribute('aria-valuenow') || '25', 10)));

    var barObs = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (!entry.isIntersecting) return;
        setTimeout(function () {
          completionFill.style.width = pct + '%';
        }, 250);
        barObs.unobserve(entry.target);
      });
    }, { threshold: 0.5 });

    barObs.observe(completionWrap);
  } else if (completionFill) {
    completionFill.style.width = '25%';
  }

  /* ──────────────────────────────────────────────────────────
     6. NAVBAR SCROLL SHADOW
  ────────────────────────────────────────────────────────── */
  var navbar = $('navbar');
  if (navbar) {
    window.addEventListener('scroll', function () {
      navbar.style.boxShadow = window.scrollY > 8
        ? '0 1px 20px rgba(17,24,39,0.09)'
        : '';
    }, { passive: true });
  }

  /* ──────────────────────────────────────────────────────────
     7. TREND CHIP STAGGER ENTRANCE
  ────────────────────────────────────────────────────────── */
  var chips = $$('.trend-chip');
  chips.forEach(function (chip, i) {
    chip.style.opacity    = '0';
    chip.style.transform  = 'translateY(6px)';
    chip.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
    chip.style.transitionDelay = (i * 45) + 'ms';
  });

  if (chips.length && 'IntersectionObserver' in window) {
    var chipObs = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (!entry.isIntersecting) return;
        $$('.trend-chip', entry.target).forEach(function (c) {
          c.style.opacity = '1';
          c.style.transform = 'translateY(0)';
        });
        chipObs.unobserve(entry.target);
      });
    }, { threshold: 0.2 });

    $$('.trending-chips').forEach(function (wrap) { chipObs.observe(wrap); });
  }

  /* ──────────────────────────────────────────────────────────
     8. EXPERT CARD STAGGER ENTRANCE
  ────────────────────────────────────────────────────────── */
  var expertCards = $$('.expert-card');
  expertCards.forEach(function (card, i) {
    card.style.opacity    = '0';
    card.style.transform  = 'translateY(10px)';
    card.style.transition = 'opacity 0.4s ease, transform 0.4s ease';
    card.style.transitionDelay = (i * 60) + 'ms';
  });

  if (expertCards.length && 'IntersectionObserver' in window) {
    var cardObs = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (!entry.isIntersecting) return;
        $$('.expert-card', entry.target).forEach(function (c) {
          c.style.opacity = '1';
          c.style.transform = 'translateY(0)';
        });
        cardObs.unobserve(entry.target);
      });
    }, { threshold: 0.15 });

    $$('.expert-grid').forEach(function (grid) { cardObs.observe(grid); });
  }

})();