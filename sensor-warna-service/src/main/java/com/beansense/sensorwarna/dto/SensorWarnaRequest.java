package com.beansense.sensorwarna.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorWarnaRequest {

    // Nilai dari ESP32: MERAH, HIJAU, atau TIDAK DIKENALI (case-insensitive)
    // Validasi ketat dilakukan di service layer setelah toUpperCase()
    @NotBlank(message = "sensor_warna wajib diisi")
    private String sensor_warna;
}
