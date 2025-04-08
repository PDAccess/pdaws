package com.h2h.pda.repository;

import com.h2h.pda.entity.AlarmEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends CrudRepository<AlarmEntity, Integer> {

    @Query(value = "SELECT a FROM AlarmEntity a, GroupServiceEntity gse  WHERE a.groupsEntity.groupId = gse.group.groupId and gse.service.inventoryId=:serviceId AND a.active IS TRUE")
    List<AlarmEntity> findAllByActiveAndService(@Param("serviceId") String serviceId);

    @Query(value = "SELECT a FROM AlarmEntity a WHERE a.groupsEntity.groupId=:groupId")
    List<AlarmEntity> findAllByGroup(@Param("groupId") String groupId);
}
