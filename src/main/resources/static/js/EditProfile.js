/* EditProfile.js — Knowly
   Vanilla ES6, no libraries, IIFE pattern consistent with main.js / SignUp.js
*/
(function () {
  'use strict';

  /* ============================================================
     HELPERS
  ============================================================ */
  const $ = id => document.getElementById(id);
  const $$ = (sel, ctx = document) => [...ctx.querySelectorAll(sel)];

  /* ============================================================
     1. MOBILE NAV TOGGLE
  ============================================================ */
  const navToggle = $('navToggle');
  const navMobile  = $('navMobile');

  if (navToggle && navMobile) {
    navToggle.addEventListener('click', () => {
      const open = navMobile.classList.toggle('open');
      navToggle.classList.toggle('open', open);
      navToggle.setAttribute('aria-expanded', String(open));
    });
    $$('a', navMobile).forEach(a => a.addEventListener('click', () => {
      navMobile.classList.remove('open');
      navToggle.classList.remove('open');
      navToggle.setAttribute('aria-expanded', 'false');
    }));
  }

  /* ============================================================
     2. SCROLL REVEAL
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
     3. STICKY NAVBAR SHADOW
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
     4. AVATAR GRADIENT (matches Profile.js)
  ============================================================ */
  const GRADIENTS = [
    ['#2563EB','#7C3AED'], ['#10B981','#2563EB'], ['#F59E0B','#EF4444'],
    ['#7C3AED','#10B981'], ['#EF4444','#F59E0B'], ['#0891B2','#2563EB'],
    ['#059669','#0891B2'], ['#DC2626','#7C3AED'],
  ];
  const gradientFor = letter => {
    if (!letter) return GRADIENTS[0];
    return GRADIENTS[Math.max(0, (letter.toUpperCase().charCodeAt(0) - 65) % GRADIENTS.length)];
  };

  function applyGradient(el, letter) {
    if (!el) return;
    const [a, b] = gradientFor(letter);
    el.style.background = `linear-gradient(135deg,${a},${b})`;
  }

  const avatarEl  = $('avatarWrap');
  const sideAvatarEl = $('sideAvatar');
  const navAvFall = document.querySelector('.avatar-initial');

  if (avatarEl) {
    const letter = (avatarEl.querySelector('.ep-avatar-letter') || {}).textContent?.trim();
    applyGradient(avatarEl, letter);
    if (sideAvatarEl && !sideAvatarEl.querySelector('img')) {
      applyGradient(sideAvatarEl, letter);
    }
  }
  if (navAvFall) {
    const nav = navAvFall.closest('.avatar');
    applyGradient(nav, navAvFall.textContent.trim());
  }

  /* ============================================================
     5. PROFILE PICTURE — upload, drag-drop, preview, remove
  ============================================================ */
  const avatarInput      = $('avatarInput');
  const avatarImg        = $('avatarImg');
  const avatarLetter     = $('avatarLetter');
  const avatarOverlayBtn = $('avatarOverlayBtn');
  const uploadBtn        = $('uploadBtn');
  const removeBtn        = $('removeBtn');
  const dropzone         = $('dropzone');
  const avatarError      = $('avatarError');
  const sideAvatarImgWrap = $('sideAvatar');

  const MAX_BYTES   = 5 * 1024 * 1024;
  const VALID_TYPES = ['image/jpeg', 'image/png', 'image/webp'];

  function setAvatarErr(msg) {
    if (avatarError) avatarError.textContent = msg;
  }

  function applyFile(file) {
    setAvatarErr('');
    if (!file) return;
    if (!VALID_TYPES.includes(file.type)) { setAvatarErr('Please choose a JPG, PNG or WEBP image.'); return; }
    if (file.size > MAX_BYTES) { setAvatarErr('Image must be smaller than 5 MB.'); return; }

    const reader = new FileReader();
    reader.onload = e => {
      if (avatarImg) { avatarImg.src = e.target.result; avatarImg.hidden = false; }
      if (avatarLetter) avatarLetter.style.opacity = '0';
      // Mirror the preview into the completion-card avatar too
      if (sideAvatarImgWrap) {
        let img = sideAvatarImgWrap.querySelector('img');
        if (!img) {
          img = document.createElement('img');
          img.className = 'completion-avatar-img';
          img.alt = 'Profile picture';
          sideAvatarImgWrap.appendChild(img);
        }
        img.src = e.target.result;
        sideAvatarImgWrap.querySelector('.completion-avatar-letter')?.style.setProperty('display', 'none');
      }
      markStep('picture', true);
      recalcCompletion();
    };
    reader.readAsDataURL(file);

    if (avatarInput && window.DataTransfer) {
      const dt = new DataTransfer();
      dt.items.add(file);
      avatarInput.files = dt.files;
    }
  }

  [avatarOverlayBtn, uploadBtn].forEach(btn => {
    if (btn) btn.addEventListener('click', () => avatarInput?.click());
  });

  if (avatarInput) {
    avatarInput.addEventListener('change', () => applyFile(avatarInput.files?.[0]));
  }

  if (removeBtn) {
    removeBtn.addEventListener('click', () => {
      setAvatarErr('');
      if (avatarInput) avatarInput.value = '';
      if (avatarImg)   { avatarImg.src = ''; avatarImg.hidden = true; }
      if (avatarLetter) avatarLetter.style.opacity = '1';
      markStep('picture', false);
      recalcCompletion();
    });
  }

  if (dropzone) {
    ['dragenter', 'dragover'].forEach(evt => dropzone.addEventListener(evt, e => {
      e.preventDefault(); e.stopPropagation();
      dropzone.classList.add('ep-drag-over');
    }));
    ['dragleave', 'drop'].forEach(evt => dropzone.addEventListener(evt, e => {
      e.preventDefault(); e.stopPropagation();
      dropzone.classList.remove('ep-drag-over');
    }));
    dropzone.addEventListener('drop', e => applyFile(e.dataTransfer?.files?.[0]));
    dropzone.addEventListener('keydown', e => {
      if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); avatarInput?.click(); }
    });
  }

  /* ============================================================
     6. BIO CHARACTER COUNTER
  ============================================================ */
  const bioEl    = $('bio');
  const bioCount = $('bioCount');
  const bioError = $('bioError');
  const BIO_MAX  = 1000;

  function updateBioCount() {
    if (!bioEl || !bioCount) return;
    const len = bioEl.value.length;
    bioCount.textContent = `${len} / ${BIO_MAX}`;
    bioCount.classList.toggle('ep-near-limit', len >= BIO_MAX * 0.9 && len < BIO_MAX);
    bioCount.classList.toggle('ep-at-limit',   len >= BIO_MAX);
  }

  function validateBio() {
    if (!bioEl) return true;
    const group = bioEl.closest('.ep-field');
    if (bioEl.value.length > BIO_MAX) {
      if (bioError) bioError.textContent = `Bio must be ${BIO_MAX} characters or fewer.`;
      group?.classList.add('ep-has-error');
      return false;
    }
    if (bioError) bioError.textContent = '';
    group?.classList.remove('ep-has-error');
    return true;
  }

  if (bioEl) {
    updateBioCount();
    bioEl.addEventListener('input', () => {
      if (bioEl.value.length > BIO_MAX) bioEl.value = bioEl.value.slice(0, BIO_MAX);
      updateBioCount();
      markStep('bio', bioEl.value.trim().length > 0);
      recalcCompletion();
      if (bioError) bioError.textContent = '';
      bioEl.closest('.ep-field')?.classList.remove('ep-has-error');
    });
  }

  /* ============================================================
     7. DATE OF BIRTH VALIDATION
  ============================================================ */
  const dobEl    = $('dob');
  const dobError = $('dobError');

  function validateDob() {
    if (!dobEl || !dobEl.value) { if (dobError) dobError.textContent = ''; return true; }
    const today  = new Date(); today.setHours(0, 0, 0, 0);
    const chosen = new Date(dobEl.value);
    if (chosen > today) {
      if (dobError) dobError.textContent = 'Date of birth cannot be in the future.';
      dobEl.closest('.ep-field')?.classList.add('ep-has-error');
      return false;
    }
    if (dobError) dobError.textContent = '';
    dobEl.closest('.ep-field')?.classList.remove('ep-has-error');
    return true;
  }

  if (dobEl) {
    dobEl.setAttribute('max', new Date().toISOString().split('T')[0]);
    dobEl.addEventListener('change', () => {
      validateDob();
      markStep('dob', !!dobEl.value);
      recalcCompletion();
    });
  }

  /* ============================================================
     8. LOCATION VALIDATION
  ============================================================ */
  const locationEl    = $('location');
  const locationError = $('locationError');
  const LOC_MAX        = 100;

  function validateLocation() {
    if (!locationEl) return true;
    if (locationEl.value.length > LOC_MAX) {
      if (locationError) locationError.textContent = `Location must be ${LOC_MAX} characters or fewer.`;
      locationEl.closest('.ep-field')?.classList.add('ep-has-error');
      return false;
    }
    if (locationError) locationError.textContent = '';
    locationEl.closest('.ep-field')?.classList.remove('ep-has-error');
    return true;
  }

  if (locationEl) {
    locationEl.addEventListener('input', () => {
      locationEl.closest('.ep-field')?.classList.remove('ep-has-error');
      if (locationError) locationError.textContent = '';
      markStep('location', locationEl.value.trim().length > 0);
      recalcCompletion();
    });
    locationEl.addEventListener('blur', validateLocation);
  }

  /* ============================================================
     9. GENDER change → completion
  ============================================================ */
  const genderEl = $('gender');
  if (genderEl) {
    genderEl.addEventListener('change', () => {
      markStep('gender', !!genderEl.value);
      recalcCompletion();
    });
  }

  /* ============================================================
     10. LANGUAGES — searchable chip selector
  ============================================================ */
  const LANG_OPTIONS = [
    'English', 'Hindi', 'Telugu', 'Tamil', 'Kannada', 'Malayalam',
    'Marathi', 'Bengali', 'Gujarati', 'Punjabi', 'Urdu',
    'French', 'Spanish', 'German', 'Italian', 'Portuguese',
    'Mandarin', 'Japanese', 'Korean', 'Arabic', 'Russian',
    'Dutch', 'Swedish', 'Turkish', 'Polish', 'Vietnamese',
  ];

  const langBox      = $('langBox');
  const langSearch   = $('langSearch');
  const langDropdown = $('langDropdown');
  const langHidden   = $('languagesHidden');

  // Wrap langBox in a relative-position div for dropdown positioning
  if (langBox) {
    const wrapper = document.createElement('div');
    wrapper.className = 'ep-lang-box-wrap';
    wrapper.style.position = 'relative';
    langBox.parentNode.insertBefore(wrapper, langBox);
    wrapper.appendChild(langBox);
    if (langDropdown) wrapper.appendChild(langDropdown);
  }

  function getSelected() {
    return $$('.ep-lang-chip', langBox).map(c => c.getAttribute('data-lang'));
  }

  function syncHidden() {
    if (langHidden) langHidden.value = getSelected().join(',');
    markStep('languages', getSelected().length > 0);
    recalcCompletion();
  }

  function makeChip(lang) {
    const chip = document.createElement('span');
    chip.className = 'ep-lang-chip';
    chip.setAttribute('data-lang', lang);

    const label = document.createElement('span');
    label.className = 'ep-lang-chip-text';
    label.textContent = lang;

    const rm = document.createElement('button');
    rm.type = 'button';
    rm.className = 'ep-lang-chip-remove';
    rm.setAttribute('aria-label', `Remove ${lang}`);
    rm.innerHTML = '&times;';
    rm.addEventListener('click', () => { chip.remove(); syncHidden(); });

    chip.appendChild(label);
    chip.appendChild(rm);
    return chip;
  }

  function addLang(lang) {
    if (!lang) return;
    const dupe = getSelected().some(l => l.toLowerCase() === lang.toLowerCase());
    if (dupe) return;
    const chip = makeChip(lang);
    langBox.insertBefore(chip, langSearch);
    syncHidden();
  }

  let activeIdx = -1;

  function renderDropdown(query = '') {
    if (!langDropdown) return;
    langDropdown.innerHTML = '';
    activeIdx = -1;

    const selected = getSelected().map(l => l.toLowerCase());
    const q = query.trim().toLowerCase();
    const matches = LANG_OPTIONS.filter(l => l.toLowerCase().includes(q));

    if (!matches.length) {
      const li = document.createElement('li');
      li.className = 'ep-lang-empty';
      li.textContent = 'No languages found';
      langDropdown.appendChild(li);
      langDropdown.hidden = false;
      return;
    }

    matches.forEach((lang, i) => {
      const isSel = selected.includes(lang.toLowerCase());
      const li = document.createElement('li');
      li.className = 'ep-lang-option' + (isSel ? ' ep-opt-selected' : '');
      li.setAttribute('role', 'option');
      li.setAttribute('aria-selected', String(isSel));
      li.dataset.idx = i;
      li.textContent = lang;
      if (isSel) {
        const tag = document.createElement('span');
        tag.style.cssText = 'font-size:.75rem;color:#9CA3AF;margin-left:auto';
        tag.textContent = 'Added';
        li.appendChild(tag);
      } else {
        li.addEventListener('click', () => {
          addLang(lang);
          if (langSearch) { langSearch.value = ''; langSearch.focus(); }
          renderDropdown('');
        });
      }
      langDropdown.appendChild(li);
    });
    langDropdown.hidden = false;
    langSearch?.setAttribute('aria-expanded', 'true');
  }

  function closeDropdown() {
    if (langDropdown) langDropdown.hidden = true;
    langSearch?.setAttribute('aria-expanded', 'false');
    activeIdx = -1;
  }

  function moveActive(dir) {
    const opts = $$('.ep-lang-option:not(.ep-opt-selected)', langDropdown);
    if (!opts.length) return;
    opts[activeIdx]?.classList.remove('ep-opt-focused');
    activeIdx = Math.min(Math.max(activeIdx + dir, 0), opts.length - 1);
    opts[activeIdx]?.classList.add('ep-opt-focused');
    opts[activeIdx]?.scrollIntoView({ block: 'nearest' });
  }

  if (langSearch) {
    langSearch.addEventListener('focus', () => renderDropdown(langSearch.value));
    langSearch.addEventListener('input', () => renderDropdown(langSearch.value));
    langSearch.addEventListener('keydown', e => {
      if (e.key === 'ArrowDown') { e.preventDefault(); moveActive(1); return; }
      if (e.key === 'ArrowUp')   { e.preventDefault(); moveActive(-1); return; }
      if (e.key === 'Escape')    { closeDropdown(); return; }
      if (e.key === 'Enter') {
        e.preventDefault();
        const focused = langDropdown?.querySelector('.ep-opt-focused');
        if (focused) { focused.click(); return; }
        const q = langSearch.value.trim();
        if (!q) return;
        const exact = LANG_OPTIONS.find(l => l.toLowerCase() === q.toLowerCase());
        addLang(exact || q);
        langSearch.value = '';
        renderDropdown('');
        return;
      }
      if (e.key === 'Backspace' && !langSearch.value) {
        const chips = $$('.ep-lang-chip', langBox);
        chips[chips.length - 1]?.remove();
        syncHidden();
      }
    });
  }

  document.addEventListener('click', e => {
    const wrap = langBox?.parentElement;
    if (wrap && !wrap.contains(e.target)) closeDropdown();
  });

  // Wire server-side rendered chips
  $$('.ep-lang-chip', langBox).forEach(chip => {
    chip.querySelector('.ep-lang-chip-remove')?.addEventListener('click', () => {
      chip.remove();
      syncHidden();
    });
  });

  /* ============================================================
     11. PROFILE COMPLETION CALCULATOR
  ============================================================ */
  const completionFill = $('completionFill');
  const completionPctNum = $('completionPctNum');

  // Step weights — must sum to 100
  const STEPS = {
    picture:   15,
    bio:       20,
    gender:    10,
    dob:       15,
    location:  20,
    languages: 20,
  };

  const stepState = {
    picture:   !!(avatarImg && !avatarImg.hidden),
    bio:       !!(bioEl?.value?.trim()),
    gender:    !!(genderEl?.value),
    dob:       !!(dobEl?.value),
    location:  !!(locationEl?.value?.trim()),
    languages: getSelected().length > 0,
  };

  // Checklist items that reflect these steps, matched by id
  const CHECKLIST_MAP = {
    picture:   'clPicture',
    bio:       'clBio',
    gender:    'clGender',
    dob:       'clDob',
    location:  'clLocation',
    languages: 'clLanguages',
  };

  function markStep(key, done) {
    stepState[key] = done;
    const itemId = CHECKLIST_MAP[key];
    if (!itemId) return;
    $(itemId)?.classList.toggle('cl-done', done);
  }

  const completionMissing = $('completionMissing');

  function refreshMissingHint() {
    if (!completionMissing) return;
    const missingLabels = $$('.cl-item:not(.cl-done) .cl-label').map(el => el.textContent.trim());
    if (!missingLabels.length) {
      completionMissing.innerHTML = '<strong>Your profile is fully complete.</strong>';
      return;
    }
    completionMissing.innerHTML = `<strong>Still missing:</strong> ${missingLabels.join(', ')}`;
  }

  function recalcCompletion() {
    const pct = Object.entries(STEPS).reduce((sum, [key, weight]) => {
      return sum + (stepState[key] ? weight : 0);
    }, 0);

    if (completionFill) completionFill.style.width = `${pct}%`;
    if (completionPctNum) completionPctNum.textContent = `${pct}`;
    refreshMissingHint();
  }

  // Jump-to-field links inside the checklist (picture / bio / gender / dob / location / languages)
  $$('.cl-link[data-jump]').forEach(link => {
    link.addEventListener('click', e => {
      e.preventDefault();
      const target = $(link.dataset.jump);
      if (!target) return;
      target.scrollIntoView({ behavior: 'smooth', block: 'center' });
      setTimeout(() => target.focus?.(), 300);
    });
  });

  // Init from current field/markup values
  Object.keys(stepState).forEach(k => markStep(k, stepState[k]));
  recalcCompletion();
  syncHidden();

  /* ============================================================
     12. TOAST
  ============================================================ */
  const toast      = $('toast');
  const toastMsg   = $('toastMsg');
  const toastClose = $('toastClose');

  function showToast(message, isError = false) {
    if (!toast) return;
    if (toastMsg && message) toastMsg.textContent = message;
    const icon = toast.querySelector('.ep-toast-icon');
    if (icon) icon.classList.toggle('ep-toast-success', !isError);
    toast.classList.add('ep-toast-visible');
    setTimeout(() => hideToast(), 4000);
  }

  function hideToast() {
    toast?.classList.remove('ep-toast-visible');
  }

  if (toastClose) toastClose.addEventListener('click', hideToast);

  // Auto-show after successful server redirect (flash attr: success=true)
  if (toast?.getAttribute('data-autosuccess') === 'true') {
    showToast('Profile updated successfully.');
  }

  /* ============================================================
     13. FORM SUBMIT — validate → spinner → submit
  ============================================================ */
  const form = $('editProfileForm');

  function allSaveBtns() {
    return $$('[type="submit"][form="editProfileForm"]');
  }

  function setLoadingAll(loading) {
    allSaveBtns().forEach(btn => btn.classList.toggle('is-loading', loading));
  }

  if (form) {
    form.addEventListener('submit', e => {
      const okBio = validateBio();
      const okDob = validateDob();
      const okLoc = validateLocation();

      if (!okBio || !okDob || !okLoc) {
        e.preventDefault();
        const first = form.querySelector('.ep-has-error .ep-input, .ep-has-error .ep-textarea, .ep-has-error .ep-select');
        first?.focus();
        first?.scrollIntoView({ behavior: 'smooth', block: 'center' });
        return;
      }

      syncHidden();
      setLoadingAll(true);
      // real POST — Spring Boot handles it and redirects back with ?success=true
    });
  }

  /* ============================================================
     14. CANCEL — confirm if dirty
  ============================================================ */
  $$('.ep-cancel-link, #cancelBtnTop').forEach(btn => {
    btn.addEventListener('click', e => {
      const dirty = (bioEl?.value?.length || 0) > 0 || (locationEl?.value?.length || 0) > 0;
      if (dirty && !window.confirm('Discard your unsaved changes?')) {
        e.preventDefault();
      }
    });
  });

  /* ============================================================
     15. FLOATING SAVE — show/hide based on scroll (mobile only)
  ============================================================ */
  const floatSave = $('floatingSave');
  if (floatSave) {
    const sidebarSave = $('sidebarSaveBtn');
    if ('IntersectionObserver' in window && sidebarSave) {
      const obs = new IntersectionObserver(([entry]) => {
        floatSave.style.opacity        = entry.isIntersecting ? '0' : '1';
        floatSave.style.pointerEvents  = entry.isIntersecting ? 'none' : 'auto';
      }, { threshold: 0.5 });
      obs.observe(sidebarSave);
    } else {
      floatSave.style.opacity = '1';
      floatSave.style.pointerEvents = 'auto';
    }
  }

})();