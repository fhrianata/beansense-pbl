package com.beansense.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nama", nullable = false, length = 100)
    private String nama;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Convert(converter = RoleConverter.class)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "email", length = 100)
    private String email;

    /**
     * FIX: kolom "status" ada di tabel users (enum 'aktif','suspend')
     * tapi tidak ada di entity — akibatnya ddl-auto: validate langsung
     * gagal saat startup auth-service, dan login tidak pernah sampai
     * ke tahap cek password.
     *
     * Ditambahkan sebagai String sederhana (bukan enum Java) supaya
     * Hibernate bisa membaca nilai "aktif"/"suspend" dari DB apa adanya
     * tanpa perlu konverter tambahan.
     */
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        // Default status "aktif" saat user baru dibuat via DataSeeder
        if (this.status == null) {
            this.status = "aktif";
        }
    }
}
