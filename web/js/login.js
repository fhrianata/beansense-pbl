// =============================================
// LOGIN PAGE — Toggle show/hide password
// =============================================
(function () {
  const toggleBtn = document.getElementById('togglePassword');
  const passwordInput = document.getElementById('loginPassword');

  if (!toggleBtn || !passwordInput) return;

  toggleBtn.addEventListener('click', () => {
    const isPassword = passwordInput.type === 'password';
    passwordInput.type = isPassword ? 'text' : 'password';
    toggleBtn.setAttribute(
      'aria-label',
      isPassword ? 'Sembunyikan password' : 'Tampilkan password'
    );
    toggleBtn.classList.toggle('is-visible', isPassword);
  });
})();
