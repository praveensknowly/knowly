document.addEventListener('DOMContentLoaded', () => {
  const certModal = document.getElementById('certModal');
  const deleteModal = document.getElementById('deleteModal');
  const certForm = document.getElementById('certForm');
  const deleteCertForm = document.getElementById('deleteCertForm');
  const certificationsGrid = document.getElementById('certificationsGrid');
  const emptyState = document.getElementById('emptyState');
  const modalTitle = document.getElementById('modalTitle');
  const certIdInput = document.getElementById('certId');
  const nameInput = document.getElementById('name');
  const issuerInput = document.getElementById('issuer');
  const yearInput = document.getElementById('year');
  const credentialUrlInput = document.getElementById('credentialUrl');
  const deleteCertIdInput = document.getElementById('deleteCertId');

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

  // Modal controls
  const addCertBtn = document.getElementById('addCertBtn');
  const emptyAddBtn = document.getElementById('emptyAddBtn');
  const closeModalBtn = document.getElementById('closeModal');
  const cancelBtn = document.getElementById('cancelBtn');
  const closeDeleteModalBtn = document.getElementById('closeDeleteModal');
  const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');

  // Search and sort
  const certSearch = document.getElementById('certSearch');
  const certSort = document.getElementById('certSort');

  function openModal(isEdit = false, certData = null) {
    certModal.classList.add('open');
    if (isEdit && certData) {
      modalTitle.textContent = 'Edit Certification';
      certIdInput.value = certData.id;
      nameInput.value = certData.name;
      issuerInput.value = certData.issuer;
      yearInput.value = certData.year;
      credentialUrlInput.value = certData.credentialUrl || '';
    } else {
      modalTitle.textContent = 'Add Certification';
      certForm.reset();
      certIdInput.value = '';
    }
  }

  function closeModal() {
    certModal.classList.remove('open');
  }

  function openDeleteModal(certId) {
    deleteCertIdInput.value = certId;
    deleteModal.classList.add('open');
  }

  function closeDeleteModal() {
    deleteModal.classList.remove('open');
  }

  // Event listeners
  addCertBtn?.addEventListener('click', () => openModal(false));
  emptyAddBtn?.addEventListener('click', () => openModal(false));
  closeModalBtn?.addEventListener('click', closeModal);
  cancelBtn?.addEventListener('click', closeModal);
  closeDeleteModalBtn?.addEventListener('click', closeDeleteModal);
  cancelDeleteBtn?.addEventListener('click', closeDeleteModal);

  // Close modals on outside click
  certModal?.addEventListener('click', (e) => {
    if (e.target === certModal) closeModal();
  });
  deleteModal?.addEventListener('click', (e) => {
    if (e.target === deleteModal) closeDeleteModal();
  });

  // Edit and delete buttons
  certificationsGrid?.addEventListener('click', (e) => {
    const card = e.target.closest('.project-card');
    if (!card) return;

    const certData = {
      id: card.dataset.id,
      name: card.dataset.name,
      issuer: card.dataset.issuer,
      year: card.dataset.year,
      credentialUrl: card.dataset.credentialUrl || ''
    };

    if (e.target.classList.contains('edit-btn') || e.target.closest('.edit-btn')) {
      e.preventDefault();
      openModal(true, certData);
    } else if (e.target.classList.contains('delete-btn') || e.target.closest('.delete-btn')) {
      e.preventDefault();
      openDeleteModal(certData.id);
    }
  });

  // Search functionality
  certSearch?.addEventListener('input', (e) => {
    const query = e.target.value.toLowerCase();
    const cards = certificationsGrid.querySelectorAll('.project-card');
    let visibleCount = 0;

    cards.forEach(card => {
      const name = card.dataset.name?.toLowerCase() || '';
      const issuer = card.dataset.issuer?.toLowerCase() || '';
      const matches = name.includes(query) || issuer.includes(query);
      card.style.display = matches ? 'block' : 'none';
      if (matches) visibleCount++;
    });

    if (emptyState) {
      emptyState.style.display = visibleCount === 0 && cards.length > 0 ? 'block' : 'none';
    }
  });

  // Sort functionality
  certSort?.addEventListener('change', (e) => {
    const sortBy = e.target.value;
    const cards = Array.from(certificationsGrid.querySelectorAll('.project-card'));

    cards.sort((a, b) => {
      switch (sortBy) {
        case 'newest':
          return (b.dataset.id || '').localeCompare(a.dataset.id || '');
        case 'oldest':
          return (a.dataset.id || '').localeCompare(b.dataset.id || '');
        case 'name':
          return (a.dataset.name || '').localeCompare(b.dataset.name || '');
        default:
          return 0;
      }
    });

    cards.forEach(card => certificationsGrid.appendChild(card));
  });

  // Form validation
  certForm?.addEventListener('submit', (e) => {
    const year = parseInt(yearInput.value);
    const currentYear = new Date().getFullYear();
    
    if (year < 1950 || year > currentYear + 1) {
      e.preventDefault();
      if (window.Toast) {
        window.Toast.show('Please enter a valid year between 1950 and ' + (currentYear + 1), 'error');
      } else {
        alert('Please enter a valid year between 1950 and ' + (currentYear + 1));
      }
      return;
    }
  });
});
