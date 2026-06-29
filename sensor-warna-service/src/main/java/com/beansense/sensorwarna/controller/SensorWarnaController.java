package com.beansense.sensorwarna.controller;

import com.beansense.sensorwarna.dto.SensorWarnaRequest;
import com.beansense.sensorwarna.dto.SensorWarnaResponse;
import com.beansense.sensorwarna.service.SensorWarnaService;
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
@RequestMapping("/api/sensor-warna")
@RequiredArgsConstructor
public class SensorWarnaController {

    private final SensorWarnaService sensorWarnaService;

    @Value("${sensor.api-key}")
    private String apiKey;

    // Endpoint untuk ESP32
    @PostMapping
    public ResponseEntity<SensorWarnaResponse> terima(
            @RequestHeader("X-API-KEY") String key,
            @Valid @RequestBody SensorWarnaRequest request) {

        if (!apiKey.equals(key)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "API KEY tidak valid");
        }

        SensorWarnaResponse response = sensorWarnaService.simpan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint untuk frontend
    @GetMapping
    public ResponseEntity<List<SensorWarnaResponse>> getAll() {
        return ResponseEntity.ok(sensorWarnaService.getAll());
    }

    @GetMapping("/latest")
    public ResponseEntity<SensorWarnaResponse> getLatest() {
        SensorWarnaResponse latest = sensorWarnaService.getLatest();
        if (latest == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(latest);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<SensorWarnaResponse>> getRecent() {
        return ResponseEntity.ok(sensorWarnaService.getRecent());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(sensorWarnaService.getStats());
    }
}
