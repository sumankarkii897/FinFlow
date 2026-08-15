package com.finflow.role.services;

import com.finflow.response.ApiResponse;
import com.finflow.role.entity.Role;

import java.util.List;

public interface RoleService {

    ApiResponse<Role> createRole(Role roleRequest);
    ApiResponse<Role> updateRole(Role roleRequest);
    ApiResponse<List<Role>> getAllRoles();
    ApiResponse<?> deleteRole(Long roleId);
}
