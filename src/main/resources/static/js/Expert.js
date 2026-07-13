
  /* ── Navbar toggle ── */
  (function () {
    const toggle = document.getElementById('expertNavToggle');
    const mobile = document.getElementById('expertNavMobile');
    if (!toggle || !mobile) return;

    toggle.addEventListener('click', () => {
      const open = mobile.classList.toggle('open');
      toggle.classList.toggle('open', open);
      toggle.setAttribute('aria-expanded', String(open));
    });

    document.addEventListener('click', e => {
      if (!document.getElementById('expertNav').contains(e.target)) {
        mobile.classList.remove('open');
        toggle.classList.remove('open');
        toggle.setAttribute('aria-expanded', 'false');
      }
    });

    window.addEventListener('resize', () => {
      if (window.innerWidth > 960) {
        mobile.classList.remove('open');
        toggle.classList.remove('open');
        toggle.setAttribute('aria-expanded', 'false');
      }
    });
  })();

  /* ── Skill icon gradient (deterministic by name) ── */
  (function () {
    const PALETTE = [
      ['#2563EB','#7C3AED'],['#0D9488','#2563EB'],['#7C3AED','#DB2777'],
      ['#D97706','#DC2626'],['#059669','#0891B2'],['#2563EB','#0D9488'],
      ['#9333EA','#2563EB'],['#DC2626','#D97706'],['#0891B2','#10B981'],
      ['#4F46E5','#7C3AED'],
    ];
    function hash(s) {
      let h = 0;
      for (let i = 0; i < s.length; i++) { h = (h << 5) - h + s.charCodeAt(i); h |= 0; }
      return Math.abs(h);
    }
    document.querySelectorAll('.skill-card').forEach(card => {
      const icon = card.querySelector('.skill-icon');
      const name = card.querySelector('.skill-name');
      if (!icon || !name) return;
      const [c1, c2] = PALETTE[hash(name.textContent.trim().toLowerCase()) % PALETTE.length];
      icon.style.background = `linear-gradient(135deg,${c1},${c2})`;
      icon.style.boxShadow  = `0 4px 12px ${c1}44`;
    });
  })();

  /* ── Star rendering ── */
  (function () {
    document.querySelectorAll('.skill-stars[data-score]').forEach(el => {
      const score = Math.min(Math.max(parseFloat(el.dataset.score) || 0, 0), 5);
      let html = '';
      for (let i = 1; i <= 5; i++) {
        const ratio = Math.min(1, Math.max(0, score - (i - 1)));
        const pct   = Math.round(ratio * 100);
        const id    = 'sg-' + Math.random().toString(36).slice(2);
        html += `<svg width="14" height="14" viewBox="0 0 24 24" fill="none"
          xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
          <defs>
            <linearGradient id="${id}" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="${pct}%" stop-color="#F59E0B"/>
              <stop offset="${pct}%" stop-color="#E5E7EB"/>
            </linearGradient>
          </defs>
          <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"
                fill="url(#${id})" stroke="#F59E0B" stroke-width="0.5" stroke-linejoin="round"/>
        </svg>`;
      }
      el.innerHTML = html;
    });
  })();

  /* ── Avatar fallback ── */
  (function () {
    const img = document.getElementById('expertAvatarImg');
    if (!img) return;
    img.addEventListener('error', () => {
      img.style.display = 'none';
      const letter = document.querySelector('.hero-avatar-letter');
      if (letter) letter.style.display = 'flex';
    });
  })();
 
