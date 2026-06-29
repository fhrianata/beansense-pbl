package com.beansense.sensorwarna.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorWarnaResponse {
    private Long id;
    private String sensorWarna;
    private String hasilKlasifikasi;
    private LocalDateTime timestamp;
}
