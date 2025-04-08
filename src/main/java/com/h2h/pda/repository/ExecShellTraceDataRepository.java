package com.h2h.pda.repository;

import com.h2h.pda.entity.ExecShellTraceDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ExecShellTraceDataRepository extends CrudRepository<ExecShellTraceDataEntity, Long> {

    @Query(value = "SELECT e FROM ExecShellTraceDataEntity e WHERE e.serviceId=:serviceId AND e.sessionId=:sessionId AND (:filter IS NULL OR LOWER(e.execCommand) LIKE %:filter% OR LOWER(e.stdOut) LIKE %:filter%) AND ((cast(:startTime as date) IS NULL OR to_timestamp(e.execTime/1000000000) > :startTime) AND (cast(:endTime as date) IS NULL OR to_timestamp(e.execTime/1000000000) < :endTime)) order by e.execTime asc")
    Page<ExecShellTraceDataEntity> findByServiceAndSessionId(@Param("serviceId") String serviceId, @Param("sessionId") String sessionId, @Param("filter") String filter, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime, Pageable pageable);

    @Query(value = "SELECT e FROM ExecShellTraceDataEntity e WHERE e.serviceId=:serviceId AND e.sessionId=:sessionId AND (:filter IS NULL OR LOWER(e.execCommand) LIKE %:filter% OR LOWER(e.stdOut) LIKE %:filter%) AND ((cast(:startTime as date) IS NULL OR to_timestamp(e.execTime/1000000000) > :startTime) and (cast(:endTime as date) IS NULL OR to_timestamp(e.execTime/1000000000) < :endTime)) order by e.execTime asc")
    List<ExecShellTraceDataEntity> findAllByServiceAndSessionId(@Param("serviceId") String serviceId, @Param("sessionId") String sessionId, @Param("filter") String filter, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime);

    @Query(value = "SELECT e FROM ExecShellTraceDataEntity e WHERE e.serviceId=:serviceId AND e.sessionId=:sessionId order by e.execTime asc")
    List<ExecShellTraceDataEntity> findAllByServiceAndSessionId(@Param("serviceId") String serviceId, @Param("sessionId") String sessionId);

}
