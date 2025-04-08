package com.h2h.pda.repository;

import com.h2h.pda.entity.CredentialRequestEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CredentialRequestRepository extends CrudRepository<CredentialRequestEntity, Integer> {

    @Query(value = "SELECT c FROM CredentialRequestEntity c WHERE c.respondedAt IS NOT NULL AND c.credentialEntity.credentialId=:credentialId AND c.isApproval IS :isApproval")
    List<CredentialRequestEntity> findRespondedRequestsByCredentialAndApproval(@Param("credentialId") String credentialId, @Param("isApproval") boolean isApproval);

    @Query(value = "SELECT c FROM CredentialRequestEntity c WHERE c.respondedAt IS NULL AND c.credentialEntity.credentialId=:credentialId")
    List<CredentialRequestEntity> findNotRespondedRequestsByCredential(@Param("credentialId") String credentialId);

    @Query(value = "SELECT c FROM CredentialRequestEntity c WHERE c.respondedAt IS NULL AND c.credentialEntity.credentialId=:credentialId AND c.requestingUser.userId=:userId")
    CredentialRequestEntity findNotRespondedRequestsByCredentialAndUser(@Param("credentialId") String credentialId, @Param("userId") String userId);

    @Query(value = "SELECT c FROM CredentialRequestEntity c WHERE c.respondedAt IS NULL AND c.id=:requestId")
    CredentialRequestEntity findNotRespondedRequestById(@Param("requestId") int requestId);

    @Query(value = "SELECT COUNT(c) FROM CredentialRequestEntity c WHERE c.respondedAt IS NULL AND c.credentialEntity.credentialId=:credentialId")
    long countOfWaitRequest(@Param("credentialId") String credentialId);

    @Query(value = "SELECT COUNT(c) FROM CredentialRequestEntity c WHERE c.respondedAt IS NOT NULL AND c.credentialEntity.credentialId=:credentialId AND c.isApproval IS TRUE")
    long countOfApprovedRequest(@Param("credentialId") String credentialId);

    @Query(value = "SELECT COUNT(c) FROM CredentialRequestEntity c WHERE c.respondedAt IS NOT NULL AND c.credentialEntity.credentialId=:credentialId AND c.isApproval IS FALSE")
    long countOfNotApprovedRequest(@Param("credentialId") String credentialId);
}