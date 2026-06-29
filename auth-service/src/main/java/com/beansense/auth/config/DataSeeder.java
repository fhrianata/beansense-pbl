package com.beansense.auth.config;

import com.beansense.auth.entity.Role;
import com.beansense.auth.entity.User;
import com.beansense.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Membuat 1 akun admin default secara otomatis saat aplikasi
 * pertama kali dijalankan — HANYA jika tabel "users" masih kosong.
 *
 * Kredensial default (WAJIB diganti setelah login pertama kali):
 *   username : admin
 *   password : admin123
 *
 * Jika tabel sudah punya data (>= 1 user), seeder ini tidak
 * melakukan apa-apa.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // sudah ada data, tidak perlu seeding
        }

        User admin = User.builder()
                .nama("Administrator")
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .email("admin@beansense.com")
                .build();

        userRepository.save(admin);

        log.info("==========================================================");
        log.info(" [DataSeeder] Akun admin default berhasil dibuat:");
        log.info("   username : admin");
        log.info("   password : admin123");
        log.info(" Segera login & ganti password melalui menu User Management.");
        log.info("==========================================================");
    }
}