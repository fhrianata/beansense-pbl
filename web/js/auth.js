// =============================================
// auth.js — JWT management untuk pure frontend
// Di-include di SEMUA halaman admin (sebelum script lain)
// =============================================

const Auth = (() => {
  const TOKEN_KEY = 'bs_token';
  const USER_KEY  = 'bs_user';
  const GATEWAY   = ''; // kosong = relative URL (via Nginx proxy ke gateway)

  // ---- Simpan data login ----
  function save(token, user) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  // ---- Ambil token ----
  function getToken() {
    return localStorage.getItem(TOKEN_KEY);
  }

  // ---- Ambil data user (nama, role, dll) ----
  function getUser() {
    try {
      return JSON.parse(localStorage.getItem(USER_KEY)) || {};
    } catch {
      return {};
    }
  }

  // ---- Cek apakah token masih valid (decode payload JWT, cek exp) ----
  function isLoggedIn() {
    const token = getToken();
    if (!token) return false;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      // exp dalam detik (Unix timestamp)
      return payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  // ---- Guard: panggil di awal setiap halaman admin ----
  // Kalau belum login → redirect ke /login.html
  function requireLogin() {
    if (!isLoggedIn()) {
      clear();
      window.location.href = '/login.html';
    }
  }

  // ---- Hapus data login (logout) ----
  function clear() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  }

  // ---- Helper: fetch dengan Authorization header otomatis ----
  // Pakai ini sebagai pengganti fetch() biasa untuk semua API call
  async function apiFetch(path, options = {}) {
    const token = getToken();
    const headers = {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': 'Bearer ' + token } : {}),
      ...(options.headers || {})
    };
    const res = await fetch(GATEWAY + path, { ...options, headers });
    // Kalau 401 → token expired / invalid → logout
    if (res.status === 401) {
      clear();
      window.location.href = '/login.html';
      return null;
    }
    return res;
  }

  // ---- Isi elemen profil sidebar (nama + role) ----
  function fillSidebarProfile() {
    const user = getUser();
    const nameEl = document.getElementById('sidebarNama');
    const roleEl = document.getElementById('sidebarRole');
    if (nameEl) nameEl.textContent = user.nama || 'Administrator';
    if (roleEl) roleEl.textContent = user.role || 'Admin';
  }

  return { save, getToken, getUser, isLoggedIn, requireLogin, clear, apiFetch, fillSidebarProfile };
})();