package com.finflow.auth_users.dtos.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message = "First Name is required")
    private String firstName;
    @NotBlank(message = "Last Name is required")
    private String lastName;
    @NotBlank(message = "Phone number is required")
    @Size(min=10, message = "Phone Number must of length 10")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email format")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be of 8 character.")
    private String password;

//    @NotBlank(message = "Profile Picture is required")
//    private String profilePictureUrl;



}
