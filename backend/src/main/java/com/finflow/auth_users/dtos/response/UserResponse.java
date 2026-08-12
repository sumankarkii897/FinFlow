package com.finflow.auth_users.dtos.response;

import com.finflow.role.entity.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private String profilePictureUrl;

    private Boolean active;

    private List<Role> roles;

    private LocalDateTime createdAt;


}
