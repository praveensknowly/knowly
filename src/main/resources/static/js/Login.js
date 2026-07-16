/* ===========================================================
   Login.js — ADDITIONS ONLY
   Append below your existing Login.js. Handles the Sign In
   form: password visibility, validation, loading state, and
   the (UI-only) social buttons.
   =========================================================== */

document.addEventListener('DOMContentLoaded', function () {

  var form = document.getElementById('loginForm');
  if (!form) return;

  var identifier = document.getElementById('identifier');
  var password = document.getElementById('password');
  var submitBtn = document.getElementById('submitBtn');

  // ---------- Show / hide password ----------
  var toggleBtn = document.getElementById('togglePassword');
  var eyeShow = document.getElementById('eyeShow');
  var eyeHide = document.getElementById('eyeHide');

  if (toggleBtn) {
    toggleBtn.addEventListener('click', function () {
      var isHidden = password.type === 'password';
      password.type = isHidden ? 'text' : 'password';
      eyeShow.style.display = isHidden ? 'none' : 'block';
      eyeHide.style.display = isHidden ? 'block' : 'none';
      toggleBtn.setAttribute('aria-label', isHidden ? 'Hide password' : 'Show password');
    });
  }

  // ---------- Validators ----------
  function setFieldError(inputEl, errorId, message) {
    var errorEl = document.getElementById(errorId);
    var fieldEl = inputEl.closest('.su-field');

    if (message) {
      errorEl.textContent = message;
      errorEl.classList.add('show');
      fieldEl.classList.add('su-field-invalid');
      return false;
    }
    errorEl.textContent = '';
    errorEl.classList.remove('show');
    fieldEl.classList.remove('su-field-invalid');
    return true;
  }

  function validateIdentifier() {
    var v = identifier.value.trim();
    if (!v) {
      return setFieldError(identifier, 'identifierError', 'Enter your email or phone number.');
    }
    var isEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v);
    var isPhone = /^[0-9]{10}$/.test(v.replace(/\s+/g, ''));
    if (!isEmail && !isPhone) {
      return setFieldError(identifier, 'identifierError', 'Enter a valid email or 10-digit phone number.');
    }
    return setFieldError(identifier, 'identifierError', '');
  }

  function validatePassword() {
    var v = password.value;
    if (!v) {
      return setFieldError(password, 'passwordError', 'Enter your password.');
    }
    return setFieldError(password, 'passwordError', '');
  }

  identifier.addEventListener('blur', function () {
    if (identifier.value.trim() !== '') validateIdentifier();
  });

  password.addEventListener('blur', function () {
    if (password.value !== '') validatePassword();
  });

  // ---------- OAuth button click guard ----------
  document.querySelectorAll('#googleBtn, #githubBtn').forEach(function (btn) {
    btn.addEventListener('click', function (e) {
      if (btn.classList.contains('is-loading')) {
        e.preventDefault(); // block a second click while the first navigation is in flight
        return;
      }
      btn.classList.add('is-loading');
    });
  });

  // ---------- Submit ----------
  form.addEventListener('submit', function (e) {
    e.preventDefault();

    var identifierValid = validateIdentifier();
    var passwordValid = validatePassword();

    if (!identifierValid || !passwordValid) {
      var firstInvalid = form.querySelector('.su-field-invalid');
      if (firstInvalid) {
        firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
      return;
    }

    // Loading state
    submitBtn.classList.add('su-submit-loading');
    submitBtn.disabled = true;

    // Simulated delay before the real Spring Boot POST fires.
    // In production this setTimeout can be removed and the form
    // allowed to submit natively to th:action="@{/login}".
    setTimeout(function () {
      form.submit();
    }, 900);
  });
});