package com.beansense.sensorberat.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorBeratRequest {

    // Field "wadah" dari ESP32: "MATANG" atau "MENTAH"
    @NotBlank(message = "wadah wajib diisi")
    private String wadah;

    @NotNull(message = "berat wajib diisi")
    @DecimalMin(value = "0.0", message = "berat tidak boleh negatif")
    private Double berat;

    private String satuan = "gram";
}
