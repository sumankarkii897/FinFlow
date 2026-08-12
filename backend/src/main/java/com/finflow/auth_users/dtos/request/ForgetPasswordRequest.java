package com.finflow.auth_users.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ForgetPasswordRequest {
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
