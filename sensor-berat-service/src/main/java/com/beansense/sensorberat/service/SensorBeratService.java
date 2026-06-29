package com.beansense.sensorberat.service;

import com.beansense.sensorberat.dto.SensorBeratRequest;
import com.beansense.sensorberat.dto.SensorBeratResponse;
import com.beansense.sensorberat.entity.SensorBeratData;
import com.beansense.sensorberat.repository.SensorBeratDataRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SensorBeratService {

    private final SensorBeratDataRepository repository;
    private final MeterRegistry meterRegistry;

    private final Map<String, Counter> counterPerWadah = new ConcurrentHashMap<>();
    private final AtomicReference<Double> beratTerakhir = new AtomicReference<>(0.0);

    public SensorBeratService(
            SensorBeratDataRepository repository,
            MeterRegistry meterRegistry) {

        this.repository = repository;
        this.meterRegistry = meterRegistry;

        meterRegistry.gauge(
                "beansense_sensor_berat_terakhir_gram",
                beratTerakhir,
                AtomicReference::get
        );
    }

    public SensorBeratResponse simpan(SensorBeratRequest request) {
        log.info("Data berat masuk -> wadah={}, berat={}{}",
                request.getWadah(),
                request.getBerat(),
                request.getSatuan() != null ? request.getSatuan() : "gram");

        String wadah = request.getWadah() != null
                ? request.getWadah().trim().toUpperCase()
                : "TIDAK DIKENALI";

        SensorBeratData data = SensorBeratData.builder()
                .wadah(wadah)
                .berat(request.getBerat())
                .satuan(request.getSatuan() != null ? request.getSatuan() : "gram")
                .timestamp(LocalDateTime.now(ZoneId.of("Asia/Jakarta")))
                .build();

        SensorBeratData saved = repository.save(data);

        log.info("Data berat tersimpan -> id={}, wadah={}, berat={}",
                saved.getId(),
                saved.getWadah(),
                saved.getBerat());

        counterPerWadah
                .computeIfAbsent(
                        saved.getWadah(),
                        w -> Counter.builder("beansense_sensor_berat_masuk_total")
                                .description("Jumlah data berat yang diterima dari ESP32 per wadah")
                                .tags(Tags.of("wadah", w))
                                .register(meterRegistry)
                )
                .increment();

        beratTerakhir.set(saved.getBerat());

        return toResponse(saved);
    }

    /**
     * Mengembalikan maksimal 200 entri terbaru (bukan seluruh tabel).
     * Dipakai oleh history page.
     */
    public List<SensorBeratResponse> getAll() {
        return repository.findTop200ByOrderByTimestampDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SensorBeratResponse getLatest() {
        SensorBeratData data = repository.findTopByOrderByIdDesc();
        if (data == null) return null;
        return toResponse(data);
    }

    public List<SensorBeratResponse> getRecent() {
        return repository.findTop20ByOrderByTimestampDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getStats() {
        long total = repository.count();

        Double avg = repository.avgBerat();
        Double max = repository.maxBerat();

        long totalMatang = repository.countByWadah("MATANG");
        long totalMentah = repository.countByWadah("MENTAH");

        Double beratMatang = repository.findFirstByWadahOrderByIdDesc("MATANG")
                .map(SensorBeratData::getBerat)
                .orElse(0.0);

        Double beratMentah = repository.findFirstByWadahOrderByIdDesc("MENTAH")
                .map(SensorBeratData::getBerat)
                .orElse(0.0);

        return Map.of(
                "total",        total,
                "total_matang", totalMatang,
                "total_mentah", totalMentah,
                "berat_matang", beratMatang,
                "berat_mentah", beratMentah,
                "rata_rata_gram", avg  != null ? avg  : 0.0,
                "max_gram",       max  != null ? max  : 0.0
        );
    }

    private SensorBeratResponse toResponse(SensorBeratData data) {
        return SensorBeratResponse.builder()
                .id(data.getId())
                .wadah(data.getWadah())
                .berat(data.getBerat())
                .satuan(data.getSatuan())
                .timestamp(data.getTimestamp())
                .build();
    }
}
