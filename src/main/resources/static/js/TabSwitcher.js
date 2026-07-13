/* ================================================================
   Knowly — TabSwitcher.js
   Shared module for tab switching (Pending / Active / Solved)
   Used by Helping.js and Learning.js
================================================================ */

(function (window) {
  'use strict';

  function $$(selector, context) {
    return Array.from((context || document).querySelectorAll(selector));
  }

  window.TabSwitcher = function (config) {
    var tabs = $$(config.tabsSelector);
    var items = $$(config.itemSelector);
    var emptyTab = document.getElementById(config.emptyTabId);
    var emptyAll = document.getElementById(config.emptyAllId);
    var list = document.getElementById(config.listId);
    var count = document.getElementById(config.countId);
    var countLabel = document.getElementById(config.countLabelId);
    var emptyTitle = document.getElementById(config.emptyTitleId);
    var emptyText = document.getElementById(config.emptyTextId);

    var subtitles = config.subtitles;

    function setActiveTab(tab) {
      tabs.forEach(function (button) {
        var isActive = button.getAttribute('data-tab') === tab;
        button.classList.toggle('active', isActive);
        button.setAttribute('aria-selected', isActive ? 'true' : 'false');
      });

      var visibleCount = 0;
      items.forEach(function (item) {
        var show = item.getAttribute('data-tab') === tab;
        item.hidden = !show;
        if (show) {
          visibleCount += 1;
        }
      });

      if (count) {
        count.textContent = String(visibleCount);
      }
      if (countLabel && subtitles[tab]) {
        countLabel.textContent = subtitles[tab].label;
      }

      if (emptyAll) {
        emptyAll.hidden = true;
      }

      if (emptyTab) {
        var showEmpty = items.length > 0 && visibleCount === 0;
        emptyTab.hidden = !showEmpty;
        if (showEmpty && subtitles[tab]) {
          emptyTitle.textContent = subtitles[tab].emptyTitle;
          emptyText.textContent = subtitles[tab].emptyText;
        }
      }

      if (list) {
        list.hidden = items.length > 0 && visibleCount === 0;
      }
    }

    tabs.forEach(function (button) {
      button.addEventListener('click', function () {
        setActiveTab(button.getAttribute('data-tab'));
      });

      button.addEventListener('keydown', function (e) {
        if (e.key !== 'ArrowLeft' && e.key !== 'ArrowRight') {
          return;
        }
        e.preventDefault();
        var index = tabs.indexOf(button);
        var nextIndex = e.key === 'ArrowRight'
          ? (index + 1) % tabs.length
          : (index - 1 + tabs.length) % tabs.length;
        tabs[nextIndex].focus();
        setActiveTab(tabs[nextIndex].getAttribute('data-tab'));
      });
    });

    if (items.length > 0) {
      setActiveTab('pending');
    }

    return {
      setActiveTab: setActiveTab
    };
  };

  /* Card click navigation to chat page */
  window.setupCardClick = function (cardSelector) {
    $$(cardSelector).forEach(function (card) {
      card.addEventListener('click', function (e) {
        if (e.target.closest('.rc-actions')) return; // don't navigate when clicking Edit/Delete
        var sessionId = card.getAttribute('data-session-id');
        if (sessionId) {
          window.location.href = '/chat/' + sessionId;
        }
      });
    });
  };

})(window);
