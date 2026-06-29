package com.beansense.notification_service.controller;

import com.beansense.notification_service.dto.AccountRequestDto;
import com.beansense.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * POST /api/notification/request-account
     * Dipanggil dari halaman login nginx saat user klik "Hubungi Admin".
     *
     * Body JSON:
     * {
     *   "nama": "Budi Santoso",
     *   "username": "budi.santoso",
     *   "email": "budi@example.com"
     * }
     */
    @PostMapping("/request-account")
    public ResponseEntity<Map<String, String>> requestAccount(
            @Valid @RequestBody AccountRequestDto dto) {

        log.info("Menerima request akun dari: {} ({})", dto.getNama(), dto.getEmail());
        notificationService.sendAccountRequest(dto);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Permintaan berhasil dikirim. Admin akan segera menghubungi Anda."
        ));
    }

    /**
     * Health check sederhana
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
