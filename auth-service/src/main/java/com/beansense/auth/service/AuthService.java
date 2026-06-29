package com.beansense.auth.service;

import com.beansense.auth.dto.LoginRequest;
import com.beansense.auth.dto.LoginResponse;
import com.beansense.auth.entity.User;
import com.beansense.auth.repository.UserRepository;
import com.beansense.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Proses login: cek username & password, lalu generate JWT
     * jika kredensial valid.
     *
     * @throws BadCredentialsException jika username tidak ditemukan
     *         atau password tidak cocok.
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Username atau password salah"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Username atau password salah");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());

        return LoginResponse.builder()
                .token(token)
                .id(user.getId())
                .nama(user.getNama())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}