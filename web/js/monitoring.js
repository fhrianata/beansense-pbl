// =============================================
// MONITORING PAGE — Real-time tanpa refresh
// =============================================
// Poll endpoint API Gateway setiap beberapa detik dan update UI.
//
// Format response JSON dari /api/sensor-warna/latest:
// {
//   "weight": 12.5,
//   "wadah": "MATANG",
//   "classification": "MATANG",
//   "sensorWarna": "MERAH",
//   "berat_matang": 45.2,
//   "berat_mentah": 31.0,
//   "total_matang": 12,
//   "total_mentah": 8,
//   "sensors": { "rgb": true, "loadcell": true, "server": true },
//   "timestamp": "2026-06-20T14:20:05"
// }

(function () {
  // Ambil refresh interval dari localStorage (halaman Setting)
  const savedInterval = parseInt(localStorage.getItem('bs_refreshInterval'), 10);
  const REFRESH_INTERVAL =
    !isNaN(savedInterval) && savedInterval > 0 ? savedInterval * 1000 : 2000;

  const elWeight = document.getElementById('valWeight');
  const elClassBadge = document.getElementById('classificationBadge');
  const elLogList = document.getElementById('liveLogList');
  const elConnBadge = document.getElementById('connectionBadge');
  const elConnText = document.getElementById('connectionText');

  const dotRgb = document.getElementById('dotRgb');
  const dotLoadcell = document.getElementById('dotLoadcell');
  const dotServer = document.getElementById('dotServer');
  const statusRgb = document.getElementById('statusRgb');
  const statusLoadcell = document.getElementById('statusLoadcell');
  const statusServer = document.getElementById('statusServer');

  // Elemen berat per wadah
  const elBeratMatang = document.getElementById('valBeratMatang');
  const elBeratMentah = document.getElementById('valBeratMentah');
  const elTotalMatang = document.getElementById('valTotalMatang');
  const elTotalMentah = document.getElementById('valTotalMentah');

  // Elemen RGB swatch
  const elSwatch = document.getElementById('rgbSwatch');
  const elR = document.getElementById('valR');
  const elG = document.getElementById('valG');
  const elB = document.getElementById('valB');

  if (!elLogList) return;

  let lastTimestamp = null;
  const MAX_LOG_ITEMS = 50;

  function setSensorStatus(dotEl, textEl, isOnline, onlineLabel, offlineLabel) {
    if (!dotEl || !textEl) return;
    dotEl.classList.toggle('sensor-status-online', isOnline);
    dotEl.classList.toggle('sensor-status-offline', !isOnline);
    textEl.textContent = isOnline ? onlineLabel : offlineLabel;
  }

  function setClassificationBadge(classification) {
    if (!elClassBadge) return;
    const val =
      classification && classification !== '-' ? classification : 'MENUNGGU';
    elClassBadge.textContent = val;
    elClassBadge.classList.remove('is-matang', 'is-mentah', 'is-reject');
    if (classification === 'MATANG') elClassBadge.classList.add('is-matang');
    else if (classification === 'MENTAH') elClassBadge.classList.add('is-mentah');
    else if (classification === 'REJECT') elClassBadge.classList.add('is-reject');
  }

  // Warna swatch berdasarkan klasifikasi
  function setRgbSwatchFromKlasifikasi(classification) {
    if (!elSwatch) return;
    if (classification === 'MATANG') {
      elSwatch.style.backgroundColor = 'rgb(180, 60, 60)';
    } else if (classification === 'MENTAH') {
      elSwatch.style.backgroundColor = 'rgb(60, 160, 60)';
    } else {
      elSwatch.style.backgroundColor = 'rgb(180, 180, 180)';
    }
    if (elR) elR.textContent = '--';
    if (elG) elG.textContent = '--';
    if (elB) elB.textContent = '--';
  }

  function formatTime(raw) {
    if (!raw) return '-';
    try {
      const clean = raw.substring(0, 19);
      const date = new Date(clean);
      return date.toLocaleString('id-ID', {
        year: 'numeric',
        month: 'short',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false,
      });
    } catch (e) {
      return raw;
    }
  }

  function pushLogItem(data) {
    const placeholder = elLogList.querySelector('.log-item-empty');
    if (placeholder) placeholder.remove();

    const time = formatTime(data.timestamp);
    const warnaLabel = data.sensorWarna || data.classification || '-';
    const weightVal =
      data.weight !== undefined && data.weight !== null
        ? parseFloat(data.weight).toFixed(1) + ' g'
        : '-';
    const wadah = data.wadah || data.classification || '-';

    const item = document.createElement('p');
    item.className = 'log-item log-item-new';
    item.textContent = `[${time}] ${warnaLabel} — ${weightVal} → ${wadah}`;
    elLogList.insertBefore(item, elLogList.firstChild);

    while (elLogList.children.length > MAX_LOG_ITEMS) {
      elLogList.removeChild(elLogList.lastChild);
    }
  }

  function setConnectionState(connected) {
    if (!elConnBadge || !elConnText) return;
    if (connected) {
      elConnBadge.style.background = 'rgba(111, 156, 93, 0.1)';
      elConnBadge.style.borderColor = 'rgba(111, 156, 93, 0.25)';
      elConnBadge.style.color = '#5a8049';
      elConnText.textContent = 'Live';
    } else {
      elConnBadge.style.background = 'rgba(217, 83, 79, 0.1)';
      elConnBadge.style.borderColor = 'rgba(217, 83, 79, 0.25)';
      elConnBadge.style.color = '#d9534f';
      elConnText.textContent = 'Terputus';
    }
  }

  function updateUI(data) {
    if (elWeight) {
      const w =
        data.weight !== undefined && data.weight !== null
          ? parseFloat(data.weight)
          : 0;
      elWeight.textContent = w.toFixed(1);
    }

    setClassificationBadge(data.classification);
    setRgbSwatchFromKlasifikasi(data.classification);

    const sensors = data.sensors || {};
    setSensorStatus(dotRgb, statusRgb, sensors.rgb !== false, 'Online', 'Offline');
    setSensorStatus(dotLoadcell, statusLoadcell, sensors.loadcell !== false, 'Online', 'Offline');
    setSensorStatus(dotServer, statusServer, sensors.server !== false, 'Terhubung', 'Putus');

    if (elBeratMatang)
      elBeratMatang.textContent = parseFloat(data.berat_matang || 0).toFixed(1);
    if (elBeratMentah)
      elBeratMentah.textContent = parseFloat(data.berat_mentah || 0).toFixed(1);
    if (elTotalMatang) elTotalMatang.textContent = data.total_matang || 0;
    if (elTotalMentah) elTotalMentah.textContent = data.total_mentah || 0;

    if (data.timestamp && data.timestamp !== lastTimestamp) {
      lastTimestamp = data.timestamp;
      pushLogItem(data);
    }
  }

  async function fetchData() {
    try {
      const res = await Auth.apiFetch('/api/sensor-warna/latest');
      if (!res || !res.ok) throw new Error('HTTP ' + (res ? res.status : '?'));
      const data = await res.json();
      setConnectionState(true);
      updateUI(data);
    } catch (err) {
      console.warn('[monitoring] fetch error:', err.message);
      setConnectionState(false);
      setSensorStatus(dotServer, statusServer, false, 'Terhubung', 'Putus');
    }
  }

  // Mulai polling
  fetchData();
  setInterval(fetchData, REFRESH_INTERVAL);
})();
