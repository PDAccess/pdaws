package com.h2h.pda.repository;

import com.h2h.pda.entity.ExecFileFilter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExecFileFilterRepository extends CrudRepository<ExecFileFilter, Integer> {

    @Query(value = "SELECT f FROM ExecFileFilter f WHERE f.serviceId=:service_id")
    List<ExecFileFilter> findAllByServiceId(@Param("service_id") String serviceId);

    @Query(value = "SELECT f FROM ExecFileFilter f WHERE f.groupId=:group_id")
    List<ExecFileFilter> findAllByGroupId(@Param("group_id") String groupId);

    @Query(value = "SELECT f FROM ExecFileFilter f WHERE f.serviceId=:service_id OR f.groupId=:group_id")
    List<ExecFileFilter> findAllByServiceIdAndGroupId(@Param("service_id") String serviceId, @Param("group_id") String groupId);

}
