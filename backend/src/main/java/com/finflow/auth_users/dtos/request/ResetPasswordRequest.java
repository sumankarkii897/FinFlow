package com.finflow.auth_users.dtos.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
//    @NotBlank(message = "Email is required.")
//    @Email(message = "Invalid email format.")
//    private String email;

    @NotBlank(message = "Code is required.")
    private String code;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least of 8 character")
    private String newPassword;


    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Confirm Password must be at least of 8 character")
    private String confirmPassword;


}
