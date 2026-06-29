package com.beansense.sensorwarna.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_warna")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorWarnaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_warna", nullable = false, length = 50)
    private String sensorWarna;

    @Column(name = "hasil_klasifikasi", nullable = false, length = 50)
    private String hasilKlasifikasi;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
