package com.h2h.pda.repository;

import com.h2h.pda.entity.ExecShellSessionEntity;
import com.h2h.pda.pojo.ExecShellGroupSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface ExecShellSessionRepository extends CrudRepository<ExecShellSessionEntity, Long> {

    @Query(value = "SELECT e FROM ExecShellSessionEntity e WHERE e.sessionId=:sessionId")
    Optional<ExecShellSessionEntity> findBySessionId(@Param("sessionId") String sessionId);

    @Query(value = "SELECT e FROM ExecShellSessionEntity e WHERE ((cast(:startTime as date) IS NULL OR e.startTime > :startTime) AND (cast(:endTime as date) IS NULL OR e.startTime < :endTime)) AND e.serviceId=:serviceId AND (:filter IS NULL OR e.username LIKE %:filter%)")
    Page<ExecShellSessionEntity> findByServiceId(@Param("serviceId") String serviceId, @Param("filter") String filter, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime, Pageable pageable);

    @Query(value = "SELECT new com.h2h.pda.pojo.ExecShellGroupSession(e, gs.service) FROM ExecShellSessionEntity e INNER JOIN GroupServiceEntity gs ON e.serviceId=gs.id.serviceId WHERE ((cast(:startTime as date) IS NULL OR e.startTime > :startTime) AND (cast(:endTime as date) IS NULL OR e.startTime < :endTime)) AND gs.id.groupId=:groupId AND (:filter IS NULL OR e.username LIKE %:filter%)")
    Page<ExecShellGroupSession> findByGroupId(@Param("groupId") String groupId, @Param("filter") String filter, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime, Pageable pageable);
}
