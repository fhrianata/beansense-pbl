package com.beansense.auth.dto;

import com.beansense.auth.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String nama;
    private String username;
    private String role;
    private String email;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        UserResponse res = new UserResponse();
        res.id = user.getId();
        res.nama = user.getNama();
        res.username = user.getUsername();
        res.role = user.getRole().name();
        res.email = user.getEmail();
        res.createdAt = user.getCreatedAt();
        return res;
    }
}