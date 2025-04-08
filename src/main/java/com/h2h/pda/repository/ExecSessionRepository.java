package com.h2h.pda.repository;

import com.h2h.pda.entity.ExecSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface ExecSessionRepository extends CrudRepository<ExecSessionEntity, Long> {

    @Query(value = "SELECT e FROM ExecSessionEntity e WHERE (e.sessionStartTime > :startTime AND e.sessionStartTime < :endTime) AND e.serviceId=:serviceId AND (:filter IS NULL OR e.loginUser LIKE %:filter%)")
    Page<ExecSessionEntity> findByServiceId(@Param("serviceId") String serviceId, @Param("filter") String filter, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime, Pageable pageable);

    @Query(value = "SELECT e FROM ExecSessionEntity e WHERE e.sessionId=:sessionId")
    Optional<ExecSessionEntity> findBySessionId(@Param("sessionId") Long sessionId);
}
