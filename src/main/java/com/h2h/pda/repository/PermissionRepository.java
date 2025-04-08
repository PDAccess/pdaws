package com.h2h.pda.repository;

import com.h2h.pda.entity.PermissionEntity;
import com.h2h.pda.pojo.permission.PermissionWrapper;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends CrudRepository<PermissionEntity, Integer> {
    @Query("SELECT permission from PermissionEntity as permission where permission.credential.credentialId = :credentialId and permission.user.userId = :userId")
    Optional<PermissionEntity> findPermission(@Param("credentialId") String credentialId, @Param("userId") String userId);

    @Query("SELECT permission, permission.whoCreate, permission.user, permission.credential from PermissionEntity as permission where permission.credential.credentialId = :credentialId")
    List<PermissionEntity> findByCredentialId(@Param("credentialId") String credentialId);

    @Query("SELECT new com.h2h.pda.pojo.permission.PermissionWrapper(g.user, p) FROM GroupUserEntity g LEFT OUTER JOIN PermissionEntity p on g.user.userId=p.user.userId WHERE g.id.groupId=:groupId AND (p IS NULL OR p.credential.credentialId=:credentialId)")
    List<PermissionWrapper> findPermissionsByGroupAndCredential(@Param("groupId") String groupId, @Param("credentialId") String credentialId);

    @Query("SELECT p from PermissionEntity p where p.credential.credentialId=:credentialId AND p.user.userId=:userId")
    Optional<PermissionEntity> findByCredentialAndUserId(@Param("credentialId") String credentialId, @Param("userId") String userId);
}
