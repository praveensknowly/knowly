/* ================================================================
   Knowly — Chat.js
   Modules:
     1. Navbar toggle
     2. Scroll messages to bottom on load
     3. Session status countdown timer
================================================================ */

/* ── Navbar toggle ── */
(function () {
  const toggle = document.getElementById('chatNavToggle');
  const mobile = document.getElementById('chatNavMobile');
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

/* ── Scroll messages to bottom ── */
(function () {
  const chatMessages = document.getElementById('chatMessages');
  const chatForm = document.querySelector('.chat-form');
  const chatInput = document.querySelector('.chat-input');
  const chatSend = document.querySelector('.chat-send');
  const charCount = document.querySelector('.chat-char-count');
  
  function scrollToBottom() {
    if (chatMessages) {
      chatMessages.scrollTop = chatMessages.scrollHeight;
    }
  }
  
  // Scroll on load
  setTimeout(scrollToBottom, 100);
  
  // Character count and auto-resize
  if (chatInput && charCount) {
    const maxLength = chatInput.getAttribute('maxlength') || 1000;
    
    function updateCharCount() {
      const currentLength = chatInput.value.length;
      charCount.textContent = `${currentLength}/${maxLength}`;
      
      // Update color when approaching limit
      if (currentLength >= maxLength * 0.9) {
        charCount.style.color = '#ef4444';
      } else {
        charCount.style.color = '';
      }
    }
    
    function autoResize() {
      chatInput.style.height = 'auto';
      chatInput.style.height = Math.min(chatInput.scrollHeight, 120) + 'px';
    }
    
    chatInput.addEventListener('input', () => {
      updateCharCount();
      autoResize();
    });

    if (chatInput && chatForm) {
      chatInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
          e.preventDefault();
          if (chatInput.value.trim().length > 0) {
            chatForm.requestSubmit();
          }
        }
      });
    }

    // Initial count
    updateCharCount();
  }

  // Review textarea character count
  (function () {
    const reviewText = document.getElementById('reviewText');
    const reviewCharCount = document.querySelector('.rating-char-count');
    if (!reviewText || !reviewCharCount) return;

    const maxLength = reviewText.getAttribute('maxlength') || 1000;

    function updateReviewCharCount() {
      const currentLength = reviewText.value.length;
      reviewCharCount.textContent = `${currentLength}/${maxLength}`;
      
      // Update color when approaching limit
      if (currentLength >= maxLength * 0.9) {
        reviewCharCount.style.color = '#ef4444';
      } else {
        reviewCharCount.style.color = '';
      }
    }

    reviewText.addEventListener('input', updateReviewCharCount);
    updateReviewCharCount();
  })();

  // Handle form submission with loading state
  if (chatForm) {
    chatForm.addEventListener('submit', (e) => {
      // Disable input and button during submission
     if (chatInput) chatInput.setAttribute('readonly', true);
      if (chatSend) {
        chatSend.disabled = true;
        chatSend.innerHTML = '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10" stroke-opacity="0.25"></circle><path d="M12 2a10 10 0 0 1 10 10" stroke-linecap="round"></path></svg>';
        chatSend.classList.add('chat-send--loading');
      }
      
      // Scroll after a short delay (form will submit normally)
      setTimeout(scrollToBottom, 100);
    });
  }
})();

/* ── Session status countdown timer ── */
(function () {
  const statusBadge = document.querySelector('.chat-status-badge');
  if (!statusBadge) return;

  const status = statusBadge.getAttribute('data-status');
  const expiresAtMs = statusBadge.getAttribute('data-expires-at-ms');
  const expiredReason = statusBadge.getAttribute('data-expired-reason');
  const statusText = statusBadge.querySelector('.chat-status-text');
  const countdownEl = statusBadge.querySelector('.chat-countdown');

  let countdownInterval = null;

  // Handle expired and ignored states
  if (status === 'expired' || status === 'EXPIRED' || status === 'ignored' || status === 'IGNORED') {
    disableChatInput(expiredReason || 'Session has expired');
    return;
  }

  // Start countdown for ACTIVE sessions
  if (status === 'active' || status === 'ACTIVE') {
    if (expiresAtMs && countdownEl) {
      countdownEl.style.display = 'inline';
      updateCountdown(expiresAtMs, countdownEl);
      countdownInterval = setInterval(() => updateCountdown(expiresAtMs, countdownEl), 1000);
    }
  }

  function updateCountdown(expiresAtMsStr, element) {
    // Parse epoch milliseconds (UTC)
    const expiresAt = new Date(parseInt(expiresAtMsStr));
    const now = new Date();
    const diff = expiresAt - now;

    if (diff <= 0) {
      element.textContent = 'Expired';
      if (countdownInterval) {
        clearInterval(countdownInterval);
        countdownInterval = null;
      }
      disableChatInput('Session time completed');
      return;
    }

    const minutes = Math.floor(diff / 60000);
    const seconds = Math.floor((diff % 60000) / 1000);
    element.textContent = `${minutes}:${seconds.toString().padStart(2, '0')}`;
  }
  
  function disableChatInput(reason) {
    const chatInput = document.querySelector('.chat-input');
    const chatSend = document.querySelector('.chat-send');
    const chatForm = document.querySelector('.chat-form');
    
    if (chatInput) {
      chatInput.disabled = true;
      chatInput.placeholder = reason;
    }
	if (chatSend) {
	     chatSend.disabled = true;
	     chatSend.innerHTML = "Closed";
	   }

    if (chatForm) {
      chatForm.addEventListener('submit', (e) => {
        e.preventDefault();
      });
    }
    
    // Show expired banner only once
    if (!document.querySelector('.chat-expired-banner')) {
      showExpiredBanner(reason);
    }
  }
  
  function showExpiredBanner(reason) {
    const chatContainer = document.querySelector('.chat-container');
    if (!chatContainer) return;
    
    const banner = document.createElement('div');
    banner.className = 'chat-expired-banner';
    banner.innerHTML = `
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10"></circle>
        <path d="M12 8v4M12 16h.01" stroke-linecap="round" stroke-linejoin="round"></path>
      </svg>
      <span>This session has expired: ${reason}</span>
    `;
    
    chatContainer.insertBefore(banner, chatContainer.firstChild);
  }
})();

/* ── Rating Modal ── */
(function () {
  const ratingBackdrop = document.getElementById('ratingModalBackdrop');
  if (!ratingBackdrop) return;

  // Check if modal was previously dismissed
  const sessionId = ratingBackdrop.getAttribute('data-session-id');
  const dismissalKey = sessionId ? `ratingDismissed_${sessionId}` : null;
  if (dismissalKey && sessionStorage.getItem(dismissalKey) === '1') {
    ratingBackdrop.remove();
    return;
  }

  const stars = ratingBackdrop.querySelectorAll('.star');
  const starsInput = document.getElementById('starsInput');
  const submitBtn = document.getElementById('ratingSubmitBtn');
  const closeBtn = document.getElementById('ratingModalClose');
  const cancelBtn = document.getElementById('ratingCancelBtn');
  const ratingLabel = document.getElementById('ratingLabel');
  const starRating = ratingBackdrop.querySelector('.star-rating');

  const ratingLabels = {
    1: 'Poor',
    2: 'Fair',
    3: 'Good',
    4: 'Very Good',
    5: 'Excellent'
  };

  // Star hover preview
  stars.forEach(star => {
    star.addEventListener('mouseenter', () => {
      const value = parseInt(star.getAttribute('data-value'));
      stars.forEach(s => {
        const sValue = parseInt(s.getAttribute('data-value'));
        if (sValue <= value) {
          s.classList.add('selected');
        } else {
          s.classList.remove('selected');
        }
      });
      if (ratingLabel) {
        ratingLabel.textContent = ratingLabels[value];
      }
    });
  });

  // Restore actual value on mouseleave
  starRating.addEventListener('mouseleave', () => {
    const currentValue = parseInt(starsInput.value) || 0;
    stars.forEach(s => {
      const sValue = parseInt(s.getAttribute('data-value'));
      if (sValue <= currentValue) {
        s.classList.add('selected');
      } else {
        s.classList.remove('selected');
      }
    });
    if (ratingLabel) {
      ratingLabel.textContent = currentValue > 0 ? ratingLabels[currentValue] : 'Select a rating';
    }
  });

  // Star click handling
  stars.forEach(star => {
    star.addEventListener('click', () => {
      const value = parseInt(star.getAttribute('data-value'));
      starsInput.value = value;

      // Update visual state
      stars.forEach(s => {
        const sValue = parseInt(s.getAttribute('data-value'));
        if (sValue <= value) {
          s.classList.add('selected');
        } else {
          s.classList.remove('selected');
        }
      });

      // Update label
      if (ratingLabel) {
        ratingLabel.textContent = ratingLabels[value];
      }

      // Enable submit button
      submitBtn.disabled = false;
    });
  });

  // Close modal handlers
  function closeModal() {
    if (dismissalKey) {
      sessionStorage.setItem(dismissalKey, '1');
    }
    ratingBackdrop.remove();
  }

  closeBtn.addEventListener('click', closeModal);
  cancelBtn.addEventListener('click', closeModal);

  // Close on backdrop click
  ratingBackdrop.addEventListener('click', (e) => {
    if (e.target === ratingBackdrop) {
      closeModal();
    }
  });

  // Close on Escape key
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && ratingBackdrop.parentNode) {
      closeModal();
    }
  });
})();

/* ── Live message polling ── */
(function () {
  // Extract sessionId from current URL
  const pathParts = window.location.pathname.split('/');
  const sessionId = pathParts[pathParts.length - 1];
  
  if (!sessionId || sessionId === 'chat') return;
  
  let lastMessageCount = document.querySelectorAll('.chat-bubble').length;
  
  async function pollForNewMessages() {
    try {
      const res = await fetch(`/chat/${sessionId}/messages/latest`);
      if (!res.ok) return;
      
      const data = await res.json();
      if (data.count > lastMessageCount) {
        location.reload();
      }
    } catch (e) {
      // Silently fail on network errors
    }
  }
  
  // Poll every 5 seconds
  setInterval(pollForNewMessages, 5000);
})();

/* ── Voice recording ── */
(function () {
  let mediaRecorder, chunks = [], audioCtx, analyser, animationId, stream, recStartTime, timerInterval;

  const voiceRecordBtn = document.getElementById('voiceRecordBtn');
  const recorder = document.getElementById('voiceRecorder');
  const panel = document.getElementById('voiceRecordingPanel');
  const canvas = document.getElementById('voiceWaveCanvas');
  const timerEl = document.getElementById('voiceRecTimer');
  const stopBtn = document.getElementById('voiceStopBtn');
  const cancelBtn = document.getElementById('voiceCancelBtn');
  if (!voiceRecordBtn) return;

  const ctx2d = canvas.getContext('2d');

  function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return match ? decodeURIComponent(match[1]) : null;
  }

  voiceRecordBtn.addEventListener('click', startRecording);
  stopBtn.addEventListener('click', () => stopRecording(true));
  cancelBtn.addEventListener('click', () => stopRecording(false));

  async function startRecording() {
    try {
      stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    } catch (e) {
      console.error('Failed to access microphone:', e);
      alert('Could not access microphone. Please allow microphone access.');
      return;
    }

    mediaRecorder = new MediaRecorder(stream);
    chunks = [];
    mediaRecorder.ondataavailable = e => chunks.push(e.data);
    mediaRecorder.onstop = () => {
      cleanupAudioVisual();
      if (mediaRecorder._shouldUpload) uploadVoiceNote();
      stream.getTracks().forEach(t => t.stop());
    };
    mediaRecorder.start();

    // Swap button for the recording panel
    voiceRecordBtn.style.display = 'none';
    panel.style.display = 'flex';

    // Timer
    recStartTime = Date.now();
    timerInterval = setInterval(() => {
      const secs = Math.floor((Date.now() - recStartTime) / 1000);
      timerEl.textContent = `${Math.floor(secs / 60)}:${String(secs % 60).padStart(2, '0')}`;
    }, 250);

    // Waveform via AnalyserNode
    audioCtx = new (window.AudioContext || window.webkitAudioContext)();
    const source = audioCtx.createMediaStreamSource(stream);
    analyser = audioCtx.createAnalyser();
    analyser.fftSize = 64;
    source.connect(analyser);
    drawWaveform();
  }

  function drawWaveform() {
    const bufferLength = analyser.frequencyBinCount;
    const data = new Uint8Array(bufferLength);

    function render() {
      animationId = requestAnimationFrame(render);
      analyser.getByteFrequencyData(data);

      ctx2d.clearRect(0, 0, canvas.width, canvas.height);
      const barWidth = canvas.width / bufferLength;
      for (let i = 0; i < bufferLength; i++) {
        const barHeight = (data[i] / 255) * canvas.height;
        ctx2d.fillStyle = '#EF4444';
        ctx2d.fillRect(i * barWidth, canvas.height - barHeight, barWidth - 1, barHeight);
      }
    }
    render();
  }

  function cleanupAudioVisual() {
    if (animationId) cancelAnimationFrame(animationId);
    if (audioCtx) audioCtx.close();
    clearInterval(timerInterval);
    ctx2d.clearRect(0, 0, canvas.width, canvas.height);
    panel.style.display = 'none';
    voiceRecordBtn.style.display = 'inline-flex';
  }

  function stopRecording(shouldUpload) {
    if (!mediaRecorder || mediaRecorder.state === 'inactive') return;
    mediaRecorder._shouldUpload = shouldUpload;
    mediaRecorder.stop();
  }

  function uploadVoiceNote() {
    const blob = new Blob(chunks, { type: 'audio/webm' });
    const formData = new FormData();
    formData.append('file', blob, 'voice-note.webm');
    formData.append('type', 'Voice');

    fetch(window.location.pathname + '/message/attachment', {
      method: 'POST',
      body: formData,
      headers: { 'X-XSRF-TOKEN': getCsrfToken() }
    }).then(response => {
      location.href = response.url;
    }).catch(e => {
      console.error('Failed to upload voice note:', e);
      alert('Failed to send voice message. Please try again.');
    });
  }
})();
