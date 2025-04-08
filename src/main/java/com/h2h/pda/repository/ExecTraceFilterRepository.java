package com.h2h.pda.repository;

import com.h2h.pda.entity.ExecTraceFilter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExecTraceFilterRepository extends CrudRepository<ExecTraceFilter, Integer> {

    @Query(value = "SELECT f FROM ExecTraceFilter f WHERE f.serviceId=:service_id")
    List<ExecTraceFilter> findAllByServiceId(@Param("service_id") String serviceId);

    @Query(value = "SELECT f FROM ExecTraceFilter f WHERE f.groupId=:group_id")
    List<ExecTraceFilter> findAllByGroupId(@Param("group_id") String groupId);

    @Query(value = "SELECT f FROM ExecTraceFilter f WHERE f.serviceId=:service_id OR f.groupId=:group_id")
    List<ExecTraceFilter> findAllByServiceIdAndGroupId(@Param("service_id") String serviceId, @Param("group_id") String groupId);

}
