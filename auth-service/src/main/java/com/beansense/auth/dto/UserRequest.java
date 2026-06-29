package com.beansense.auth.dto;

import com.beansense.auth.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = "Nama wajib diisi")
    private String nama;

    @NotBlank(message = "Username wajib diisi")
    private String username;

    // Password opsional saat edit (kosong = tidak diubah)
    private String password;

    @NotNull(message = "Role wajib dipilih")
    private Role role;

    private String email;
}