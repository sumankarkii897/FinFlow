package com.finflow.auth_users.services.impl;

import com.finflow.account.entity.Account;
import com.finflow.auth_users.dtos.request.ForgetPasswordRequest;
import com.finflow.auth_users.dtos.request.LoginRequest;
import com.finflow.auth_users.dtos.request.RegisterRequest;
import com.finflow.auth_users.dtos.request.ResetPasswordRequest;
import com.finflow.auth_users.dtos.response.LoginResponse;
import com.finflow.auth_users.entity.PasswordResetCode;
import com.finflow.auth_users.entity.User;
import com.finflow.auth_users.repository.PasswordResetCodeRepository;
import com.finflow.auth_users.repository.UserRepository;
import com.finflow.auth_users.services.AuthService;
import com.finflow.auth_users.services.CodeGenerator;
import com.finflow.enums.AccountType;
import com.finflow.enums.Currency;
import com.finflow.exceptions.AlreadyExistsException;
import com.finflow.exceptions.BadRequestException;
import com.finflow.exceptions.InvalidTransactionException;
import com.finflow.exceptions.NotFoundException;
import com.finflow.notification.dtos.request.NotificationRequest;
import com.finflow.notification.services.NotificationService;
import com.finflow.response.ApiResponse;
import com.finflow.role.entity.Role;
import com.finflow.role.repository.RoleRepository;
import com.finflow.security.JwtUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;
    private final NotificationService notificationService;
    private final CodeGenerator codeGenerator;
    private final PasswordResetCodeRepository passwordResetCodeRepository;

    @Value("${password.reset.link}")
    private String resetLink;
    @Override
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new BadCredentialsException("Invalid email or password!")
                );
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password!");
        }
        String token = jwtUtils.generateToken(user.getEmail());

        return ApiResponse.<LoginResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Login Successful")
                .data(
                        LoginResponse.builder()
                                .token(token)
                                .roles(user.getRoles().stream().map(Role::getName).toList())
                                .build()
                )
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<String> register(RegisterRequest request) {
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Email Already Exists");
        }
        Role defaultRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(()-> new NotFoundException("CUSTOMER ROLE NOT FOUND"));
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .roles(new ArrayList<>(List.of(defaultRole)))
                .active(true)
                .build();
       User savedUser =userRepository.save(user);
       // todo auto generate acc no for the user
//        Account savedAccount = accountService.createAccount(AccountType.SAVING, savedUser);
//        send a welcome email of the user
        Map<String,Object> map = new HashMap<>();
        map.put("name",savedUser.getFirstName());
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .recipient(savedUser.getEmail())
                .subject("Your New Bank Account Has Been Created")
//                .body("Welcome to FinFlow Bank.")
                .templateName("welcome")
                .templateVariables(map)
//                .type(NotificationType.EMAIL)
                .build();
        notificationService.sendEmail(notificationRequest,savedUser);

//        send acc details to user email
        Map<String,Object> accountVariables = new HashMap<>();
        accountVariables.put("name",savedUser.getFirstName());
//        accountVariables.put("accountNumber",savedAccount.getAccountNumber());
        accountVariables.put("accountType",AccountType.SAVING.name());
        accountVariables.put("currency", Currency.NRP);

        NotificationRequest accountCreatedEmail = NotificationRequest.builder()
                .recipient(savedUser.getEmail())
                .subject("Your New Bank Account Has Been Created ")
                .templateName("account-created")
                .templateVariables(accountVariables)
                .build();

        notificationService.sendEmail(accountCreatedEmail,savedUser);

        return ApiResponse.<String>builder()
                .status(HttpStatus.CREATED.value())
                .message("User registered successfully")
//                .data("Email of your account details has been sent to you.Your account number is :"
//                +savedAccount.getAccountNumber())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<?> forgetPassword(ForgetPasswordRequest request) {
        User user= userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new NotFoundException("User Not Found")
        );
        passwordResetCodeRepository.deleteByUserId(user.getId());

        String code = codeGenerator.generateCode();
        PasswordResetCode passwordResetCode = PasswordResetCode.builder()
                .code(code)
                .user(user)
                .expiryDate(calculateExpiryDate())
                .used(false)

                .build();
        passwordResetCodeRepository.save(passwordResetCode);

        // send email reset link
        Map<String,Object> templateVariable = new HashMap<>();
        templateVariable.put("name",user.getFirstName());
        templateVariable.put("resetLink",resetLink + code);

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .recipient(user.getEmail())
                .subject("Password Reset Code")
                .templateName("password-reset")
                .templateVariables(templateVariable)
                .build();
        notificationService.sendEmail(notificationRequest,user);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Password Reset code sent to your email")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<?> resetPassword(ResetPasswordRequest request) {
        String code = request.getCode();
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();
//        Find and validate code
        PasswordResetCode resetCode = passwordResetCodeRepository.findByCode(code).orElseThrow(
                () -> new BadRequestException("Invalid reset code")
        );
        // check expiration
        if(resetCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetCodeRepository.delete(resetCode);
            throw new BadRequestException("Reset code expired");
        }
        if(!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("Passwords do not match");
        }
        // update the user password
        User user = resetCode.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
//        delete the code immediately after successful use
        passwordResetCodeRepository.delete(resetCode);
        Map<String,Object> templateVariable = new HashMap<>();
        templateVariable.put("name",user.getFirstName());

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .recipient(user.getEmail())
                .subject("Password Updated Successfully")
                .templateName("password-updated-confirmation")
                .templateVariables(templateVariable)
                .build();

        notificationService.sendEmail(notificationRequest,user);


        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Password updated successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public ApiResponse<Void> addRole(Long userId, String roleName) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User Not Found")
        );
        Role role = roleRepository.findByName(roleName).orElseThrow(
                () -> new NotFoundException("Role Not Found")
        );
//        if(user.getRoles().contains(role)) {
//           throw new AlreadyExistsException("User already has this role");
//        }
        boolean hasRole = user.getRoles().stream()
                .anyMatch(r -> r.getId().equals(role.getId()));

        if (hasRole) {
            throw new NotFoundException("User does not have this role");
        }
        user.getRoles().add(role);
        userRepository.save(user);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Role added successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public ApiResponse<Void> removeRole(Long userId, String roleName) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User Not Found")
        );
        Role role = roleRepository.findByName(roleName).orElseThrow(
                () -> new NotFoundException("Role Not Found")
        );
        if (user.getRoles().size() == 1) {
            throw new InvalidTransactionException("User must have at least one role");
        }
//        if(!user.getRoles().contains(role)) {
//            throw new NotFoundException("Role Not Found");
//        }
        boolean hasRole = user.getRoles().stream()
                .anyMatch(r -> r.getId().equals(role.getId()));

        if (!hasRole) {
            throw new NotFoundException("User does not have this role");
        }
        user.getRoles().removeIf(r->r.getId().equals(role.getId()));
        userRepository.save(user);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Role removed successfully")
                .timestamp(LocalDateTime.now())
                .build();

    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusMinutes(15);
    }
}
