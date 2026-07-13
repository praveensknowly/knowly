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

  const state = { editingId: null };
  const addProjectBtn = document.getElementById('addProjectBtn');
  const emptyAddBtn = document.getElementById('emptyAddProjectBtn');
  const projectModal = document.getElementById('projectModal');
  const closeModalBtn = document.getElementById('closeModal');
  const cancelProjectBtn = document.getElementById('cancelProject');
  const projectForm = document.getElementById('projectForm');
  const deleteProjectForm = document.getElementById('deleteProjectForm');
  const deleteProjectId = document.getElementById('deleteProjectId');
  const projectGrid = document.getElementById('projectGrid');
  const projectSearch = document.getElementById('projectSearch');
  const projectSort = document.getElementById('projectSort');
  const modalTitle = document.getElementById('modalTitle');
  const projectIdInput = document.getElementById('projectId');
  const projectTitle = document.getElementById('projectTitle');
  const projectRole = document.getElementById('projectRole');
  const projectTech = document.getElementById('projectTech');
  const projectLink = document.getElementById('projectLink');
  const projectDescription = document.getElementById('projectDescription');
  const toastRoot = document.getElementById('toastRoot');

  function openModal(editing = false) {
    if (!projectModal) return;
    projectModal.classList.add('is-open');
    modalTitle.textContent = editing ? 'Edit Project' : 'Add Project';
    (editing ? projectDescription : projectTitle)?.focus();
    document.body.style.overflow = 'hidden';
  }

  function closeModal() {
    if (!projectModal) return;
    projectModal.classList.remove('is-open');
    document.body.style.overflow = '';
    resetForm();
  }

  function showToast(message, type = 'success') {
    if (window.Toast) {
      window.Toast.show(message, type);
    } else if (toastRoot) {
      // Fallback if toast.js not loaded
      const toast = document.createElement('div');
      toast.className = 'toast';
      toast.textContent = message;
      toastRoot.appendChild(toast);
      setTimeout(() => toast.remove(), 2800);
    }
  }

  function resetForm() {
    if (!projectForm) return;
    projectForm.reset();
    state.editingId = null;
    if (projectIdInput) projectIdInput.value = '';
    if (modalTitle) modalTitle.textContent = 'Add Project';
    projectTitle?.classList.remove('invalid');
    projectRole?.classList.remove('invalid');
  }

  function populateForm(card) {
    if (!card || !projectIdInput) return;
    state.editingId = card.dataset.id;
    projectIdInput.value = state.editingId;
    projectTitle.value = card.dataset.title || '';
    projectRole.value = card.dataset.role || '';
    projectTech.value = card.dataset.tech || '';
    projectLink.value = card.dataset.link || '';
    projectDescription.value = card.dataset.description || '';
    modalTitle.textContent = 'Edit Project';
  }

  function validateForm() {
    let valid = true;
    if (!projectTitle?.value.trim()) {
      projectTitle?.classList.add('invalid');
      valid = false;
    } else {
      projectTitle?.classList.remove('invalid');
    }
    if (!projectRole?.value.trim()) {
      projectRole?.classList.add('invalid');
      valid = false;
    } else {
      projectRole?.classList.remove('invalid');
    }
    return valid;
  }

  function onFormSubmit(event) {
    if (!validateForm()) {
      event.preventDefault();
      showToast('Please enter a project title and role before saving.');
    }
  }

  function onGridClick(event) {
    const button = event.target.closest('button');
    if (!button) return;
    const card = button.closest('.project-card');
    if (!card) return;

    if (button.classList.contains('edit-btn')) {
      populateForm(card);
      openModal(true);
    }

    if (button.classList.contains('delete-btn')) {
      event.preventDefault();
      const projectName = card.dataset.title || 'this project';
      if (confirm(`Are you sure you want to delete "${projectName}"? This action cannot be undone.`)) {
        if (deleteProjectForm && deleteProjectId) {
          deleteProjectId.value = card.dataset.id;
          deleteProjectForm.submit();
        }
      }
    }
  }

  function getCardCreatedDate(card) {
    if (!card) return 0;
    const value = card.dataset.created;
    if (value) {
      const timestamp = Date.parse(value);
      return Number.isNaN(timestamp) ? 0 : timestamp;
    }
    return 0;
  }

  function applySearch() {
    if (!projectGrid || !projectSearch) return;
    const query = projectSearch.value.trim().toLowerCase();
    const cards = Array.from(projectGrid.querySelectorAll('.project-card'));
    cards.forEach((card) => {
      const title = (card.dataset.title || '').toLowerCase();
      const role = (card.dataset.role || '').toLowerCase();
      const visible = query === '' || title.includes(query) || role.includes(query);
      card.hidden = !visible;
    });
  }

  function sortCards(mode) {
    if (!projectGrid) return;
    const cards = Array.from(projectGrid.querySelectorAll('.project-card'));
    cards.sort((a, b) => {
      switch (mode) {
        case 'oldest':
          return getCardCreatedDate(a) - getCardCreatedDate(b);
        case 'title':
          return (a.dataset.title || '').localeCompare(b.dataset.title || '');
        case 'newest':
        default:
          return getCardCreatedDate(b) - getCardCreatedDate(a);
      }
    });
    cards.forEach((card) => projectGrid.appendChild(card));
  }

  function init() {
    if (addProjectBtn) addProjectBtn.addEventListener('click', () => { resetForm(); openModal(); });
    if (emptyAddBtn) emptyAddBtn.addEventListener('click', () => { resetForm(); openModal(); });
    if (closeModalBtn) closeModalBtn.addEventListener('click', closeModal);
    if (cancelProjectBtn) cancelProjectBtn.addEventListener('click', closeModal);
    if (projectModal) {
      projectModal.addEventListener('click', (event) => {
        if (event.target === projectModal) closeModal();
      });
    }
    if (projectForm) projectForm.addEventListener('submit', onFormSubmit);
    if (projectGrid) projectGrid.addEventListener('click', onGridClick);
    if (projectSearch) projectSearch.addEventListener('input', applySearch);
    if (projectSort) projectSort.addEventListener('change', () => sortCards(projectSort.value));
  }

  document.addEventListener('DOMContentLoaded', init);
})();
