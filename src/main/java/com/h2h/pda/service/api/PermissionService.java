package com.h2h.pda.service.api;

import com.h2h.pda.entity.PermissionEntity;
import com.h2h.pda.pojo.permission.PermissionWrapper;
import com.h2h.pda.pojo.permission.Permissions;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PermissionService {
    boolean hasPermission(String credentialId, String userId, Permissions permission);

    PermissionEntity createPermission(String credentialId, String userId, PermissionEntity entity);

    Map<String, Set<Permissions>> effectivePermissions(String groupId);

    void delete(Integer permission);

    List<PermissionWrapper> getPermissions(String credentialId);

    PermissionEntity updatePermission(PermissionEntity permissionEntity);

    PermissionEntity getPermission(int permissionId);

    PermissionEntity getPermissionByCredentialAndUser(String credentialId, String userId);
}