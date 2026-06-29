package com.beansense.auth.controller;

import com.beansense.auth.dto.LoginRequest;
import com.beansense.auth.dto.LoginResponse;
import com.beansense.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/login
     *
     * Dipanggil oleh web-service (lewat api-gateway) saat user
     * submit form login. Mengembalikan JWT + data user jika
     * username & password valid.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}