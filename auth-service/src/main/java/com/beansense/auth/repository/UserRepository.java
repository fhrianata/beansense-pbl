package com.beansense.auth.repository;

import com.beansense.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Dipakai saat proses login — cari user berdasarkan username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Dipakai saat tambah user baru — cek apakah username sudah dipakai.
     */
    boolean existsByUsername(String username);
}