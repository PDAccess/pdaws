package com.h2h.pda.repository;

import com.h2h.pda.entity.CredentialEntity;
import com.h2h.pda.pojo.VaultCredentialParams;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CredentialRepository extends CrudRepository<CredentialEntity, String> {

    @Query("SELECT ce from CredentialEntity ce where ce.credentialId=:credentialId AND ce.deletedAt is NULL")
    Optional<CredentialEntity> findByIdAndNotDeleted(@Param("credentialId") String credentialId);

    @Query("SELECT ce, ce.group from CredentialEntity as ce where ce.deletedAt is NULL and ce.group.groupId = :groupId")
    List<CredentialEntity> findByGroup(@Param("groupId") String groupId);

    @Query("SELECT ce from CredentialEntity ce where ce.deletedAt is NULL and ce.group.groupId = :groupId and (:filter IS NULL OR ce.username LIKE %:filter%)")
    List<CredentialEntity> findByGroupId(@Param("groupId") String groupId, @Param("filter") String filter, Pageable pageable);

    @Query("SELECT ce from CredentialEntity ce where ce.deletedAt is NULL and ce.group.groupId = :groupId")
    List<CredentialEntity> findAllByGroupId(@Param("groupId") String groupId);

    @Query("SELECT new com.h2h.pda.pojo.VaultCredentialParams(c, p) from CredentialEntity c INNER JOIN PermissionEntity p ON c.credentialId=p.credential.credentialId where c.deletedAt is NULL and p.user.userId=:userId AND size(p.permissionsSet) > 0 AND (:filter IS NULL OR c.username LIKE %:filter%)")
    List<VaultCredentialParams> findByUserId(@Param("userId") String userId, @Param("filter") String filter, Pageable pageable);

    @Query("SELECT c from CredentialEntity c INNER JOIN PermissionEntity p ON c.credentialId=p.credential.credentialId where c.deletedAt is NULL and p.user.userId=:userId AND size(p.permissionsSet) > 0 AND (c.group != null AND c.group.groupId=:groupId)")
    List<CredentialEntity> findByUserIdAndGroupId(@Param("userId") String userId, @Param("groupId") String groupId);

    @Query("SELECT new com.h2h.pda.pojo.VaultCredentialParams(c, p) from CredentialEntity c INNER JOIN PermissionEntity p ON c.credentialId=p.credential.credentialId where c.deletedAt is NULL and p.user.userId=:userId AND size(p.permissionsSet) > 0 AND (:filter IS NULL OR c.username LIKE %:filter%) AND c.group.groupId in (:groupIds)")
    List<VaultCredentialParams> findByGroupIds(@Param("userId") String userId, @Param("groupIds") List<String> groupIds, @Param("filter") String filter, Pageable pageable);

    @Query("SELECT ce FROM CredentialEntity ce WHERE ce.deletedAt IS NULL AND ce.username=:credentialName AND ce.group.groupName=:groupName")
    Optional<CredentialEntity> findByNameAndGroup(@Param("credentialName") String credentialName, @Param("groupName") String groupName);

    @Query("SELECT count(DISTINCT c) from CredentialEntity c INNER JOIN PermissionEntity p ON c.credentialId=p.credential.credentialId where c.deletedAt is NULL and p.user.userId=:userId AND size(p.permissionsSet) > 0")
    long countOfCredentials(@Param("userId") String userId);

    @Query("SELECT count(DISTINCT c) from CredentialEntity c INNER JOIN PermissionEntity p ON c.credentialId=p.credential.credentialId where c.deletedAt is NULL and p.user.userId=:userId AND size(p.permissionsSet) > 0 AND c.group.groupId in (:groupIds)")
    long countOfCredentialsForAdmin(@Param("userId") String userId, @Param("groupIds") List<String> groupIds);

    @Query(value = "SELECT ce from CredentialEntity ce where ce.deletedAt is NULL AND ce.connectionUser IS NOT NULL")
    List<CredentialEntity> findManagedCredentialsByAccount();

}

