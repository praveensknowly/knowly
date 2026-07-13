/* ================================================================
   Knowly — Help Request Modal
   Pure vanilla JS. No frameworks, no network calls (the actual
   POST that creates the Help Request is left as a single
   commented-out spot in module 7 — swap it in for real use).

   Modules:
     1. Open / close core (backdrop + animation states)
     2. Focus trap + keyboard handling (ESC, Tab cycling)
     3. Skill chip single-select (+ per-skill rating in header)
     4. Subject character counter
     5. Field-level validation
     6. Button ripple effect
     7. Submit flow (validate → loading → success)
================================================================ */

(function () {
  'use strict';

  function $(id) { return document.getElementById(id); }

 var openButtons = document.querySelectorAll('.open-help-request');
  var backdrop   = $('hrmBackdrop');
  var modal      = $('hrmModal');
  var closeBtn   = $('hrmCloseBtn');
  var cancelBtn  = $('hrmCancelBtn');
  var sendBtn    = $('hrmSendBtn');

  var chips        = Array.from(document.querySelectorAll('.hrm-chip'));
  var skillInput   = $('hrmSkillInput');
  var ratingBox    = $('hrmSkillRating');
  var ratingValue  = $('hrmSkillRatingValue');

  var subject      = $('hrmSubject');
  var subjectCounter = $('hrmSubjectCounter');
  var description  = $('hrmDescription');

  var SUBJECT_MAX  = 150;
  var lastFocusedEl = null;

  /* ─────────────────────────────
     1. OPEN / CLOSE CORE
  ───────────────────────────── */
  function openModal() {
      lastFocusedEl = document.activeElement;

      backdrop.hidden = false;

      void backdrop.offsetWidth;

      backdrop.classList.add("hrm-open");

      document.body.style.overflow = "hidden";

      var firstChip = modal.querySelector(".hrm-chip");
      if (firstChip) {
          firstChip.focus();
      }

      document.addEventListener("keydown", onKeydown);
  }
  function closeModal() {
    backdrop.classList.remove('hrm-open');
    backdrop.classList.add('hrm-closing');
    document.removeEventListener('keydown', onKeydown);

    var CLOSE_DURATION = 220;
    setTimeout(function () {
      backdrop.hidden = true;
      backdrop.classList.remove('hrm-closing');
      document.body.style.overflow = '';
      resetForm();

      if (lastFocusedEl && typeof lastFocusedEl.focus === 'function') {
        lastFocusedEl.focus();
      }
    }, CLOSE_DURATION);
  }
  openButtons.forEach(function(button){
      button.addEventListener("click", function(){
          openModal();
      });

  });

  
  closeBtn.addEventListener('click', closeModal);
  cancelBtn.addEventListener('click', closeModal);

  // Click outside the modal (directly on the backdrop) closes it
  backdrop.addEventListener('mousedown', function (e) {
    if (e.target === backdrop) closeModal();
  });

  /* ─────────────────────────────
     2. FOCUS TRAP + KEYBOARD
  ───────────────────────────── */
  function onKeydown(e) {
    if (e.key === 'Escape') {
      closeModal();
      return;
    }
    if (e.key === 'Tab') {
      trapFocus(e);
    }
  }

  function getFocusable() {
    return Array.from(
      modal.querySelectorAll(
        'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
      )
    ).filter(function (el) { return !el.disabled && el.offsetParent !== null; });
  }

  function trapFocus(e) {
    var focusable = getFocusable();
    if (!focusable.length) return;

    var first = focusable[0];
    var last  = focusable[focusable.length - 1];

    if (e.shiftKey && document.activeElement === first) {
      e.preventDefault();
      last.focus();
    } else if (!e.shiftKey && document.activeElement === last) {
      e.preventDefault();
      first.focus();
    }
  }

  /* ─────────────────────────────
     3. SKILL CHIP SINGLE-SELECT
     The rating shown belongs to the SELECTED SKILL, not the
     helper — each chip carries its own data-rating.
  ───────────────────────────── */
  chips.forEach(function (chip) {
    chip.addEventListener('click', function () {
      chips.forEach(function (c) { c.setAttribute('aria-checked', 'false'); });
      chip.setAttribute('aria-checked', 'true');

     skillInput.value = chip.dataset.skillId;
      ratingValue.textContent = chip.dataset.rating;
      ratingBox.classList.remove('hrm-rating-empty');

      clearFieldError('skill');
    });
  });

  /* ─────────────────────────────
     4. SUBJECT CHARACTER COUNTER
  ───────────────────────────── */
  function updateSubjectCounter() {
    var len = subject.value.length;
    subjectCounter.textContent = len + ' / ' + SUBJECT_MAX;
    subjectCounter.classList.toggle('hrm-counter-limit', len >= SUBJECT_MAX - 10);
  }
  subject.addEventListener('input', function () {
    updateSubjectCounter();
    clearFieldError('subject');
  });

  description.addEventListener('input', function () {
    clearFieldError('description');
  });

  /* ─────────────────────────────
     5. FIELD-LEVEL VALIDATION
  ───────────────────────────── */
  function fieldWrapper(name) {
    return modal.querySelector('.hrm-field[data-field="' + name + '"]');
  }

  function setFieldError(name) {
    var wrapper = fieldWrapper(name);
    var errorEl = $('hrm' + capitalize(name) + 'Error');
    if (!wrapper || !errorEl) return;
    wrapper.classList.add('hrm-invalid');
    errorEl.hidden = false;
  }

  function clearFieldError(name) {
    var wrapper = fieldWrapper(name);
    var errorEl = $('hrm' + capitalize(name) + 'Error');
    if (!wrapper || !errorEl) return;
    wrapper.classList.remove('hrm-invalid');
    errorEl.hidden = true;
  }

  function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
  }

  function validateForm() {
    var isValid = true;

    if (!skillInput.value) { setFieldError('skill'); isValid = false; }
    else clearFieldError('skill');

    if (!subject.value.trim()) { setFieldError('subject'); isValid = false; }
    else clearFieldError('subject');

    if (!description.value.trim()) { setFieldError('description'); isValid = false; }
    else clearFieldError('description');

    return isValid;
  }

  /* ─────────────────────────────
     6. BUTTON RIPPLE EFFECT
  ───────────────────────────── */
  function addRipple(e, btn) {
    var rect = btn.getBoundingClientRect();
    var size = Math.max(rect.width, rect.height);
    var ripple = document.createElement('span');

    ripple.className = 'hrm-ripple';
    ripple.style.width = ripple.style.height = size + 'px';
    ripple.style.left = (e.clientX - rect.left - size / 2) + 'px';
    ripple.style.top  = (e.clientY - rect.top  - size / 2) + 'px';

    btn.appendChild(ripple);
    ripple.addEventListener('animationend', function () { ripple.remove(); });
  }

  [sendBtn, cancelBtn].forEach(function (btn) {
    btn.addEventListener('click', function (e) { addRipple(e, btn); });
  });

  /* ─────────────────────────────
     7. SUBMIT FLOW
     validate → brief loading state → success confirmation.
     Replace the setTimeout below with a real request to create
     the Help Request, e.g.

       fetch('/help-requests', {
         method: 'POST',
         headers: { 'Content-Type': 'application/json' },
         body: JSON.stringify({
           skill: skillInput.value,
           subject: subject.value.trim(),
           description: description.value.trim()
         })
       }).then(...).catch(...)
  ───────────────────────────── */
  sendBtn.addEventListener('click', function (e) {
    e.preventDefault();

    if (!validateForm()) {
      modal.classList.remove('hrm-shake');
      void modal.offsetWidth; // restart the animation if triggered again
      modal.classList.add('hrm-shake');

      var firstInvalid = modal.querySelector('.hrm-invalid');
      if (firstInvalid) {
        var focusable = firstInvalid.querySelector('input, textarea, [role="radio"]');
        if (focusable) focusable.focus();
      }
      return;
    }

    sendBtn.classList.add('hrm-loading');

    setTimeout(function () {
      sendBtn.classList.remove('hrm-loading');
      sendBtn.classList.add('hrm-sent');
      sendBtn.querySelector('.hrm-btn-label').textContent = 'Request Sent';

      setTimeout(function () {
        closeModal();
        // Submit the form after the animation completes
        sendBtn.closest('form').submit();
      }, 900);
    }, 900);
  });

  /* ─────────────────────────────
     RESET
     Restores the modal to its initial state after it closes,
     so reopening it starts clean.
  ───────────────────────────── */
  function resetForm() {
    chips.forEach(function (c) { c.setAttribute('aria-checked', 'false'); });
    skillInput.value = '';
    ratingValue.textContent = 'Select a skill';
    ratingBox.classList.add('hrm-rating-empty');

    subject.value = '';
    updateSubjectCounter();
    description.value = '';

    ['skill', 'subject', 'description'].forEach(clearFieldError);

    sendBtn.classList.remove('hrm-loading', 'hrm-sent');
    sendBtn.querySelector('.hrm-btn-label').textContent = 'Send Help Request';
  }

  /* ─────────────────────────────
     AVATAR IMAGE FALLBACK
  ───────────────────────────── */
  var avatarImg    = $('hrmAvatarImg');
  var avatarLetter = $('hrmAvatarLetter');
  if (avatarImg) {
    avatarImg.addEventListener('error', function () {
      avatarImg.hidden = true;
      if (avatarLetter) avatarLetter.hidden = false;
    });
  }

  // Initialize counter display on load
  updateSubjectCounter();

})();