package com.finflow.auth_users.services;

import com.finflow.auth_users.dtos.request.UpdatePasswordRequest;
import com.finflow.auth_users.dtos.response.UserResponse;
import com.finflow.auth_users.entity.User;
import com.finflow.response.ApiResponse;
import com.finflow.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    User getCurrentLoggedInUser();
    ApiResponse<UserResponse> getProfile();
    ApiResponse<PageResponse<UserResponse>> getAllUsers(Pageable pageable);
    ApiResponse<?> updatePassword(UpdatePasswordRequest updatePasswordRequest);
    ApiResponse<?> uploadProfilePicture(MultipartFile profilePicture);

}
