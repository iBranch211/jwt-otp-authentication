package com.mist.mist_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Check otp dto
 */
@Data
@AllArgsConstructor
public class OtpCheckDto {

    @Email
    @NotNull
    private String email;
    @NotNull
    @Size(min=4, max=4)
    private String otp;
}
