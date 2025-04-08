package com.h2h.pda.api.groups;

import com.h2h.pda.entity.PermissionEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.CredentialDetails;
import com.h2h.pda.pojo.permission.*;
import com.h2h.pda.service.api.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    @Autowired
    PermissionService permissionService;

    @GetMapping("{credentialId}")
    public ResponseEntity<List<PermissionWrapper>> getPermissions(@PathVariable String credentialId) {
        return ResponseEntity.ok(permissionService.getPermissions(credentialId));
    }

    // TODO: Entity Fix
    @PostMapping("{credentialId}")
    public ResponseEntity<PermissionEntity> updatePermission(@PathVariable String credentialId, @RequestBody PermissionParam permissionParam) {
        if (permissionParam.getPermission() == null) {
            PermissionEntity permissionEntity = new PermissionEntity();
            permissionEntity.setPermissionsSet(permissionParam.getValue() ? Collections.singleton(Permissions.valueOf(permissionParam.getKey())) : Collections.emptySet());
            permissionEntity = permissionService.createPermission(credentialId, permissionParam.getUser().getUserId(), permissionEntity);
            return ResponseEntity.ok(permissionEntity);
        }

        PermissionEntity permissionEntity = permissionService.getPermission(permissionParam.getPermission().getPermissionId());
        if (permissionEntity == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Set<Permissions> permissions = permissionEntity.getPermissionsSet();
        Permissions perm = Permissions.valueOf(permissionParam.getKey());
        if (permissionParam.getValue()) {
            permissions.add(perm);
        } else {
            permissions.remove(perm);
        }
        permissionEntity.setPermissionsSet(permissions);
        permissionService.updatePermission(permissionEntity);

        return ResponseEntity.ok(permissionEntity);
    }

    @PostMapping("all")
    public ResponseEntity<Void> saveAllPermissions(@RequestBody PermissionsParam permissionsParam) {
        Set<Permissions> permissions = new HashSet<>();
        for (PermissionBaseParam permissionBaseParam : permissionsParam.getPermissions()) {
            if (permissionBaseParam.isEnable()) {
                permissions.add(Permissions.valueOf(permissionBaseParam.getValue()));
            }
        }

        for (CredentialDetails credentialDetails : permissionsParam.getCredentials()) {
            for (UserEntity userEntity : permissionsParam.getUsers()) {
                PermissionEntity permissionEntity = permissionService.getPermissionByCredentialAndUser(credentialDetails.getId(), userEntity.getUserId());
                if (permissionEntity == null) {
                    permissionEntity = new PermissionEntity();
                    permissionEntity.setPermissionsSet(permissions);
                    permissionService.createPermission(credentialDetails.getId(), userEntity.getUserId(), permissionEntity);
                } else {
                    permissionEntity.setPermissionsSet(permissions);
                    permissionService.updatePermission(permissionEntity);
                }
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
