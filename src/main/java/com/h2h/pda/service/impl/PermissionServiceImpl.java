package com.h2h.pda.service.impl;

import com.h2h.pda.entity.CredentialEntity;
import com.h2h.pda.entity.GroupUserEntity;
import com.h2h.pda.entity.PermissionEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.permission.PermissionWrapper;
import com.h2h.pda.pojo.permission.Permissions;
import com.h2h.pda.repository.CredentialRepository;
import com.h2h.pda.repository.PermissionRepository;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.PermissionService;
import com.h2h.pda.service.api.UsersOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    CredentialRepository credentialRepository;

    @Autowired
    GroupOps groupOps;

    @Autowired
    UsersOps usersOps;

    @Override
    public boolean hasPermission(String credentialId, String userId, Permissions permission) {
        Optional<PermissionEntity> id = permissionRepository.findPermission(credentialId, userId);
        return id.map(permissionEntity -> permissionEntity.getPermissionsSet().contains(permission)).orElse(false);

    }

    @Override
    public PermissionEntity createPermission(String accountId, String userId, PermissionEntity permissionEntity) {
        Optional<CredentialEntity> optionalCredentialEntity = credentialRepository.findById(accountId);
        Optional<UserEntity> optionalUserEntity = usersOps.byId(userId);
        if (optionalCredentialEntity.isPresent() && optionalUserEntity.isPresent()) {
            PermissionEntity entity = new PermissionEntity();
            entity.setCreatedAt(Timestamp.from(Instant.now()));
            entity.setPermissionsSet(permissionEntity.getPermissionsSet());
            entity.setCredential(optionalCredentialEntity.get());
            entity.setUser(optionalUserEntity.get());
            entity.setWhoCreate(optionalUserEntity.get());
            return permissionRepository.save(entity);
        }
        return null;
    }

    @Override
    public Map<String, Set<Permissions>> effectivePermissions(String accountId) {

        Iterable<PermissionEntity> id = permissionRepository.findByCredentialId(accountId);

        Map<String, List<PermissionEntity>> collect =
                StreamSupport.stream(id.spliterator(), false).collect(Collectors.groupingBy(f -> f.getUser().getUserId()));

        HashMap<String, Set<Permissions>> map = new HashMap<>();

        for (String key : collect.keySet()) {
            Optional<Set<Permissions>> first = collect.get(key).stream().map(f -> f.getPermissionsSet()).findFirst();
            if (first.isPresent())
                map.put(key, first.get());
        }

        return map;
    }

    @Override
    public void delete(Integer permissionId) {
        permissionRepository.deleteById(permissionId);
    }

    @Override
    public List<PermissionWrapper> getPermissions(String credentialId) {
        Optional<CredentialEntity> optionalCredentialEntity = credentialRepository.findById(credentialId);
        if (optionalCredentialEntity.isPresent()) {
            List<PermissionWrapper> permissionWrappers = new ArrayList<>();
            CredentialEntity credentialEntity = optionalCredentialEntity.get();
            Iterable<GroupUserEntity> groupUserEntities = groupOps.effectiveMembers(credentialEntity.getGroup().getGroupId());
            List<PermissionEntity> permissionEntities = permissionRepository.findByCredentialId(credentialEntity.getCredentialId());
            for (GroupUserEntity groupUserEntity : groupUserEntities) {
                Optional<PermissionEntity> optionalPermissionEntity = permissionEntities.stream().filter(p -> p.getUser().getUserId().equals(groupUserEntity.getId().getUserId())).findFirst();
                permissionWrappers.add(new PermissionWrapper(groupUserEntity.getUser(), optionalPermissionEntity.orElse(null)));
            }
            return permissionWrappers;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public PermissionEntity updatePermission(PermissionEntity permissionEntity) {
        return permissionRepository.save(permissionEntity);
    }

    @Override
    public PermissionEntity getPermission(int permissionId) {
        Optional<PermissionEntity> optionalPermissionEntity = permissionRepository.findById(permissionId);
        return optionalPermissionEntity.orElse(null);
    }

    @Override
    public PermissionEntity getPermissionByCredentialAndUser(String credentialId, String userId) {
        Optional<PermissionEntity> optionalPermissionEntity = permissionRepository.findByCredentialAndUserId(credentialId, userId);
        return optionalPermissionEntity.orElse(null);
    }

}
