package com.finflow.auth_users.controller;

import com.finflow.auth_users.dtos.request.UpdatePasswordRequest;
import com.finflow.auth_users.dtos.response.UserResponse;
import com.finflow.auth_users.services.impl.UserService;
import com.finflow.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(
               userService.getAllUsers(pageable)
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(){
        return ResponseEntity.ok(userService.getProfile());
    }

    @PutMapping("/update-password")
    public ResponseEntity<ApiResponse<?>> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest){
        return ResponseEntity.ok(userService.updatePassword(updatePasswordRequest));
    }

    @PutMapping("/profile-picture")
    public ResponseEntity<ApiResponse<?>> uploadProfilePicture(@Valid @RequestParam("profilePicture") MultipartFile profilePicture){
        return ResponseEntity.ok(userService.uploadProfilePicture(profilePicture));
    }

}
