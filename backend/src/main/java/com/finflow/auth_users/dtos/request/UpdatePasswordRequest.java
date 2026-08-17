package com.finflow.auth_users.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePasswordRequest {

    @NotBlank(message = "Old password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    private String oldPassword;

    @NotBlank(message = "New password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    private String newPassword;

    @NotBlank(message = "Confirm password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    private String confirmPassword;
}