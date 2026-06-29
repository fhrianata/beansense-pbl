package com.beansense.sensorberat.controller;

import com.beansense.sensorberat.dto.SensorBeratRequest;
import com.beansense.sensorberat.dto.SensorBeratResponse;
import com.beansense.sensorberat.service.SensorBeratService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sensor-berat")
@RequiredArgsConstructor
public class SensorBeratController {

    private final SensorBeratService sensorBeratService;

    @Value("${sensor.api-key}")
    private String apiKey;

    // Endpoint untuk ESP32
    @PostMapping
    public ResponseEntity<SensorBeratResponse> terima(
            @RequestHeader("X-API-KEY") String key,
            @Valid @RequestBody SensorBeratRequest request) {

        if (!apiKey.equals(key)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "API KEY tidak valid");
        }

        SensorBeratResponse response = sensorBeratService.simpan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint untuk frontend
    @GetMapping
    public ResponseEntity<List<SensorBeratResponse>> getAll() {
        return ResponseEntity.ok(sensorBeratService.getAll());
    }

    @GetMapping("/latest")
    public ResponseEntity<SensorBeratResponse> getLatest() {
        SensorBeratResponse latest = sensorBeratService.getLatest();
        if (latest == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(latest);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<SensorBeratResponse>> getRecent() {
        return ResponseEntity.ok(sensorBeratService.getRecent());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(sensorBeratService.getStats());
    }
}
