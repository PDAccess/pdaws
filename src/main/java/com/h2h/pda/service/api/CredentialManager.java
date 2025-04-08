package com.h2h.pda.service.api;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.*;
import com.h2h.pda.pojo.vault.CredentialCounter;
import com.h2h.pda.pojo.vault.CredentialRequestCounter;
import org.springframework.data.domain.PageRequest;
import org.springframework.vault.support.VaultResponse;

import java.util.List;
import java.util.Optional;

public interface CredentialManager {
    CredentialManagerResponse verify(VerifyRequest vr);

    CredentialManagerResponse changePassword(VerifyRequest vr);

    void pushChangeRequest(CredentialManagerWrapper request);

    CredentialEntity newCredential(CredentialEntity credentialEntity, CredentialParams credentialParams);

    List<CredentialEntity> getCredentials(String groupId, String filter, PageRequest pageRequest);

    List<CredentialEntity> getAllCredentials(String groupId);

    CredentialEntity getCredential(String credentialId);

    List<VaultCredentialParams> getCredentialsByUserId(String userId, String filter, PageRequest pageRequest);

    List<VaultCredentialParams> getCredentialsByGroupIds(String userId, List<String> groupIds, String filter, PageRequest pageRequest);

    List<CredentialEntity> getCredentialsByUserIdAndGroupId(String userId, String groupId);

    CredentialParams breakCredential(String credentialId, String reason, String ipAddress, boolean isShare);

    CredentialParams breakCredentialUsingRootToken(String credentialId, String reason, UserEntity userEntity, List<UserEntity> userEntities, String ipAddress, boolean isShare);

    BreakTheGlassShareEntity getSharedLink(String shareId);

    BreakTheGlassShareEntity createSharedLink(BreakTheGlassShareEntity shareEntity);

    void revokeSharedLink(String shareId);

    CredentialEntity updateCredential(CredentialEntity credentialEntity);

    void deleteCredential(String credentialId);

    BreakTheGlassEntity getLastCredentialBreak(String credentialId);

    AutoCredentialsHistoryEntity getLastCredentialChange(String credentialId);

    List<AutoCredentialsHistoryEntity> getChangeHistory(String credentialId, PageRequest pageRequest);

    void checkoutCredential(String credentialId);

    Optional<CredentialEntity> getCredentialByNameAndGroup(String credentialName, String groupName);

    VaultResponse updateCredentialData(CredentialParams credentialParams);

    CredentialCounter credentialCounters(String userId);

    CredentialRequestCounter credentialRequestCounters(String credentialId);

    CredentialEntity getMostParentCredential(CredentialEntity credentialEntity);

    List<CredentialRequestEntity> getNotRespondedCredentialRequests(String credentialId);

    List<CredentialRequestEntity> getRespondedCredentialRequests(String credentialId, boolean isApproved);

    CredentialRequestEntity requestCredential(String credentialId);

    CredentialRequestEntity responseCredentialRequest(int requestId, String ipAddress, boolean isApproved);

    boolean isAuthorizedForApproveCredentialRequests(String credentialId, String userId);

    CredentialRequestEntity getCredentialRequestById(int requestId);

    List<CredentialEntity> getManagedCredentials();
}
