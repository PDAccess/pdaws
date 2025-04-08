package com.h2h.pda.repository;

import com.h2h.pda.entity.PolicyEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends CrudRepository<PolicyEntity, String> {

    @Query(value = "SELECT p FROM PolicyEntity p WHERE p.group.groupId = ?1")
    List<PolicyEntity> groupPolicies(String groupId);

    @Query(value = "SELECT p FROM PolicyEntity p, GroupServiceEntity gse  WHERE p.behavior=:behavior AND p.group.groupId  = gse.group.groupId and gse.service.inventoryId = :serviceId")
    List<PolicyEntity> findByBehaviorAndUpperId(@Param("behavior") String behavior, @Param("serviceId") String serviceId);

    @Query(value = "SELECT p FROM PolicyEntity p join p.policyUserEntity pu WHERE pu.userId = ?1")
    List<PolicyEntity> policyUserFind(String userid, Pageable pageable);
}
