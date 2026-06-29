package com.beansense.sensorberat.repository;

import com.beansense.sensorberat.entity.SensorBeratData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SensorBeratDataRepository extends JpaRepository<SensorBeratData, Long> {

    SensorBeratData findTopByOrderByIdDesc();

    List<SensorBeratData> findTop20ByOrderByTimestampDesc();

    // Dipakai oleh history page — dibatasi 200 baris terbaru (bukan seluruh tabel)
    List<SensorBeratData> findTop200ByOrderByTimestampDesc();

    Optional<SensorBeratData> findTopByWadahOrderByIdDesc(String wadah);

    long countByWadah(String wadah);

    @Query("SELECT AVG(s.berat) FROM SensorBeratData s")
    Double avgBerat();

    @Query("SELECT MAX(s.berat) FROM SensorBeratData s")
    Double maxBerat();

    Optional<SensorBeratData> findFirstByWadahOrderByIdDesc(String wadah);
}
