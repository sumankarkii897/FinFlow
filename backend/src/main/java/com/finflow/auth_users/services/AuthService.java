package com.finflow.auth_users.services;

import com.finflow.auth_users.dtos.request.ForgetPasswordRequest;
import com.finflow.auth_users.dtos.request.LoginRequest;
import com.finflow.auth_users.dtos.request.RegisterRequest;
import com.finflow.auth_users.dtos.request.ResetPasswordRequest;
import com.finflow.auth_users.dtos.response.LoginResponse;
import com.finflow.auth_users.dtos.response.UserResponse;
import com.finflow.response.ApiResponse;

public interface AuthService {
    ApiResponse<LoginResponse> login(LoginRequest request);

    ApiResponse<String> register(RegisterRequest request);

    ApiResponse<?> forgetPassword(ForgetPasswordRequest request);

    ApiResponse<?> resetPassword(ResetPasswordRequest request);

    ApiResponse<Void> addRole(Long userId, String roleName);
    ApiResponse<Void> removeRole(Long userId, String roleName);



}
