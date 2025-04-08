package com.h2h.pda.service.impl;

import com.h2h.pda.entity.CredentialEntity;
import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.repository.CredentialRepository;
import com.h2h.pda.service.api.VaultOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VaultOpsImpl implements VaultOps {

    @Autowired
    CredentialRepository credentialRepository;

    @Override
    public String newCredential(String groupId, String userId, CredentialEntity credential) {
        credential.setGroup(new GroupsEntity().setGroupId(groupId));
        credential.setCreatedAt(Timestamp.from(Instant.now()));
        credential.setUpdatedAt(Timestamp.from(Instant.now()));
        credential.setDeletedAt(null);
        credential.setCredentialId(UUID.randomUUID().toString());
        credential.setWhoCreate(new UserEntity().setUserId(userId));
        CredentialEntity save = credentialRepository.save(credential);
        return save.getCredentialId();
    }

    @Override
    public List<CredentialEntity> byGroup(String groupId) {
        return credentialRepository.findByGroup(groupId);
    }

    @Override
    public Optional<CredentialEntity> byId(String id) {
        return credentialRepository.findById(id);
    }

    @Override
    public void delete(String credentialId) {
        Optional<CredentialEntity> id = credentialRepository.findById(credentialId);
        if (id.isPresent()) {
            CredentialEntity credentialEntity = id.get();
            credentialEntity.setDeletedAt(Timestamp.from(Instant.now()));
            credentialRepository.save(credentialEntity);
        }
    }
}
