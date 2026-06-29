package com.beansense.sensorwarna.service;

import com.beansense.sensorwarna.dto.SensorWarnaRequest;
import com.beansense.sensorwarna.dto.SensorWarnaResponse;
import com.beansense.sensorwarna.entity.SensorWarnaLog;
import com.beansense.sensorwarna.repository.SensorWarnaLogRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorWarnaService {

    private final SensorWarnaLogRepository repository;
    private final MeterRegistry meterRegistry;

    private final Map<String, Counter> counterPerKlasifikasi = new ConcurrentHashMap<>();

    public SensorWarnaResponse simpan(SensorWarnaRequest request) {
        log.info("Data warna masuk -> sensor_warna={}", request.getSensor_warna());

        String warna = request.getSensor_warna().trim().toUpperCase();

        if (!warna.equals("MERAH") && !warna.equals("HIJAU") && !warna.equals("TIDAK DIKENALI")) {
            log.warn("Nilai warna tidak dikenali dari sensor: '{}'", warna);
            warna = "TIDAK DIKENALI";
        }

        String klasifikasi = switch (warna) {
            case "MERAH" -> "MATANG";
            case "HIJAU" -> "MENTAH";
            default -> "TIDAK DIKENALI";
        };

        SensorWarnaLog log_ = SensorWarnaLog.builder()
                .sensorWarna(warna)
                .hasilKlasifikasi(klasifikasi)
                .timestamp(LocalDateTime.now(ZoneId.of("Asia/Jakarta")))
                .build();

        SensorWarnaLog saved = repository.save(log_);
        log.info("Data warna tersimpan -> id={}, warna={}, klasifikasi={}",
                saved.getId(), saved.getSensorWarna(), saved.getHasilKlasifikasi());

        counterPerKlasifikasi
                .computeIfAbsent(klasifikasi, k -> Counter.builder("beansense_sensor_warna_klasifikasi_total")
                        .description("Jumlah biji kopi yang diklasifikasikan sensor warna")
                        .tags(Tags.of("klasifikasi", k))
                        .register(meterRegistry))
                .increment();

        return toResponse(saved);
    }

    /**
     * Mengembalikan maksimal 200 entri terbaru (bukan seluruh tabel).
     * Dipakai oleh history page — cukup untuk kebutuhan tampilan,
     * tanpa menguras memori saat tabel sudah banyak data.
     */
    public List<SensorWarnaResponse> getAll() {
        return repository.findTop200ByOrderByTimestampDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SensorWarnaResponse getLatest() {
        SensorWarnaLog log = repository.findTopByOrderByIdDesc();
        if (log == null) return null;
        return toResponse(log);
    }

    public List<SensorWarnaResponse> getRecent() {
        return repository.findTop20ByOrderByTimestampDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getStats() {
        long total  = repository.count();
        long matang = repository.countByKlasifikasi("MATANG");
        long mentah = repository.countByKlasifikasi("MENTAH");
        long reject = Math.max(total - matang - mentah, 0);

        return Map.of(
                "total",  total,
                "matang", matang,
                "mentah", mentah,
                "reject", reject
        );
    }

    private SensorWarnaResponse toResponse(SensorWarnaLog log) {
        return SensorWarnaResponse.builder()
                .id(log.getId())
                .sensorWarna(log.getSensorWarna())
                .hasilKlasifikasi(log.getHasilKlasifikasi())
                .timestamp(log.getTimestamp())
                .build();
    }
}
