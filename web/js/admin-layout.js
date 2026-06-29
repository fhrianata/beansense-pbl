// =============================================
// admin-layout.js
// Di-include di semua halaman admin.
// Inject sidebar + topbar hamburger ke DOM,
// lalu aktifkan toggle mobile drawer.
// =============================================

(function () {
  // Tentukan menu mana yang aktif berdasarkan URL
  function getActivePage() {
    const path = window.location.pathname;
    if (path.includes('dashboard'))  return 'dashboard';
    if (path.includes('monitoring')) return 'monitoring';
    if (path.includes('history'))    return 'history';
    if (path.includes('analytics'))  return 'analytics';
    if (path.includes('user'))       return 'user';
    if (path.includes('setting'))    return 'setting';
    return '';
  }

  function navLink(href, icon, label, page, active) {
    const isActive = page === active ? 'active' : '';
    return `
      <a href="${href}" class="admin-nav-link ${isActive}">
        <svg class="admin-nav-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">${icon}</svg>
        <span>${label}</span>
      </a>`;
  }

  const icons = {
    dashboard:  '<rect x="3" y="3" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.8"/><rect x="14" y="3" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.8"/><rect x="3" y="14" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.8"/><rect x="14" y="14" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.8"/>',
    monitoring: '<path d="M3 12h4l2 7 4-14 2 7h6" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>',
    history:    '<circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.8"/><path d="M12 7v5l3 3" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>',
    analytics:  '<path d="M4 19V10M10 19V5M16 19v-7" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>',
    user:       '<circle cx="12" cy="8" r="3.5" stroke="currentColor" stroke-width="1.8"/><path d="M4 20c0-3.3 3.6-6 8-6s8 2.7 8 6" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>',
    setting:    '<circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.8"/><path d="M12 1v2.5M12 20.5V23M4.2 4.2l1.8 1.8M18 18l1.8 1.8M1 12h2.5M20.5 12H23M4.2 19.8l1.8-1.8M18 6l1.8-1.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>',
    logout:     '<path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/><path d="M16 17l5-5-5-5M21 12H9" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>',
  };

  const active = getActivePage();

  const sidebarHTML = `
    <div class="admin-sidebar-overlay" id="adminSidebarOverlay"></div>

    <aside class="admin-sidebar" id="adminSidebar">
      <!-- Tombol hamburger (mobile) -->
      <button class="admin-sidebar-toggle" id="adminSidebarToggle" type="button" aria-label="Buka menu">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M3 6h18M3 12h18M3 18h18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </button>

      <!-- Brand -->
      <div class="admin-brand">
        <img src="/img/logo.png" alt="BeanSense Logo" class="admin-logo" />
        <span class="admin-brand-name">BeanSense</span>
        <button class="admin-sidebar-close" id="adminSidebarClose" type="button" aria-label="Tutup menu">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6 6 18M6 6l12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>
      </div>

      <!-- Navigation -->
      <nav class="admin-nav">
        <p class="admin-nav-label">IoT's SISTEM</p>
        ${navLink('/admin/dashboard.html',  icons.dashboard,  'Dashboard',   'dashboard',  active)}
        ${navLink('/admin/monitoring.html', icons.monitoring, 'Monitoring',  'monitoring', active)}
        ${navLink('/admin/history.html',    icons.history,    'History',     'history',    active)}
        ${navLink('/admin/analytics.html',  icons.analytics,  'Analytics',   'analytics',  active)}

        <p class="admin-nav-label">MANAGEMENT</p>
        ${navLink('/admin/user.html',       icons.user,       'User',        'user',       active)}
        ${navLink('/admin/setting.html',    icons.setting,    'Pengaturan',  'setting',    active)}
      </nav>

      <!-- Footer: profile + logout -->
      <div class="admin-sidebar-footer">
        <div class="admin-profile">
          <div class="admin-avatar">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="8" r="3.5" stroke="currentColor" stroke-width="1.8"/>
              <path d="M4 20c0-3.3 3.6-6 8-6s8 2.7 8 6" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
            </svg>
          </div>
          <div class="admin-profile-text">
            <span class="admin-profile-name" id="sidebarNama">Administrator</span>
            <span class="admin-profile-role" id="sidebarRole">Administrator</span>
          </div>
        </div>
        <button class="admin-logout-btn" id="logoutBtn" aria-label="Logout">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            ${icons.logout}
          </svg>
        </button>
      </div>
    </aside>`;

  // Inject sebelum konten utama
  document.body.insertAdjacentHTML('afterbegin', sidebarHTML);

  // Isi nama & role dari Auth
  Auth.fillSidebarProfile();

  // Logout
  document.getElementById('logoutBtn').addEventListener('click', () => {
    Auth.clear();
    window.location.href = '/login.html';
  });

  // ---- Mobile drawer toggle ----
  const sidebar  = document.getElementById('adminSidebar');
  const toggle   = document.getElementById('adminSidebarToggle');
  const closeBtn = document.getElementById('adminSidebarClose');
  const overlay  = document.getElementById('adminSidebarOverlay');

  function openSidebar()  { sidebar.classList.add('is-open');    document.body.classList.add('admin-sidebar-open'); }
  function closeSidebar() { sidebar.classList.remove('is-open'); document.body.classList.remove('admin-sidebar-open'); }

  toggle.addEventListener('click', openSidebar);
  closeBtn.addEventListener('click', closeSidebar);
  overlay.addEventListener('click', closeSidebar);
  sidebar.querySelectorAll('.admin-nav-link').forEach(l => l.addEventListener('click', closeSidebar));
  window.addEventListener('resize', () => { if (window.innerWidth > 992) closeSidebar(); });
})();