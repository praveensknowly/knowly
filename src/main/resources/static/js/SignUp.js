(function () {
  'use strict';

  /* ============================================================
     HELPERS
  ============================================================ */
  function $(id) { return document.getElementById(id); }

  function setFieldState(fieldId, state, message) {
    var field = document.getElementById('field-' + fieldId);
    var error = $(fieldId + 'Error');
    if (!field) return;
    field.classList.remove('valid', 'error');
    if (state) field.classList.add(state);
    if (error) error.textContent = message || '';
  }

  function clearField(fieldId) {
    var field = document.getElementById('field-' + fieldId);
    if (field) { field.classList.remove('valid', 'error'); }
    var error = $(fieldId + 'Error');
    if (error) error.textContent = '';
  }

  /* ============================================================
     VALIDATORS
  ============================================================ */
  function validateName(val) {
    if (!val.trim()) return 'Full name is required.';
    if (val.trim().length < 2) return 'Name must be at least 2 characters.';
    if (!/^[a-zA-Z\s'.,-]+$/.test(val.trim())) return 'Please enter a valid name.';
    return '';
  }

  function validateEmail(val) {
    if (!val.trim()) return 'Email address is required.';
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(val.trim())) return 'Enter a valid email address.';
    return '';
  }

  function validateNumber(val) {
    var digits = val.replace(/[\s\-\+\(\)]/g, '');
    if (!val.trim()) return 'Phone number is required.';
    if (!/^\d+$/.test(digits)) return 'Phone number must contain only digits.';
    if (digits.length < 7 || digits.length > 15) return 'Enter a valid phone number (7–15 digits).';
    return '';
  }

  function validatePassword(val) {
    if (!val) return 'Password is required.';
    if (val.length < 8) return 'Password must be at least 8 characters.';
    return '';
  }

  function validateConfirm(val, original) {
    if (!val) return 'Please confirm your password.';
    if (val !== original) return 'Passwords do not match.';
    return '';
  }

  /* ============================================================
     PASSWORD STRENGTH
  ============================================================ */
  var strengthConfigs = [
    { label: 'Weak',   cls: 'weak',   fill: 1 },
    { label: 'Fair',   cls: 'fair',   fill: 2 },
    { label: 'Good',   cls: 'good',   fill: 3 },
    { label: 'Strong', cls: 'strong', fill: 4 }
  ];

  function getStrength(val) {
    if (!val) return -1;
    var score = 0;
    if (val.length >= 8)  score++;
    if (val.length >= 12) score++;
    if (/[A-Z]/.test(val) && /[a-z]/.test(val)) score++;
    if (/[0-9]/.test(val)) score++;
    if (/[^A-Za-z0-9]/.test(val)) score++;
    if (score <= 1) return 0;
    if (score === 2) return 1;
    if (score === 3) return 2;
    return 3;
  }

  function renderStrength(val) {
    var level = getStrength(val);
    var segs  = [$('seg1'), $('seg2'), $('seg3'), $('seg4')];
    var label = $('strengthLabel');

    segs.forEach(function (s) {
      s.className = 'su-seg';
    });
    label.className = 'su-strength-label';
    label.textContent = '';

    if (level < 0 || !val) return;

    var cfg = strengthConfigs[level];
    for (var i = 0; i < cfg.fill; i++) {
      segs[i].classList.add(cfg.cls);
    }
    label.classList.add(cfg.cls);
    label.textContent = cfg.label;
  }

  /* ============================================================
     PASSWORD VISIBILITY TOGGLE
  ============================================================ */
  function setupToggle(toggleId, inputId, showIconId, hideIconId) {
    var btn  = $(toggleId);
    var inp  = $(inputId);
    var show = $(showIconId);
    var hide = $(hideIconId);
    if (!btn || !inp) return;

    btn.addEventListener('click', function () {
      var isPassword = inp.type === 'password';
      inp.type = isPassword ? 'text' : 'password';
      show.style.display = isPassword ? 'none'  : 'block';
      hide.style.display = isPassword ? 'block' : 'none';
      btn.setAttribute('aria-label', isPassword ? 'Hide password' : 'Show password');
    });
  }

  setupToggle('togglePassword', 'password',        'eyeShow',  'eyeHide');
  setupToggle('toggleConfirm',  'confirmPassword',  'eyeShow2', 'eyeHide2');

  /* ============================================================
     FIELD REFERENCES (must come before anything that uses them)
  ============================================================ */
  var nameInput    = $('name');
  var emailInput   = $('email');
  var numberInput  = $('number');
  var passInput    = $('password');
  var confirmInput = $('confirmPassword');
  var termsInput   = $('terms');

  /* ============================================================
     EMAIL OTP VERIFICATION
  ============================================================ */
    var verifyBtn     = $('verifyEmailBtn');
    var emailStatus   = $('emailStatus');
    var otpField      = $('field-otp');
    var otpInput      = $('otpCode');
    var confirmOtpBtn = $('confirmOtpBtn');
    var otpStatus     = $('otpStatus');
    var resendLink    = $('resendOtpLink');
    var verifiedFlag  = $('emailVerifiedFlag');
	var verifiedCheck = $('emailVerifiedCheck');

    var COOLDOWN_SECONDS = 30;
    var cooldownTimer     = null;
    var cooldownRemaining = 0;

    function updateResendUI() {
      if (cooldownRemaining > 0) {
        resendLink.classList.add('disabled');
        resendLink.textContent = 'Resend code (' + cooldownRemaining + 's)';
        verifyBtn.textContent = 'Resend (' + cooldownRemaining + 's)';
        verifyBtn.disabled = true;
      } else {
        resendLink.classList.remove('disabled');
        resendLink.textContent = 'Resend code';
        verifyBtn.textContent = 'Resend';
        verifyBtn.disabled = false;
      }
    }

    function startCooldown() {
      cooldownRemaining = COOLDOWN_SECONDS;
      updateResendUI();

      if (cooldownTimer) clearInterval(cooldownTimer);
      cooldownTimer = setInterval(function () {
        cooldownRemaining--;
        if (cooldownRemaining <= 0) {
          clearInterval(cooldownTimer);
          cooldownTimer = null;
          cooldownRemaining = 0;
        }
        updateResendUI();
      }, 1000);
    }

    function stopCooldown() {
      if (cooldownTimer) clearInterval(cooldownTimer);
      cooldownTimer = null;
      cooldownRemaining = 0;
      updateResendUI();
    }

	function markEmailUnverified() {
	  verifiedFlag.value = 'false';
	  verifyBtn.textContent = 'Verify';
	  verifyBtn.disabled = false;
	  verifyBtn.style.display = '';           // bring button back
	  verifyBtn.classList.remove('verified');
	  verifiedCheck.style.display = 'none';   // hide checkmark
	  emailInput.readOnly = false;            // unfreeze
	  emailInput.classList.remove('frozen');
	  otpField.style.display = 'none';
	  otpInput.disabled = false;
	  otpInput.value = '';
	  confirmOtpBtn.style.display = '';    
	  resendLink.style.display = '';         
	  emailStatus.textContent = '';
	  stopCooldown();
	}

    // If they edit the email after verifying, verification resets
    if (emailInput) {
      emailInput.addEventListener('input', function () {
        if (verifiedFlag.value === 'true') {
          markEmailUnverified();
        }
      });
    }

    if (verifyBtn) {
      verifyBtn.addEventListener('click', function () {
        if (cooldownRemaining > 0) return; // guard against race clicks

        var emailErr = validateEmail(emailInput.value);
        setFieldState('email', emailErr ? 'error' : 'valid', emailErr);
        if (emailErr) return;

        verifyBtn.disabled = true;
        verifyBtn.textContent = 'Sending...';

        fetch('/send-otp', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email: emailInput.value.trim() })
        })
          .then(function (res) { return res.json(); })
          .then(function (data) {
            if (data.success) {
              otpField.style.display = 'block';
              otpStatus.textContent = 'Code sent to your inbox.';
              otpInput.focus();
              startCooldown();
            } else {
              setFieldState('email', 'error', data.message);
              verifyBtn.textContent = 'Verify';
              verifyBtn.disabled = false;
            }
          })
          .catch(function () {
            setFieldState('email', 'error', 'Something went wrong. Try again.');
            verifyBtn.textContent = 'Verify';
            verifyBtn.disabled = false;
          });
      });
    }

    if (resendLink) {
      resendLink.addEventListener('click', function (e) {
        e.preventDefault();
        if (cooldownRemaining > 0) return; // still cooling down, ignore
        verifyBtn.click();
      });
    }

    if (confirmOtpBtn) {
      confirmOtpBtn.addEventListener('click', function () {
        var code = otpInput.value.trim();
        if (!/^\d{6}$/.test(code)) {
          $('otpError').textContent = 'Enter the 6-digit code.';
          return;
        }

        confirmOtpBtn.disabled = true;
        confirmOtpBtn.textContent = 'Checking...';

        fetch('/verify-otp', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email: emailInput.value.trim(), code: code })
        })
          .then(function (res) { return res.json(); })
          .then(function (data) {
            confirmOtpBtn.disabled = false;
            confirmOtpBtn.textContent = 'Confirm';

			if (data.success) {
			  verifiedFlag.value = 'true';
			  $('otpError').textContent = '';
			  otpStatus.textContent = 'Email verified.';
			  otpInput.disabled = true;
			  confirmOtpBtn.style.display = 'none';
			  resendLink.style.display = 'none';
			  verifyBtn.style.display = 'none';       // hide the button entirely, checkmark takes its place
			  verifiedCheck.style.display = 'flex';   // show green tick
			  emailInput.readOnly = true;             // freeze the field
			  emailInput.classList.add('frozen');
			  setFieldState('email', 'valid', '');
			  stopCooldown();
			} else {
              $('otpError').textContent = data.message;
            }
          })
          .catch(function () {
            confirmOtpBtn.disabled = false;
            confirmOtpBtn.textContent = 'Confirm';
            $('otpError').textContent = 'Something went wrong. Try again.';
          });
      });
    }

  /* ============================================================
     LIVE VALIDATION (blur + input events)
  ============================================================ */
  function bindLive(input, fieldId, validatorFn, extra) {
    if (!input) return;

    input.addEventListener('blur', function () {
      var err = validatorFn(input.value, extra ? extra() : undefined);
      setFieldState(fieldId, err ? 'error' : 'valid', err);
    });

    input.addEventListener('input', function () {
      if (input.value === '') { clearField(fieldId); return; }
      var err = validatorFn(input.value, extra ? extra() : undefined);
      setFieldState(fieldId, err ? 'error' : 'valid', err);
    });
  }

  bindLive(nameInput,   'name',    validateName);
  bindLive(emailInput,  'email',   validateEmail);
  bindLive(numberInput, 'number',  validateNumber);

  if (passInput) {
    passInput.addEventListener('input', function () {
      renderStrength(passInput.value);
      var err = validatePassword(passInput.value);
      if (passInput.value === '') { clearField('password'); return; }
      setFieldState('password', err ? 'error' : 'valid', err);

      // re-validate confirm if already typed
      if (confirmInput && confirmInput.value) {
        var cErr = validateConfirm(confirmInput.value, passInput.value);
        setFieldState('confirm', cErr ? 'error' : 'valid', cErr);
      }
    });
    passInput.addEventListener('blur', function () {
      var err = validatePassword(passInput.value);
      if (passInput.value) setFieldState('password', err ? 'error' : 'valid', err);
    });
  }

  if (confirmInput) {
    bindLive(
      confirmInput, 'confirm',
      validateConfirm,
      function () { return passInput ? passInput.value : ''; }
    );
  }

  /* ============================================================
     FORM SUBMIT
  ============================================================ */
  var form      = $('signupForm');
  var submitBtn = $('submitBtn');

  if (form) {
      form.addEventListener("submit", function (e) {

          var nameVal    = nameInput ? nameInput.value.trim() : "";
          var emailVal   = emailInput ? emailInput.value.trim() : "";
          var numberVal  = numberInput ? numberInput.value.trim() : "";
          var passVal    = passInput ? passInput.value : "";
          var confirmVal = confirmInput ? confirmInput.value : "";
          var termsVal   = termsInput ? termsInput.checked : false;

          var nameErr    = validateName(nameVal);
          var emailErr   = validateEmail(emailVal);
          var numberErr  = validateNumber(numberVal);
          var passErr    = validatePassword(passVal);
          var confirmErr = validateConfirm(confirmVal, passVal);

          setFieldState("name", nameErr ? "error" : "valid", nameErr);
          setFieldState("email", emailErr ? "error" : "valid", emailErr);
          setFieldState("number", numberErr ? "error" : "valid", numberErr);
          setFieldState("password", passErr ? "error" : "valid", passErr);
          setFieldState("confirm", confirmErr ? "error" : "valid", confirmErr);

          var termsErrEl = $("termsError");
          if (!termsVal) {
              if (termsErrEl)
                  termsErrEl.textContent = "You must agree to the Terms of Service.";
          } else {
              if (termsErrEl)
                  termsErrEl.textContent = "";
          }

          var notVerified = verifiedFlag.value !== 'true';
          if (notVerified) {
            emailStatus.textContent = 'Please verify your email before continuing.';
          }

          var hasErrors = nameErr || emailErr || numberErr || passErr || confirmErr || !termsVal || notVerified;

          if (hasErrors) {
              e.preventDefault(); // Stop submission only when there are errors

              var firstError = form.querySelector(".su-field.error, .su-terms-wrap");
              if (firstError) {
                  firstError.scrollIntoView({
                      behavior: "smooth",
                      block: "center"
                  });
              }
              return;
          }

          // Show loading animation
          submitBtn.classList.add("loading");
          submitBtn.disabled = true;

          // DON'T call preventDefault().
          // The browser will automatically submit the form to Spring Boot.
      });
  }

})();