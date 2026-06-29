package com.beansense.sensorberat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorBeratResponse {
    private Long id;
    private String wadah;
    private Double berat;
    private String satuan;
    private LocalDateTime timestamp;
}
