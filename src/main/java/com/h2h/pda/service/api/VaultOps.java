package com.h2h.pda.service.api;

import com.h2h.pda.entity.CredentialEntity;

import java.util.List;
import java.util.Optional;

public interface VaultOps {
    String newCredential(String groupId, String userId, CredentialEntity credential);

    List<CredentialEntity> byGroup(String groupId);

    Optional<CredentialEntity> byId(String id);

    void delete(String credentialId);
}
