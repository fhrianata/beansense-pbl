package com.beansense.auth.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * ROOT CAUSE FIX — penyebab utama "tidak bisa login".
 *
 * Sebelumnya entity User memakai @Enumerated(EnumType.STRING) langsung
 * di kolom "role". Itu artinya Hibernate memanggil Role.valueOf(...)
 * dengan nilai MENTAH dari database, dan Java enum valueOf() itu
 * case-SENSITIVE serta tidak menerima nilai yang tidak persis sama.
 *
 * Di database (lihat tabel users yang sudah ada / lama) nilai kolom
 * role disimpan lowercase ("admin", "klien") — bukan "ADMIN"/"OPERATOR".
 * Akibatnya, setiap kali auth-service mencoba membaca baris user untuk
 * proses login, Hibernate melempar:
 *   IllegalArgumentException: No enum constant ...Role.admin
 * Ini terjadi SEBELUM password sempat dicek sama sekali — jadi gagal
 * login terjadi walau username & password yang diketik sudah benar.
 * Exception ini tidak ditangani GlobalExceptionHandler (tidak ada
 * handler generic), jadi auth-service balas HTTP 500, lalu web-service
 * menangkapnya di blok "catch (Exception e)" dan tetap menampilkan
 * pesan generik "Username atau password salah" — padahal akar masalah
 * sebenarnya adalah error 500 ini, bukan kredensial yang salah.
 *
 * Converter ini membuat pemetaan role TIDAK case-sensitive, dan juga
 * memetakan nilai legacy yang masih ada di database (mis. "klien")
 * ke role yang valid, supaya data lama tidak membuat aplikasi crash.
 */
@Converter
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        return role == null ? null : role.name();
    }

    @Override
    public Role convertToEntityAttribute(String dbValue) {
        if (dbValue == null || dbValue.isBlank()) {
            return null;
        }

        String normalized = dbValue.trim().toUpperCase();

        switch (normalized) {
            case "ADMIN":
                return Role.ADMIN;
            case "OPERATOR":
                return Role.OPERATOR;
            // Nilai legacy dari prototype/seed data lama (mis. phpMyAdmin
            // manual insert) yang bukan ADMIN/OPERATOR dipetakan ke
            // OPERATOR sebagai default yang paling aman (akses paling
            // terbatas), supaya baris user lama tetap bisa login alih-alih
            // membuat seluruh request error 500.
            case "KLIEN":
            case "CLIENT":
            case "USER":
                return Role.OPERATOR;
            default:
                return Role.OPERATOR;
        }
    }
}
