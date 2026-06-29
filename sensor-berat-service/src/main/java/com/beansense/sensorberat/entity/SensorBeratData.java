package com.beansense.sensorberat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_berat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorBeratData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Wadah tujuan biji: "MATANG" atau "MENTAH"
    @Column(name = "wadah", length = 20)
    private String wadah;

    @Column(name = "berat", nullable = false)
    private Double berat;

    @Column(name = "satuan", length = 10)
    private String satuan;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
