package com.finflow.auth_users.entity;

import com.finflow.account.entity.Account;
import com.finflow.role.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String firstName;
@Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(unique = true, nullable = false)
    @Email(message = "Invalid format")
    private String email;

    @Column(nullable = false)
    private String password;

    private String profilePictureUrl;

    private Boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER)
            @JoinTable(
                    name = "user_roles",
                    joinColumns = @JoinColumn(name = "user_id"),
                    inverseJoinColumns = @JoinColumn(name = "role_id")
            )
    List<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Account> accounts;
    @CreationTimestamp
    private LocalDateTime createdAt=LocalDateTime.now();
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
