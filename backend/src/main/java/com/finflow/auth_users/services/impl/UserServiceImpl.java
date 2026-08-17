package com.finflow.auth_users.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.finflow.auth_users.dtos.request.UpdatePasswordRequest;
import com.finflow.auth_users.dtos.response.UserResponse;
import com.finflow.auth_users.entity.User;
import com.finflow.auth_users.repository.UserRepository;
import com.finflow.exceptions.BadRequestException;
import com.finflow.exceptions.NotFoundException;
import com.finflow.notification.dtos.request.NotificationRequest;
import com.finflow.notification.services.NotificationService;
import com.finflow.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final Cloudinary cloudinary;

    private final String uploadDir = "uploads/profile-pictures/";
    @Override
    public User getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new NotFoundException("User is not authenticated");
        }
        String email = authentication.getName();

        return userRepository.findByEmail(email).orElseThrow(
                ()-> new NotFoundException("User Not found")
        );
    }

    @Override
    public ApiResponse<UserResponse> getProfile() {
       User user = getCurrentLoggedInUser();
UserResponse userResponse = modelMapper.map(user, UserResponse.class);
      return ApiResponse.<UserResponse>builder()
              .status(HttpStatus.OK.value())
              .message("User profile fetched successfully")
              .data(userResponse)
              .timestamp(LocalDateTime.now())
              .build();
    }

    @Override
    public ApiResponse<Page<UserResponse>> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
       Page<UserResponse> userResponses = users.map(user -> modelMapper.map(user, UserResponse.class));
        return ApiResponse.<Page<UserResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("User list fetched successfully")
                .data(userResponses)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<?> updatePassword(UpdatePasswordRequest updatePasswordRequest) {

        User user = getCurrentLoggedInUser();
        String oldPassword = updatePasswordRequest.getOldPassword();
        String newPassword = updatePasswordRequest.getNewPassword();
        String confirmPassword = updatePasswordRequest.getConfirmPassword();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("Confirm password mismatch");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        Map<String, Object> passwordChange = new HashMap<>();
        passwordChange.put("name", user.getFirstName());
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .subject("Your Password has been changed Successfully")
                .templateName("password-change")
                .recipient(user.getEmail())
                .templateVariables(passwordChange)
                .build();

        notificationService.sendEmail(notificationRequest, user);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Password updated successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
//    public ApiResponse<?> uploadProfilePicture(MultipartFile profilePicture) {
//        User user = getCurrentLoggedInUser();
//        try{
//            Path uploadPath = Paths.get(uploadDir);
//            if(!Files.exists(uploadPath)){
//                Files.createDirectories(uploadPath);
//            }
//            if(user.getProfilePictureUrl()!=null && !user.getProfilePictureUrl().isEmpty()){
//                Path oldFile = Paths.get(user.getProfilePictureUrl());
//                if(Files.exists(oldFile)){
//                    Files.delete(oldFile);
//                }
//            }
//             // Generating a unique file name
//            String originalFilename = profilePicture.getOriginalFilename();
//            String fileExtension = "";
//            if(originalFilename !=null && originalFilename.contains(".")){
//                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
//
//            }
//            String newFileName = UUID.randomUUID().toString() + "." + fileExtension;
//            Path filePath = uploadPath.resolve(newFileName);
//
//            Files.copy(profilePicture.getInputStream(), filePath);
//            String fileUrl = uploadDir + newFileName;
//
//            user.setProfilePictureUrl(fileUrl);
//
//            userRepository.save(user);
//
//            return ApiResponse.builder()
//                    .status(HttpStatus.OK.value())
//                    .message("Profile picture uploaded successfully")
//                    .data(fileUrl)
//                    .build();
//        }
//        catch (IOException e) {
//            throw new RuntimeException(e.getMessage());
//
//        }
//
//    }

    @Transactional
    public ApiResponse<?> uploadProfilePicture(MultipartFile file) {
        User user = getCurrentLoggedInUser();

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        try {
            if (user.getPublicId() != null && !user.getPublicId().isBlank()) {
                cloudinary.uploader().destroy(
                        user.getPublicId(),
                        ObjectUtils.emptyMap()
                );
            }

            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.emptyMap()
            );

            user.setProfilePictureUrl(
                    uploadResult.get("secure_url").toString()
            );

            user.setPublicId(
                    uploadResult.get("public_id").toString()
            );

            userRepository.save(user);

            return ApiResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Profile picture uploaded successfully")
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile picture", e);
        }
    }



}
