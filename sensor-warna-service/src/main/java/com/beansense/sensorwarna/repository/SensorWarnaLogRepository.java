package com.beansense.sensorwarna.repository;

import com.beansense.sensorwarna.entity.SensorWarnaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface SensorWarnaLogRepository extends JpaRepository<SensorWarnaLog, Long> {

    SensorWarnaLog findTopByOrderByIdDesc();

    List<SensorWarnaLog> findTop20ByOrderByTimestampDesc();

    // Dipakai oleh history page — dibatasi 200 baris terbaru (bukan seluruh tabel)
    List<SensorWarnaLog> findTop200ByOrderByTimestampDesc();

    @Query("SELECT s.hasilKlasifikasi AS klasifikasi, COUNT(s) AS jumlah FROM SensorWarnaLog s GROUP BY s.hasilKlasifikasi")
    List<Map<String, Object>> countGroupByKlasifikasi();

    @Query("SELECT COUNT(s) FROM SensorWarnaLog s WHERE s.hasilKlasifikasi = :klasifikasi")
    long countByKlasifikasi(@Param("klasifikasi") String klasifikasi);

    @Query("SELECT COUNT(s) FROM SensorWarnaLog s WHERE s.sensorWarna = :warna")
    Long countByWarna(@Param("warna") String warna);
}
