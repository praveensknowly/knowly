(function () {
  'use strict';

  // Navbar mobile toggle
  const navToggle = document.getElementById('navToggle');
  const navMobile = document.getElementById('navMobile');
  
  if (navToggle && navMobile) {
    navToggle.addEventListener('click', () => {
      const isOpen = navMobile.classList.toggle('open');
      navToggle.classList.toggle('open', isOpen);
      navToggle.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
    });

    navMobile.querySelectorAll('a').forEach(link => {
      link.addEventListener('click', () => {
        navMobile.classList.remove('open');
        navToggle.classList.remove('open');
        navToggle.setAttribute('aria-expanded', 'false');
      });
    });
  }

  const ModalUI = (function () {
    const addModal = document.getElementById('skillModal');
    const deleteModal = document.getElementById('deleteModal');
    const form = document.getElementById('skillForm');
    const title = document.getElementById('modalTitle');
    const hiddenId = document.getElementById('skillId');
    const nameInput = document.getElementById('name');
    const yearsInput = document.getElementById('yearsOfExperience');
    const proficiencyInput = document.getElementById('proficiencyLevel');
    const addButton = document.getElementById('addSkillBtn');
    const emptyAddButton = document.getElementById('emptyAddSkillBtn');
    const closeButton = document.getElementById('closeModal');
    const cancelSkill = document.getElementById('cancelSkill');
    const deleteIdInput = document.getElementById('deleteSkillId');
    const cancelDelete = document.getElementById('cancelDelete');
    const searchInput = document.getElementById('skillSearch');
    const sortSelect = document.getElementById('skillSort');
    const grid = document.getElementById('skillGrid');
    const noResultsState = document.getElementById('noResultsState');

    let lastFocused = null;

    function clearValidation() {
      if (!form) return;
      form.querySelectorAll('.field').forEach((field) => field.classList.remove('is-invalid'));
    }

    function resetForm() {
      if (!form) return;
      form.reset();
      clearValidation();
      if (hiddenId) hiddenId.value = '';
    }

    function openAddModal() {
      if (!addModal) return;
      resetForm();
      if (title) title.textContent = 'Add Skill';
      lastFocused = document.activeElement;
      addModal.classList.add('is-open');
      if (nameInput) nameInput.focus();
      document.addEventListener('keydown', onKeydown);
    }

    function openEditModal(card) {
      if (!addModal || !card) return;
      resetForm();
      if (title) title.textContent = 'Edit Skill';
      if (hiddenId) hiddenId.value = card.getAttribute('data-id') || '';
      if (nameInput) nameInput.value = card.getAttribute('data-name') || '';
      if (yearsInput) yearsInput.value = card.getAttribute('data-years') || '';
      if (proficiencyInput) proficiencyInput.value = (card.getAttribute('data-level') || '').toUpperCase();
      clearValidation();
      lastFocused = document.activeElement;
      addModal.classList.add('is-open');
      if (nameInput) nameInput.focus();
      document.addEventListener('keydown', onKeydown);
    }

    function openDeleteModal(card) {
      if (!deleteModal || !card) return;
      if (deleteIdInput) deleteIdInput.value = card.getAttribute('data-id') || '';
      lastFocused = document.activeElement;
      deleteModal.classList.add('is-open');
      document.addEventListener('keydown', onKeydown);
    }

    function closeModal() {
      addModal?.classList.remove('is-open');
      deleteModal?.classList.remove('is-open');
      document.removeEventListener('keydown', onKeydown);
      if (lastFocused && typeof lastFocused.focus === 'function') {
        lastFocused.focus();
      }
    }

    function onKeydown(event) {
      if (event.key === 'Escape') {
        closeModal();
      }
    }

    function setInvalid(input, invalid) {
      const field = input.closest('.field');
      if (!field) return;
      field.classList.toggle('is-invalid', invalid);
    }

    function validateForm() {
      let valid = true;
      if (nameInput) {
        const invalid = nameInput.value.trim().length === 0;
        setInvalid(nameInput, invalid);
        valid = valid && !invalid;
      }
      if (yearsInput) {
        const raw = yearsInput.value.trim();
        const num = Number(raw);
        const invalid = raw === '' || Number.isNaN(num) || num < 0 || num > 60;
        setInvalid(yearsInput, invalid);
        valid = valid && !invalid;
      }
      if (proficiencyInput) {
        const invalid = !proficiencyInput.value;
        setInvalid(proficiencyInput, invalid);
        valid = valid && !invalid;
      }
      return valid;
    }

    function applySearch() {
      if (!grid || !searchInput) return;
      const query = searchInput.value.trim().toLowerCase();
      const cards = Array.from(grid.querySelectorAll('.skill-card'));
      let visibleCount = 0;
      cards.forEach((card) => {
        const name = (card.getAttribute('data-name') || '').toLowerCase();
        const matches = query === '' || name.includes(query);
        card.hidden = !matches;
        if (matches) visibleCount += 1;
      });
      if (noResultsState) {
        noResultsState.hidden = !(query !== '' && visibleCount === 0 && cards.length > 0);
      }
    }

    function getCardCreatedDate(card) {
      const explicit = card.getAttribute('data-created');
      if (explicit) {
        const parsed = Date.parse(explicit);
        return Number.isNaN(parsed) ? 0 : parsed;
      }

      const createdText = card.querySelector('.skill-info p:last-of-type span');
      if (!createdText) return 0;
      const parsed = Date.parse(createdText.textContent.trim());
      return Number.isNaN(parsed) ? 0 : parsed;
    }

    function sortCards(mode) {
      if (!grid) return;
      const cards = Array.from(grid.querySelectorAll('.skill-card'));
      cards.sort((a, b) => {
        const aYears = Number(a.getAttribute('data-years') || 0);
        const bYears = Number(b.getAttribute('data-years') || 0);
        const aScore = Number(a.getAttribute('data-score') || 0);
        const bScore = Number(b.getAttribute('data-score') || 0);
        const aCreated = getCardCreatedDate(a);
        const bCreated = getCardCreatedDate(b);
        switch (mode) {
          case 'oldest':
            return aCreated - bCreated;
          case 'experience':
            return bYears - aYears;
          case 'score':
            return bScore - aScore;
          case 'newest':
          default:
            return bCreated - aCreated;
        }
      });
      cards.forEach((card) => grid.appendChild(card));
    }

    function init() {
      if (addButton) addButton.addEventListener('click', openAddModal);
      if (emptyAddButton) emptyAddButton.addEventListener('click', openAddModal);
      if (closeButton) closeButton.addEventListener('click', closeModal);
      if (cancelSkill) cancelSkill.addEventListener('click', closeModal);
      if (cancelDelete) cancelDelete.addEventListener('click', closeModal);
      if (addModal) addModal.addEventListener('click', (event) => { if (event.target === addModal) closeModal(); });
      if (deleteModal) deleteModal.addEventListener('click', (event) => { if (event.target === deleteModal) closeModal(); });

      if (grid) {
        grid.addEventListener('click', (event) => {
          const target = event.target.closest('button');
          if (!target) return;
          const card = target.closest('.skill-card');
          if (!card) return;
          if (target.classList.contains('edit-btn')) {
            openEditModal(card);
          } else if (target.classList.contains('delete-btn')) {
            openDeleteModal(card);
          }
        });
      }

      if (form) {
        form.addEventListener('submit', (event) => {
          if (!validateForm()) {
            event.preventDefault();
            if (window.Toast) {
              window.Toast.show('Please fix the highlighted fields.', 'error');
            }
          }
        });
        form.addEventListener('input', validateForm);
      }

      if (searchInput) searchInput.addEventListener('input', applySearch);
      if (sortSelect) sortSelect.addEventListener('change', () => sortCards(sortSelect.value));
    }

    return { init };
  })();

  document.addEventListener('DOMContentLoaded', () => {
    ModalUI.init();
  });
})();
