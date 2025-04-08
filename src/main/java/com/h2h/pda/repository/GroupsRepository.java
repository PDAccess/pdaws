package com.h2h.pda.repository;

import com.h2h.pda.entity.GroupUserEntity;
import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.pojo.group.GroupCategory;
import com.h2h.pda.pojo.group.GroupRole;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupsRepository extends CrudRepository<GroupsEntity, String> {

    @Query("SELECT c FROM GroupsEntity c left outer join c.members m WHERE m.user.userId=:userId and c.deletedAt IS NULL and m.membershipRole in (:roles)")
    List<GroupsEntity> findByUserGroups(@Param("userId") String userId, @Param("roles") GroupRole[] roles);

    @Query("SELECT c FROM GroupsEntity c left outer join c.members m WHERE m.user.userId=:userId and c.deletedAt IS NULL and m.membershipRole in (:roles)")
    List<GroupsEntity> findByUserGroups(@Param("userId") String userId, @Param("roles") GroupRole[] roles, Pageable page);

    @Query("SELECT c FROM GroupsEntity c left outer join c.members m WHERE m.user.userId=:userId and c.deletedAt IS NULL and (m.expireDate IS NULL or m.expireDate > now()) and m.membershipRole in (:roles) and (:filter IS NULL OR c.groupName LIKE %:filter% OR c.description LIKE %:filter%)")
    List<GroupsEntity> findByUserGroups(@Param("userId") String userId, @Param("roles") GroupRole[] roles, @Param("filter") String filter, Pageable page);

    List<GroupsEntity> getTop5ByDeletedAtIsNullOrderByCreatedAtDesc();

    long countByDeletedAtIsNull();

    @Query(value = "SELECT g FROM GroupsEntity g WHERE g.groupId=:groupId AND g.deletedAt IS NULL")
    Optional<GroupsEntity> findByIdAndNotDeleted(@Param("groupId") String groupId);

    @Query(value = "SELECT g FROM GroupsEntity g WHERE g.groupName=:groupName AND g.deletedAt IS NULL")
    Optional<GroupsEntity> findByNameAndNotDeleted(@Param("groupName") String groupName);

    @Query(value = "SELECT g FROM GroupsEntity g WHERE g.deletedAt IS NULL AND g.groupCategory=:groupCategory")
    List<GroupsEntity> findByGroupTypeAndNotDeleted(@Param("groupCategory") GroupCategory groupCategory);

    @Query(value = "SELECT count(gue) FROM GroupUserEntity gue WHERE gue.group.groupId= :groupId and gue.group.deletedAt IS NULL")
    long countOfMembers(@Param("groupId") String groupId);

    @Query(value = "SELECT count(gse) FROM GroupServiceEntity gse WHERE gse.group.groupId= :groupId and gse.service.deletedAt is NULL and gse.group.deletedAt IS NULL")
    long countOfServices(@Param("groupId") String groupId);

    @Query(value = "SELECT count(c) FROM CredentialEntity c WHERE c.group.groupId = :groupId and c.group.deletedAt IS NULL")
    long countOfCredentials(@Param("groupId") String groupId);

    @Query(value = "SELECT count(p) FROM PolicyEntity p WHERE p.group.groupId = :groupId and p.group.deletedAt IS NULL")
    long countOfPolicies(@Param("groupId") String groupId);

    @Query(value = "SELECT count(a) FROM AlarmEntity a WHERE a.groupsEntity.groupId =:groupId and a.groupsEntity.deletedAt IS NULL")
    long countOfAlarms(@Param("groupId") String groupId);

    @Query(value = "SELECT count(gue) FROM GroupUserEntity gue WHERE gue.user.userId=:userId and gue.membershipRole=:role and (gue.expireDate IS NULL or gue.expireDate > now()) and gue.group.deletedAt IS NULL")
    long countByUsers(@Param("userId") String userId, @Param("role") GroupRole role);

    @Query(value = "SELECT gue FROM GroupUserEntity gue WHERE gue.group.deletedAt IS NULL AND gue.expireDate IS NOT NULL AND DAY(gue.expireDate-now())=:remainDay")
    List<GroupUserEntity> findGroupMemberByExpire(@Param("remainDay") int remainDay);

    @Query(value = "SELECT g FROM GroupsEntity g WHERE g.parent IS NOT NULL AND g.parent.groupId=:groupId AND g.deletedAt IS NULL")
    List<GroupsEntity> findChildGroups(@Param("groupId") String groupId);

    @Query("SELECT c FROM GroupsEntity c left outer join c.members m WHERE m.user.userId=:userId and c.deletedAt IS NULL and c.parent IS NULL and (m.expireDate IS NULL or m.expireDate > now()) and m.membershipRole in (:roles)")
    List<GroupsEntity> findParentGroupsByUser(@Param("userId") String userId, @Param("roles") GroupRole[] roles);
}
