package com.mist.mist_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Login dto
 */
@Data
@AllArgsConstructor
public class LoginUserDto {

    @Email
    @NotNull
    private String email;
    @Size(min=6, max=30)
    private String password;
    @Size(min = 4, max = 4)
    private String otp;
}
