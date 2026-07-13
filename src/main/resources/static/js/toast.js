/**
 * Knowly Toast Module
 * Shared toast notification system for all pages
 */

(function () {
  'use strict';

  const Toast = (function () {
    const root = document.getElementById('toastRoot');
    const icons = {
      success: '<svg viewBox="0 0 24 24" fill="none"><path d="M20 6L9 17L4 12" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"></path></svg>',
      error: '<svg viewBox="0 0 24 24" fill="none"><path d="M12 8V13M12 16H12.01M12 3L2 20H22L12 3Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"></path></svg>'
    };

    function show(message, type) {
      if (!root) return;
      const toast = document.createElement('div');
      const toastType = type === 'error' ? 'error' : 'success';
      toast.className = 'toast toast--' + toastType;
      toast.setAttribute('role', 'status');
      toast.innerHTML = '<span class="toast__dot">' + icons[toastType] + '</span><span class="toast__msg"></span>';
      toast.querySelector('.toast__msg').textContent = message;
      root.appendChild(toast);

      const remove = () => {
        toast.classList.add('is-leaving');
        toast.addEventListener('animationend', () => toast.remove(), { once: true });
      };

      const timer = window.setTimeout(remove, 3200);
      toast.addEventListener('click', () => {
        window.clearTimeout(timer);
        remove();
      });
    }

    return { show };
  })();

  // Export to global scope
  window.Toast = Toast;
})();
