package com.finflow.auth_users.controller;
import com.finflow.auth_users.dtos.request.ForgetPasswordRequest;
import com.finflow.auth_users.dtos.request.LoginRequest;
import com.finflow.auth_users.dtos.request.RegisterRequest;
import com.finflow.auth_users.dtos.request.ResetPasswordRequest;
import com.finflow.auth_users.dtos.response.LoginResponse;
import com.finflow.auth_users.services.AuthService;
import com.finflow.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgotPassword(@Valid @RequestBody ForgetPasswordRequest forgetPasswordRequest) {
        return ResponseEntity.ok(authService.forgetPassword(forgetPasswordRequest));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        return ResponseEntity.ok(authService.resetPassword(resetPasswordRequest));
    }

    @PostMapping("/addRole/{userId}")
    public ResponseEntity<ApiResponse<Void>> addRole(@PathVariable Long userId, @RequestParam String roleName) {
        return ResponseEntity.ok(authService.addRole(userId, roleName));
    }

    @PostMapping("/removeRole/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeRole(@PathVariable Long userId,@RequestParam String roleName) {
        return ResponseEntity.ok(authService.removeRole(userId, roleName));
    }

}
