package com.h2h.pda.repository;

import com.h2h.pda.entity.ConnectionUserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionUserRepository extends CrudRepository<ConnectionUserEntity, Integer> {

    @Query("SELECT c FROM ConnectionUserEntity c WHERE c.serviceEntity.inventoryId=:serviceId")
    List<ConnectionUserEntity> findByServiceId(@Param("serviceId") String serviceId);

    @Query("SELECT c FROM ConnectionUserEntity c left outer join GroupServiceEntity g on c.serviceEntity.inventoryId = g.service.inventoryId WHERE g.group.groupId=:groupId")
    @Deprecated
    List<ConnectionUserEntity> findByGroupId(@Param("groupId") String groupId);

    ConnectionUserEntity findByUsername(String username);

    @Query(value = "SELECT c FROM ConnectionUserEntity c WHERE c.serviceEntity.inventoryId=:serviceId AND c.isAdmin IS :role")
    List<ConnectionUserEntity> findByServiceIdAndRole(@Param("serviceId") String serviceId, @Param("role") Boolean role);

    @Query(value = "SELECT c FROM ConnectionUserEntity c WHERE c.username=:username AND c.serviceEntity.inventoryId=:serviceId")
    List<ConnectionUserEntity> findByUsernameAndService(@Param("username") String username, @Param("serviceId") String serviceId);
}