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

  /* ============================================================
     FEEDBACK FORM
  ============================================================ */
  const feedbackTextarea = $('feedbackMessage');
  const feedbackBtn = $('feedbackSubmit');
  const feedbackError = $('feedbackError');

  function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
  }

  if (feedbackBtn && feedbackTextarea) {
    feedbackBtn.addEventListener('click', async () => {
      const message = feedbackTextarea.value.trim();
      feedbackError.style.display = 'none';

      if (!message) {
        feedbackError.textContent = 'Please enter a message before sending.';
        feedbackError.style.display = 'block';
        return;
      }

      feedbackBtn.disabled = true;
      feedbackBtn.textContent = 'Sending...';

      try {
        const res = await fetch('/settings/feedback', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': getCookie('XSRF-TOKEN')
          },
          body: JSON.stringify({ message })
        });

        const text = await res.text();

        if (!res.ok) {
          feedbackError.textContent = text.replace('error: ', '') || 'Something went wrong.';
          feedbackError.style.display = 'block';
        } else {
          feedbackTextarea.value = '';
          if (window.Toast) {
            window.Toast.show('Thanks for your feedback!', 'success');
          }
        }
      } catch (err) {
        feedbackError.textContent = 'Failed to send feedback. Please try again.';
        feedbackError.style.display = 'block';
      } finally {
        feedbackBtn.disabled = false;
        feedbackBtn.textContent = 'Send Feedback';
      }
    });
  }
})();
