/* ================================================================
   Knowly — Helping.js
   Modules:
     1. Navbar toggle
     2. Tab switching (via shared TabSwitcher.js)
     3. Card click interaction
================================================================ */

/* ── Navbar toggle ── */
(function () {
  const toggle = document.getElementById('hNavToggle');
  const mobile = document.getElementById('hNavMobile');
  if (!toggle || !mobile) return;

  toggle.addEventListener('click', () => {
    const open = mobile.classList.toggle('open');
    toggle.classList.toggle('open', open);
    toggle.setAttribute('aria-expanded', String(open));
  });

  document.addEventListener('click', e => {
    if (!document.getElementById('navbar').contains(e.target)) {
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

/* ── Tab switching ── */
(function () {
  'use strict';

  TabSwitcher({
    tabsSelector: '#helpingTabs [role="tab"]',
    itemSelector: '.helping-item',
    emptyTabId: 'helpingEmptyTab',
    emptyAllId: 'helpingEmptyAll',
    listId: 'helpingList',
    countId: 'helpingCount',
    countLabelId: 'helpingCountLabel',
    emptyTitleId: 'helpingEmptyTitle',
    emptyTextId: 'helpingEmptyText',
    subtitles: {
      pending: {
        label: ' people are waiting on your knowledge.',
        emptyTitle: 'No pending requests',
        emptyText: 'You are all caught up. New requests will appear here.'
      },
      active: {
        label: ' active conversations need your attention.',
        emptyTitle: 'No active sessions',
        emptyText: 'Accept a pending request to start helping someone.'
      },
      solved: {
        label: ' solved sessions in your history.',
        emptyTitle: 'No solved sessions yet',
        emptyText: 'Completed help sessions will be listed here.'
      }
    }
  });

  setupCardClick('.request-card');
})();
