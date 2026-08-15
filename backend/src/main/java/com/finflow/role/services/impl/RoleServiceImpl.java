package com.finflow.role.services.impl;

import com.finflow.exceptions.AlreadyExistsException;
import com.finflow.exceptions.NotFoundException;
import com.finflow.response.ApiResponse;
import com.finflow.role.entity.Role;
import com.finflow.role.repository.RoleRepository;
import com.finflow.role.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    @Override
    public ApiResponse<Role> createRole(Role roleRequest) {
        if(roleRepository.findByName(roleRequest.getName()).isPresent()) {
            throw new AlreadyExistsException("Role already exists");
        }
        Role savedRole = roleRepository.save(roleRequest);

        return ApiResponse.<Role>builder()
                .status(HttpStatus.CREATED.value())
                .message("Role created successfully")
                .data(savedRole)
                .build();
    }

    @Override
    public ApiResponse<Role> updateRole(Role roleRequest) {
        Role role = roleRepository.findById(roleRequest.getId())
                .orElseThrow(
                        ()-> new NotFoundException("Role not found with id " + roleRequest.getId())
                );
        role.setName(roleRequest.getName());
        Role updatedRole = roleRepository.save(role);
        return ApiResponse.<Role>builder()
                .status(HttpStatus.OK.value())
                .message("Role updated successfully")
                .data(updatedRole)
                .build();
    }

    @Override
    public ApiResponse<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return ApiResponse.<List<Role>>builder()
                .status(HttpStatus.OK.value())
                .message("Roles retrieved successfully")
                .data(roles)
                .build();
    }

    @Override
    public ApiResponse<?> deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(
                ()-> new NotFoundException("Role not found with id " + roleId)
        );
        roleRepository.delete(role);
        return ApiResponse.builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("Role deleted Successfully")
                .build();
    }
}
