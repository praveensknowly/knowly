(function () {
  'use strict';

  /* ============================================
     WORD-BY-WORD HERO ANIMATION INDEX
     ============================================ */
  document.querySelectorAll('.reveal-word').forEach(function (word, i) {
    word.style.setProperty('--i', i);
  });

  /* ============================================
     STAGGERED REVEAL DELAY (data-delay continuation)
     ============================================ */
  document.querySelectorAll('.reveal[style*="--delay"]').forEach(function (el) {
    var delay = getComputedStyle(el).getPropertyValue('--delay').trim();
    if (delay) {
      el.style.transitionDelay = (parseFloat(delay) * 0.05) + 's';
    }
  });

  /* ============================================
     MOBILE NAV TOGGLE
     ============================================ */
  var navToggle = document.getElementById('navToggle');
  var navLinksMobile = document.getElementById('navLinksMobile');

  if (navToggle && navLinksMobile) {
    navToggle.addEventListener('click', function () {
      var isOpen = navLinksMobile.classList.toggle('open');
      navToggle.classList.toggle('open', isOpen);
      navToggle.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
    });

    navLinksMobile.querySelectorAll('a').forEach(function (link) {
      link.addEventListener('click', function () {
        navLinksMobile.classList.remove('open');
        navToggle.classList.remove('open');
        navToggle.setAttribute('aria-expanded', 'false');
      });
    });
  }

  /* ============================================
     SCROLL REVEAL — Intersection Observer
     ============================================ */
  var revealEls = document.querySelectorAll('.reveal');

  if ('IntersectionObserver' in window) {
    var io = new IntersectionObserver(
      function (entries) {
        entries.forEach(function (entry, idx) {
          if (entry.isIntersecting) {
            var siblings = Array.prototype.slice.call(
              entry.target.parentElement.querySelectorAll('.reveal')
            );
            var position = siblings.indexOf(entry.target);
            var staggerDelay = Math.max(position, 0) * 80;

            setTimeout(function () {
              entry.target.classList.add('visible');
            }, staggerDelay);

            io.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.12, rootMargin: '0px 0px -40px 0px' }
    );

    revealEls.forEach(function (el) { io.observe(el); });
  } else {
    revealEls.forEach(function (el) { el.classList.add('visible'); });
  }

  /* ============================================
     FLOATING CARD PARALLAX (mouse move, desktop only)
     ============================================ */
  var heroVisual = document.getElementById('heroVisual');

  if (heroVisual && window.matchMedia('(min-width: 1025px)').matches) {
    var cards = heroVisual.querySelectorAll('.float-card');

    heroVisual.addEventListener('mousemove', function (e) {
      var rect = heroVisual.getBoundingClientRect();
      var cx = (e.clientX - rect.left) / rect.width - 0.5;
      var cy = (e.clientY - rect.top) / rect.height - 0.5;

      cards.forEach(function (card) {
        var depth = parseFloat(card.getAttribute('data-depth')) || 20;
        var moveX = cx * depth;
        var moveY = cy * depth;
        card.style.setProperty('--parallax-x', moveX.toFixed(2) + 'px');
        card.style.setProperty('--parallax-y', moveY.toFixed(2) + 'px');
        card.style.transform = 'translate(' + moveX.toFixed(2) + 'px, ' + moveY.toFixed(2) + 'px)';
      });
    });

    heroVisual.addEventListener('mouseleave', function () {
      cards.forEach(function (card) {
        card.style.transform = '';
      });
    });
  }

  /* ============================================
     ANIMATED SEARCH PLACEHOLDER
     ============================================ */
  var searchInput = document.getElementById('searchInput');

  if (searchInput) {
    var phrases = [
      'Java Developer',
      'Organic Farming',
      'Tax Advice',
      'Water Pump Repair',
      'Cooking'
    ];

    var phraseIndex = 0;
    var charIndex = 0;
    var isDeleting = false;
    var typingSpeed = 65;
    var deletingSpeed = 35;
    var holdDuration = 1500;

    function typeLoop() {
      if (document.activeElement === searchInput) {
        setTimeout(typeLoop, 400);
        return;
      }

      var current = phrases[phraseIndex];

      if (!isDeleting) {
        charIndex++;
        searchInput.placeholder = current.slice(0, charIndex);

        if (charIndex === current.length) {
          isDeleting = true;
          setTimeout(typeLoop, holdDuration);
          return;
        }
      } else {
        charIndex--;
        searchInput.placeholder = current.slice(0, charIndex);

        if (charIndex === 0) {
          isDeleting = false;
          phraseIndex = (phraseIndex + 1) % phrases.length;
        }
      }

      setTimeout(typeLoop, isDeleting ? deletingSpeed : typingSpeed);
    }

    setTimeout(typeLoop, 1200);
  }

  /* ============================================
     WAITLIST FORM
     ============================================ */
  var waitlistForm = document.getElementById('waitlistForm');
  var waitlistStatus = document.getElementById('waitlistStatus');

  if (waitlistForm && waitlistStatus) {
    waitlistForm.addEventListener('submit', function (e) {
      e.preventDefault();
      var emailInput = waitlistForm.querySelector('.waitlist-input');
      var email = emailInput.value.trim();

      if (!email) return;

      waitlistStatus.textContent = "You're on the list. We'll email you when we launch.";
      emailInput.value = '';
      emailInput.blur();
    });
  }

  /* ============================================
     SMOOTH SCROLL OFFSET FOR STICKY NAV
     ============================================ */
  document.querySelectorAll('a[href^="#"]').forEach(function (anchor) {
    anchor.addEventListener('click', function (e) {
      var targetId = this.getAttribute('href');
      if (targetId.length <= 1) return;

      var target = document.querySelector(targetId);
      if (!target) return;

      e.preventDefault();
      var navbar = document.querySelector('.navbar');
      var navHeight = navbar ? navbar.offsetHeight : 0;
      var top = target.getBoundingClientRect().top + window.pageYOffset - navHeight - 16;

      window.scrollTo({ top: top, behavior: 'smooth' });
    });
  });

})();