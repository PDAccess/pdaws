package com.h2h.pda.repository;

import com.h2h.pda.entity.AgentStatusEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface AgentStatusRepository extends CrudRepository<AgentStatusEntity, Integer> {

    @Query(value = "SELECT a FROM AgentStatusEntity a WHERE a.service.inventoryId=:serviceId AND a.createdAt>:time")
    Page<AgentStatusEntity> findByService(@Param("serviceId") String serviceId, @Param("time") Timestamp time, Pageable pageable);
}
