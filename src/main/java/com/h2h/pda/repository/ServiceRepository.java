package com.h2h.pda.repository;

import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.group.GroupRole;
import com.h2h.pda.pojo.service.ServiceType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends CrudRepository<ServiceEntity, String> {
    @Query(value = "SELECT c FROM ServiceEntity c WHERE c.deletedAt IS NULL")
    List<ServiceEntity> findByNotDeleted();

    List<ServiceEntity> getTop5ByDeletedAtIsNullOrderByCreatedAtDesc();

    @Query("SELECT c FROM ServiceEntity c WHERE c.inventoryId IN (SELECT p.inventoryId FROM SessionEntity p " +
            "WHERE p.sessionId IN (SELECT ac.sessionEntity.sessionId  FROM ActionEntity ac" +
            " group by ac.sessionEntity.sessionId ORDER BY COUNT(ac.sessionEntity.sessionId) DESC) AND p.endTime IS NULL)")
    List<ServiceEntity> getMostActiveServices(Pageable pageable);

    @Query(value = "SELECT c FROM ServiceEntity c WHERE c.inventoryId=?1 AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    Optional<ServiceEntity> findServiceById(String id);

    Optional<ServiceEntity> findByName(String name);

    @Query(value = "UPDATE ServiceEntity s SET s.lastAccessTime=:lastAccessTime WHERE s.inventoryId=:serviceId")
    @Modifying
    @Transactional
    void updateLastAccessByServiceId(@Param("serviceId") String serviceId, @Param("lastAccessTime") Timestamp lastAccessTime);

    @Query(value = "Select DISTINCT s FROM ServiceEntity s left outer join s.memberOf gse left outer join gse.group ge left outer join ge.members gue WHERE " +
            "(:filter IS NULL OR (LOWER(s.name) LIKE %:filter% OR LOWER(s.description) LIKE %:filter% OR s.ipAddress LIKE %:filter2%)) AND " +
            "(gue.user.userId = :userId and ge.deletedAt IS NULL and s.deletedAt IS NULL and (gue.expireDate IS NULL or gue.expireDate > now()))")
    List<ServiceEntity> findByUserIdAndTenantIdAndFilter(@Param("userId") String userId, @Param("filter") String filter, @Param("filter2") String filter2, Pageable page);

    @Query(value = "Select DISTINCT s FROM ServiceEntity s left outer join s.memberOf gse left outer join gse.group ge left outer join ge.members gue WHERE " +
            "(:filter IS NULL OR (LOWER(s.name) LIKE %:filter% OR LOWER(s.description) LIKE %:filter% OR s.ipAddress LIKE %:filter2%)) AND " +
            "(gue.user.userId = :userId and ge.deletedAt IS NULL and s.deletedAt IS NULL and (gue.membershipRole IS NOT NULL and gue.membershipRole=:role) and (gue.expireDate IS NULL or gue.expireDate > now()))")
    List<ServiceEntity> findByUserIdAndTenantIdAndFilter(@Param("userId") String userId, @Param("role") GroupRole role, @Param("filter") String filter, @Param("filter2") String filter2, Pageable page);

    //    @Query(value = "Select s FROM ServiceEntity s WHERE s.deletedAt IS NULL AND s.hasAgent IS TRUE AND (s.lastAccessTime IS NOT NULL AND s.lastAccessTime>:time) AND (:filter IS NULL OR (LOWER(s.name) LIKE %:filter% OR s.ipAddress LIKE :filter2%)) AND (s.inventoryId IN " +
//            "(SELECT c.id.serviceId FROM UserServiceEntity c WHERE c.id.userId=:userId) OR " +
//            "s.inventoryId IN " +
//            "(SELECT c.id.serviceId FROM GroupServiceEntity c WHERE c.id.groupId IN " +
//            "(SELECT c.groupId FROM GroupsEntity c WHERE c.groupId IN " +
//            "(SELECT c.id.groupId FROM GroupUserEntity c WHERE c.id.userId=:userId))))")
    @Query(value = "Select s FROM ServiceEntity s left outer join s.memberOf gse left outer join gse.group ge left outer join ge.members gue WHERE " +
            "s.hasAgent IS TRUE AND (s.lastAccessTime IS NOT NULL AND s.lastAccessTime>:time) AND" +
            "(:filter IS NULL OR (LOWER(s.name) LIKE %:filter% OR LOWER(s.description) LIKE %:filter% OR s.ipAddress LIKE %:filter2%)) AND " +
            "(gue.user.userId = :userId and ge.deletedAt IS NULL and s.deletedAt IS NULL)")
    List<ServiceEntity> findActiveAgentsByUserIdAndTenantIdAndFilter(@Param("userId") String userId, @Param("filter") String filter, @Param("filter2") String filter2, @Param("time") Timestamp time, Pageable page);

    //    @Query(value = "Select s FROM ServiceEntity s WHERE s.deletedAt IS NULL AND s.hasAgent IS TRUE AND (s.lastAccessTime IS NULL OR s.lastAccessTime<:time) AND (:filter IS NULL OR (LOWER(s.name) LIKE %:filter% OR s.ipAddress LIKE :filter2%)) AND (s.inventoryId IN " +
//            "(SELECT c.id.serviceId FROM UserServiceEntity c WHERE c.id.userId=:userId) OR " +
//            "s.inventoryId IN " +
//            "(SELECT c.id.serviceId FROM GroupServiceEntity c WHERE c.id.groupId IN " +
//            "(SELECT c.groupId FROM GroupsEntity c WHERE c.groupId IN " +
//            "(SELECT c.id.groupId FROM GroupUserEntity c WHERE c.id.userId=:userId))))")
    @Query(value = "Select s FROM ServiceEntity s left outer join s.memberOf gse left outer join gse.group ge left outer join ge.members gue WHERE " +
            "s.hasAgent IS TRUE AND (s.lastAccessTime IS NOT NULL AND s.lastAccessTime<:time) AND" +
            "(:filter IS NULL OR (LOWER(s.name) LIKE %:filter% OR LOWER(s.description) LIKE %:filter% OR s.ipAddress LIKE %:filter2%)) AND " +
            "(gue.user.userId = :userId and ge.deletedAt IS NULL and s.deletedAt IS NULL)")
    List<ServiceEntity> findInactiveAgentByUserIdAndTenantIdAndFilter(@Param("userId") String userId, @Param("filter") String filter, @Param("filter2") String filter2, @Param("time") Timestamp time, Pageable page);


    @Query(value = "SELECT s FROM ServiceEntity s WHERE s.deletedAt IS NULL AND s.serviceTypeId=:serviceType")
    Iterable<ServiceEntity> findByServiceType(@Param("serviceType") ServiceType serviceType);

    @Query("SELECT gue.user FROM GroupUserEntity gue left outer join gue.group g left outer join g.services gse left outer join gse.service s WHERE gue.user.deletedAt IS NULL AND gue.user.blocked IS NULL AND s.inventoryId=:serviceId and g.deletedAt is null")
    List<UserEntity> findUsers(@Param("serviceId") String serviceId);

    @Query("SELECT s FROM GroupUserEntity gue left outer join gue.group g left outer join g.services gse left outer join gse.service s WHERE gue.id.userId=:userId and g.deletedAt is null and s.deletedAt is null")
    List<ServiceEntity> findServices(@Param("userId") String userId);

    // Counters

    long countByServiceTypeId(ServiceType type);

    long countByDeletedAtIsNull();

    @Query(value = "SELECT COUNT(c.inventoryId) FROM ServiceEntity c WHERE (c.serviceTypeId in :serviceType) AND c.deletedAt IS NULL")
    long countByServiceType(@Param("serviceType") List<ServiceType> serviceType);

    @Query(value = "SELECT COUNT(DISTINCT gue) FROM GroupUserEntity gue left outer join gue.group g left outer join g.services gse WHERE gse.service.inventoryId = :serviceId AND g.deletedAt IS NULL")
    long countOfMembers(@Param("serviceId") String serviceId);

    @Query(value = "SELECT COUNT(DISTINCT gse.group) FROM GroupServiceEntity gse WHERE gse.service.inventoryId = :serviceId AND gse.group.deletedAt IS NULL")
    long countOfGroups(@Param("serviceId") String serviceId);

    @Query(value = "SELECT COUNT(DISTINCT cue) FROM ConnectionUserEntity cue WHERE cue.serviceEntity.inventoryId = :serviceId")
    long countOfCredentials(@Param("serviceId") String serviceId);

    @Query(value = "SELECT COUNT(DISTINCT p) FROM GroupServiceEntity gse, PolicyEntity p WHERE p.group.groupId = gse.group.groupId and gse.service.inventoryId = :serviceId AND gse.group.deletedAt IS NULL")
    long countOfPolicies(@Param("serviceId") String serviceId);

    @Query(value = "SELECT COUNT(DISTINCT a) FROM GroupServiceEntity gse, AlarmEntity a WHERE a.groupsEntity.groupId = gse.group.groupId and gse.service.inventoryId = :serviceId AND gse.group.deletedAt IS NULL")
    long countOfAlarms(@Param("serviceId") String serviceId);

    @Query(value = "Select count( DISTINCT s) FROM ServiceEntity s left outer join s.memberOf gse left outer join gse.group ge left outer join ge.members gue WHERE" +
            "(gue.user.userId = :userId and ge.deletedAt IS NULL and s.deletedAt IS NULL and (gue.expireDate IS NULL or gue.expireDate > now()))")
    long countOfServices(@Param("userId") String userId);

    @Query(value = "Select count( DISTINCT s) FROM ServiceEntity s left outer join s.memberOf gse left outer join gse.group ge left outer join ge.members gue WHERE" +
            "(gue.user.userId = :userId and ge.deletedAt IS NULL and s.deletedAt IS NULL and (gue.membershipRole IS NOT NULL AND gue.membershipRole=:role) and (gue.expireDate IS NULL or gue.expireDate > now()))")
    long countOfServices(@Param("userId") String userId, @Param("role") GroupRole role);

    @Query(value = "Select count( DISTINCT s) FROM ServiceEntity s left outer join s.memberOf gse left outer join gse.group ge left outer join ge.members gue WHERE" +
            "(gue.user.userId = :userId and ge.deletedAt IS NULL and s.deletedAt IS NULL and s.hasAgent IS TRUE and (gue.expireDate IS NULL or gue.expireDate > now()))")
    long countOfAgentServices(@Param("userId") String userId);

    @Query(value = "SELECT gse.group FROM GroupServiceEntity gse WHERE gse.service.inventoryId = :serviceId AND gse.group.deletedAt IS NULL AND (:filter IS NULL OR gse.group.groupName LIKE %:filter%)")
    List<GroupsEntity> findServiceGroups(@Param("serviceId") String serviceId, @Param("filter") String filter);

    @Query(value = "Select DISTINCT gse.group FROM GroupServiceEntity gse left outer join gse.group ge left outer join ge.members gue WHERE " +
            "gse.service.inventoryId = :serviceId and gue.user.userId = :userId and gse.group.deletedAt IS NULL and gse.service.deletedAt IS NULL")
    List<GroupsEntity> findGroupsByServiceAndUserId(@Param("serviceId") String serviceId, @Param("userId") String userId);

    @Query(value = "Select DISTINCT s FROM ServiceEntity s left outer join s.memberOf gse left outer join gse.group ge left outer join ge.members gue WHERE" +
            "(gue.user.userId = :userId and ge.deletedAt IS NULL and s.deletedAt IS NULL and s.hasAgent IS TRUE and (:filter IS NULL or s.name LIKE %:filter% or s.description LIKE %:filter%) and s.lastAccessTime>:time)")
    List<ServiceEntity> findActiveAgents(@Param("userId") String userId, @Param("filter") String filter, @Param("time") Timestamp time, Pageable page);

    @Query(value = "Select DISTINCT s FROM ServiceEntity s left outer join s.memberOf gse left outer join gse.group ge left outer join ge.members gue WHERE" +
            "(gue.user.userId = :userId and ge.deletedAt IS NULL and s.deletedAt IS NULL and s.hasAgent IS TRUE and (:filter IS NULL or s.name LIKE %:filter% or s.description LIKE %:filter%) and (s.lastAccessTime IS NULL OR s.lastAccessTime<:time))")
    List<ServiceEntity> findDeactiveAgents(@Param("userId") String userId, @Param("filter") String filter, @Param("time") Timestamp time, Pageable page);

    @Query(value = "Select DISTINCT s FROM ServiceEntity s left outer join s.memberOf gse left outer join gse.group ge left outer join ge.members gue WHERE" +
            "(gue.user.userId = :userId and ge.deletedAt IS NULL and s.deletedAt IS NULL and s.inventoryId=:serviceId and (gue.membershipRole IS NOT NULL AND gue.membershipRole=:role))")
    Optional<ServiceEntity> findByUserAndServiceIdAndRole(@Param("serviceId") String serviceId, @Param("userId") String userId, @Param("role") GroupRole role);

    @Query(value = "Select DISTINCT s FROM ServiceEntity s left outer join s.memberOf gse left outer join gse.group ge left outer join ge.members gue WHERE" +
            "(gue.user.userId = :userId and ge.deletedAt IS NULL and s.deletedAt IS NULL and s.inventoryId=:serviceId)")
    Optional<ServiceEntity> findByUserAndServiceId(@Param("serviceId") String serviceId, @Param("userId") String userId);

    @Query(value = "SELECT c FROM ServiceEntity c WHERE c.ipAddress=:ipAddress AND c.deletedAt IS NULL")
    Optional<ServiceEntity> findByIpAddress(@Param("ipAddress") String ipAddress);
}