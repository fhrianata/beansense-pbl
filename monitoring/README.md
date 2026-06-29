# BeanSense Backend — Monitoring Setup (Prometheus + Grafana + ELK)

## Konsep

Sesuai arahan dosen, stack monitoring yang dipakai:

| Kebutuhan | Tool | Kenapa |
|---|---|---|
| **Metrics** (CPU, memory, request rate, latency, error rate, JVM, custom metric sensor) | **Prometheus + Grafana** | Prometheus *scrape* (tarik) metrics dari endpoint `/actuator/prometheus` tiap service secara periodik. Grafana memvisualisasikannya dalam dashboard. |
| **Logs** (`logger.info/error/warn` dari aplikasi & data sensor masuk) | **ELK Stack** (Elasticsearch + Logstash + Kibana) | Tiap service Spring Boot kirim log langsung (format JSON, lewat TCP) ke **Logstash**. Logstash memproses lalu menyimpannya ke **Elasticsearch**. **Kibana** dipakai untuk cari, filter, dan visualisasi log. |

Prometheus tetap dipakai khusus untuk metrics (data numerik time-series), karena itu memang tujuannya dan tidak cocok untuk menyimpan teks log. ELK menangani log secara terpisah, sesuai standar yang lebih umum dipakai di industri/diajarkan di kelas.

## Yang sudah diterapkan ke project ini

### 1. Metrics (tidak berubah dari sebelumnya)
- Tiap service (`eureka`, `api-gateway`, `auth-service`, `sensor-warna-service`, `sensor-berat-service`, `web-service`) punya dependency `spring-boot-starter-actuator` + `micrometer-registry-prometheus`, dan endpoint `/actuator/prometheus` aktif.
- Custom metric sensor: `beansense_sensor_warna_klasifikasi_total`, `beansense_sensor_berat_masuk_total`, `beansense_sensor_berat_terakhir_gram`.
- `prometheus.yml` men-scrape ke-6 service tiap 10 detik.
- Dashboard Grafana (`BeanSense - Microservices Overview`) auto-provisioned: status up/down, JVM heap, request rate, error 5xx, CPU, plus 4 panel khusus sensor (klasifikasi warna, berat masuk per wadah, berat terakhir, pie chart total biji).

### 2. Logs (baru, ganti dari Loki/Promtail ke ELK)
- Tiap service ditambah dependency `net.logstash.logback:logstash-logback-encoder` di `pom.xml`.
- Tiap service punya `logback-spring.xml` yang mengirim log ke **Logstash** (`LOGSTASH_HOST:5000`, format JSON), selain tetap tercetak di console (`docker compose logs` masih jalan normal).
- Tiap baris log otomatis ditandai field `service` (nama service pengirim), supaya gampang difilter di Kibana.
- Logstash (`monitoring/elk/logstash/pipeline/logstash.conf`) menerima log lewat TCP port 5000, lalu menyimpannya ke Elasticsearch dengan index per service per hari: `beansense-<service>-YYYY.MM.dd`.

### 3. Database — TIDAK diubah
Koneksi & query SQL ke database `beansense` (MySQL lokal via `host.docker.internal`) **tidak disentuh sama sekali** dalam perubahan ini — tetap seperti setup sebelumnya, aman dan jalan normal.

## Cara menjalankan

```bash
cd beansense-backend
docker compose up -d --build
```

⚠️ **ELK butuh resource lumayan** (Elasticsearch minimal 512MB-1GB RAM). Kalau laptop kamu terbatas RAM-nya, pastikan Docker Desktop dialokasikan RAM minimal 4GB (Settings → Resources → Memory).

Tunggu semua container `running`/`healthy` (`docker compose ps`), lalu cek:

| Yang ingin dilihat | URL |
|---|---|
| Eureka dashboard | http://localhost:8761 |
| Prometheus targets | http://localhost:9090/targets |
| **Grafana** (metrics dashboard) | http://localhost:3000 (login: `admin` / `admin`) |
| **Kibana** (log explorer) | http://localhost:5601 |
| Elasticsearch (cek cluster health) | http://localhost:9200/_cluster/health |
| Raw metrics tiap service | `http://localhost:<port>/actuator/prometheus` |

## Setup pertama kali di Kibana (cuma sekali)

1. Buka http://localhost:5601
2. Pastikan minimal satu service sudah pernah jalan & kirim log (biar index-nya sudah terbentuk di Elasticsearch)
3. Menu **☰ → Stack Management → Index Patterns / Data Views → Create data view**
4. Index pattern: `beansense-*`
5. Time field: `@timestamp`
6. Simpan, lalu buka **☰ → Discover** untuk lihat & filter log semua service — bisa filter per service lewat field `service` (contoh: `service: "sensor-berat-service"`)

## Monitoring data sensor (HX711 berat & TCS3200 warna)

`sensor-warna-service` dan `sensor-berat-service` mencatat tiap data masuk dari ESP32 lewat `log.info(...)` — sekarang log itu otomatis terkirim ke Logstash → Elasticsearch, bisa dicari/filter di Kibana (misal cari semua log yang mengandung kata "MATANG" atau filter `service: "sensor-warna-service"`).

Selain log, ada juga custom metric Prometheus (tetap di Grafana):
- `beansense_sensor_warna_klasifikasi_total{klasifikasi="MATANG|MENTAH|TIDAK DIKENALI"}`
- `beansense_sensor_berat_masuk_total{wadah="MATANG|MENTAH"}`
- `beansense_sensor_berat_terakhir_gram`

Jadi pembagian tugasnya: **Grafana** untuk lihat tren/grafik angka (berapa biji/menit, berat berapa), **Kibana** untuk telusuri detail teks log per kejadian (misal mau tahu persis jam berapa biji ke-37 lewat dan datanya apa).

## Catatan untuk PBL

- ELK lebih berat dibanding Loki, tapi ini stack yang lebih umum dipakai & diajarkan — cocok untuk pembelajaran dan portofolio.
- Endpoint `/actuator/**` dan port internal (`5000` Logstash, `9200` Elasticsearch, `8761` Eureka) sebaiknya **tidak** diekspos ke publik saat nanti production — untuk demo PBL di lokal ini aman karena masih closed network.
- Kalau Logstash belum jalan saat service start (misal kamu jalankan service manual tanpa Docker), `logback-spring.xml` sudah didesain tidak bikin aplikasi crash — dia cuma retry koneksi di background, log tetap tercetak normal ke console.
