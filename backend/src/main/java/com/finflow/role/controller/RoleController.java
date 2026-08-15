package com.finflow.role.controller;

import com.finflow.response.ApiResponse;
import com.finflow.role.entity.Role;
import com.finflow.role.services.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/roles")
@PreAuthorize("hasAuthority('ADMIN')")
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<ApiResponse<Role>> createRole(@Valid @RequestBody Role role) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleService.createRole(role));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Role>> updateRole(@Valid @RequestBody Role role) {
        return ResponseEntity.ok(roleService.updateRole(role));
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<ApiResponse<?>> deleteRole(@PathVariable Long roleId) {

        return ResponseEntity.ok(roleService.deleteRole(roleId));
    }
}
