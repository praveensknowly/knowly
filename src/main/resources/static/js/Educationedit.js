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
  const addEducationBtn = document.getElementById('addEducationBtn');
  const emptyAddBtn = document.getElementById('emptyAddEducationBtn');
  const educationModal = document.getElementById('educationModal');
  const closeModalBtn = document.getElementById('closeModal');
  const cancelEducationBtn = document.getElementById('cancelEducation');
  const educationForm = document.getElementById('educationForm');
  const deleteEducationForm = document.getElementById('deleteEducationForm');
  const deleteEducationId = document.getElementById('deleteEducationId');
  const educationGrid = document.getElementById('educationGrid');
  const educationSearch = document.getElementById('eduSearch');
  const educationSort = document.getElementById('eduSort');
  const modalTitle = document.getElementById('modalTitle');
  const educationIdInput = document.getElementById('educationId');
  const institutionInput = document.getElementById('institution');
  const degreeInput = document.getElementById('degree');
  const fieldOfStudyInput = document.getElementById('fieldOfStudy');
  const startYearInput = document.getElementById('startYear');
  const endYearInput = document.getElementById('endYear');
  const descriptionInput = document.getElementById('description');

  function openModal(editing = false) {
    if (!educationModal) return;
    educationModal.classList.add('is-open');
    modalTitle.textContent = editing ? 'Edit Education' : 'Add Education';
    (editing ? degreeInput : institutionInput)?.focus();
    document.body.style.overflow = 'hidden';
  }

  function closeModal() {
    if (!educationModal) return;
    educationModal.classList.remove('is-open');
    document.body.style.overflow = '';
    resetForm();
  }

  function resetForm() {
    if (!educationForm) return;
    educationForm.reset();
    state.editingId = null;
    if (educationIdInput) educationIdInput.value = '';
    if (modalTitle) modalTitle.textContent = 'Add Education';
  }

  function populateForm(card) {
    if (!card || !educationIdInput) return;
    state.editingId = card.dataset.id;
    educationIdInput.value = state.editingId;
    institutionInput.value = card.dataset.institution || '';
    degreeInput.value = card.dataset.degree || '';
    fieldOfStudyInput.value = card.dataset.fieldOfStudy || '';
    startYearInput.value = card.dataset.startYear || '';
    endYearInput.value = card.dataset.endYear || '';
    descriptionInput.value = card.dataset.description || '';
    modalTitle.textContent = 'Edit Education';
  }

  function validateForm() {
    let valid = true;
    if (!institutionInput?.value.trim()) {
      institutionInput.classList.add('invalid');
      valid = false;
    } else {
      institutionInput.classList.remove('invalid');
    }
    if (!degreeInput?.value.trim()) {
      degreeInput.classList.add('invalid');
      valid = false;
    } else {
      degreeInput.classList.remove('invalid');
    }
    return valid;
  }

  function onFormSubmit(event) {
    if (!validateForm()) {
      event.preventDefault();
      if (window.Toast) {
        window.Toast.show('Please fill in all required fields.', 'error');
      }
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
      const institution = card.dataset.institution || 'this education';
      if (confirm(`Are you sure you want to delete "${institution}"? This action cannot be undone.`)) {
        if (deleteEducationForm && deleteEducationId) {
          deleteEducationId.value = card.dataset.id;
          deleteEducationForm.submit();
        }
      }
    }
  }

  function applySearch() {
    if (!educationGrid || !educationSearch) return;
    const query = educationSearch.value.trim().toLowerCase();
    const cards = Array.from(educationGrid.querySelectorAll('.project-card'));
    cards.forEach((card) => {
      const institution = (card.dataset.institution || '').toLowerCase();
      const degree = (card.dataset.degree || '').toLowerCase();
      const field = (card.dataset.fieldOfStudy || '').toLowerCase();
      const visible = query === '' || institution.includes(query) || degree.includes(query) || field.includes(query);
      card.hidden = !visible;
    });
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

  function sortCards(mode) {
    if (!educationGrid) return;
    const cards = Array.from(educationGrid.querySelectorAll('.project-card'));
    cards.sort((a, b) => {
      switch (mode) {
        case 'oldest':
          return getCardCreatedDate(a) - getCardCreatedDate(b);
        case 'school':
          return (a.dataset.institution || '').localeCompare(b.dataset.institution || '');
        case 'newest':
        default:
          return getCardCreatedDate(b) - getCardCreatedDate(a);
      }
    });
    cards.forEach((card) => educationGrid.appendChild(card));
  }

  function init() {
    if (addEducationBtn) addEducationBtn.addEventListener('click', () => { resetForm(); openModal(); });
    if (emptyAddBtn) emptyAddBtn.addEventListener('click', () => { resetForm(); openModal(); });
    if (closeModalBtn) closeModalBtn.addEventListener('click', closeModal);
    if (cancelEducationBtn) cancelEducationBtn.addEventListener('click', closeModal);
    if (educationModal) {
      educationModal.addEventListener('click', (event) => {
        if (event.target === educationModal) closeModal();
      });
    }
    if (educationForm) educationForm.addEventListener('submit', onFormSubmit);
    if (educationGrid) educationGrid.addEventListener('click', onGridClick);
    if (educationSearch) educationSearch.addEventListener('input', applySearch);
    if (educationSort) educationSort.addEventListener('change', () => sortCards(educationSort.value));
  }

  document.addEventListener('DOMContentLoaded', init);
})();