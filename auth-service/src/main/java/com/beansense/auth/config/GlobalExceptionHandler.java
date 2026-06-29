package com.beansense.auth.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Dilempar saat username tidak ditemukan atau password tidak cocok.
     * Sengaja menggunakan pesan generik (tidak membedakan "username tidak
     * ada" vs "password salah") untuk mencegah username enumeration.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildBody(ex.getMessage()));
    }

    /**
     * Dilempar saat validasi @Valid pada LoginRequest gagal
     * (misal username/password kosong).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Data tidak valid");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildBody(message));
    }

    /**
     * Fallback untuk error tak terduga (mis. bug mapping data seperti
     * kasus Role di atas). Sebelumnya tidak ada handler generic, jadi
     * error apapun selain BadCredentials/Validation keluar sebagai
     * HTTP 500 tanpa pesan jelas, dan di sisi web-service malah
     * ditampilkan sebagai "Username atau password salah" — menyesatkan.
     * Sekarang pesannya dibuat jelas beda dari kredensial salah.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildBody("Terjadi kesalahan pada server auth-service: " + ex.getMessage()));
    }

    private Map<String, Object> buildBody(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", message);
        return body;
    }
}