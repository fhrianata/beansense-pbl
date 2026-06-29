package com.beansense.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean untuk hashing password user (dipakai saat simpan user baru
     * dan saat validasi login).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * auth-service adalah REST API stateless — tidak pakai session/cookie,
     * tidak pakai form login bawaan Spring Security.
     *
     * Endpoint /api/auth/** (login) dibuka untuk publik.
     * Endpoint /api/users/** (CRUD user) untuk sementara juga dibuka
     * di level auth-service — validasi role ADMIN dilakukan di web-service
     * berdasarkan JWT (atau bisa diperketat lagi nanti dengan JWT filter).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}