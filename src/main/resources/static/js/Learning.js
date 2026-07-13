/* ================================================================
   Knowly — Learning.js
   Modules:
     1. Navbar toggle
     2. Tab switching (via shared TabSwitcher.js)
     3. Card click interaction
================================================================ */

/* ── Navbar toggle ── */
(function () {
  const toggle = document.getElementById('lNavToggle');
  const mobile = document.getElementById('lNavMobile');
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
    tabsSelector: '#learningTabs [role="tab"]',
    itemSelector: '.learning-item',
    emptyTabId: 'learningEmptyTab',
    emptyAllId: 'learningEmptyAll',
    listId: 'learningList',
    countId: 'learningCount',
    countLabelId: 'learningCountLabel',
    emptyTitleId: 'learningEmptyTitle',
    emptyTextId: 'learningEmptyText',
    subtitles: {
      pending: {
        label: ' requests you\'ve made are waiting for a response.',
        emptyTitle: 'No pending requests',
        emptyText: 'Your requests are waiting for experts to respond.'
      },
      active: {
        label: ' active conversations are in progress.',
        emptyTitle: 'No active sessions',
        emptyText: 'Once an expert responds, your active sessions will appear here.'
      },
      solved: {
        label: ' completed sessions in your history.',
        emptyTitle: 'No completed sessions yet',
        emptyText: 'Finished learning sessions will be listed here.'
      }
    }
  });

  setupCardClick('.request-card');
})();
