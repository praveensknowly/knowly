document.addEventListener('DOMContentLoaded', function () {
  var emailInput = document.getElementById('email');
  var emailError = document.getElementById('emailError');
  var sendCodeBtn = document.getElementById('sendCodeBtn');
  var resetSection = document.getElementById('resetSection');
  var codeInput = document.getElementById('code');
  var newPasswordInput = document.getElementById('newPassword');
  var resetError = document.getElementById('resetError');
  var resetBtn = document.getElementById('resetBtn');

  sendCodeBtn.addEventListener('click', function () {
    var email = emailInput.value.trim();
    if (!email) {
      emailError.textContent = 'Email is required.';
      return;
    }
    sendCodeBtn.disabled = true;
    sendCodeBtn.textContent = 'Sending...';

    fetch('/forgot-password/send-otp', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email })
    })
      .then(function (res) { return res.json(); })
      .then(function (data) {
        emailError.textContent = data.success ? '' : data.message;
        if (data.success) {
          resetSection.style.display = 'block';
        }
      })
      .finally(function () {
        sendCodeBtn.disabled = false;
        sendCodeBtn.textContent = 'Send Code';
      });
  });

  resetBtn.addEventListener('click', function () {
    var email = emailInput.value.trim();
    var code = codeInput.value.trim();
    var newPassword = newPasswordInput.value;

    if (!/^\d{6}$/.test(code)) {
      resetError.textContent = 'Enter the 6-digit code.';
      return;
    }
    if (newPassword.length < 6) {
      resetError.textContent = 'Password must be at least 6 characters.';
      return;
    }

    resetBtn.disabled = true;
    resetBtn.textContent = 'Resetting...';

    fetch('/forgot-password/reset', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email, code: code, newPassword: newPassword })
    })
      .then(function (res) { return res.json(); })
      .then(function (data) {
        if (data.success) {
          window.location.href = '/login';
        } else {
          resetError.textContent = data.message;
        }
      })
      .finally(function () {
        resetBtn.disabled = false;
        resetBtn.textContent = 'Reset Password';
      });
  });
});
