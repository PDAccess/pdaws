package com.h2h.pda.repository;

import com.h2h.pda.entity.MaintenanceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceRepository extends CrudRepository<MaintenanceEntity, String> {

    @Query(value = "SELECT m FROM MaintenanceEntity m WHERE m.uniqueId=:uniqueId")
    Optional<MaintenanceEntity> findByUniqueId(@Param("uniqueId") String uniqueId);

    @Query(value = "SELECT m FROM MaintenanceEntity m WHERE m.groupsEntity.groupId=:groupId")
    List<MaintenanceEntity> findAllByGroupId(@Param("groupId") String groupId);

    @Query(value = "SELECT m FROM MaintenanceEntity m WHERE m.groupsEntity.groupId=?1 and m.startDate=?2 and m.endDate=?3")
    List<MaintenanceEntity> findAllByGroupIdAndStartDateAnAndEndDate(String groupId, Timestamp startDate, Timestamp endDate);

    @Query(value = "SELECT m FROM MaintenanceEntity m WHERE m.groupsEntity.groupId=?1 and m.userId=?2 and m.startDate=?3 and m.endDate=?4")
    List<MaintenanceEntity> findAllByGroupIdAndUserIdAndStartDateAnAndEndDate(String groupId, String userId, Timestamp startDate, Timestamp endDate);
}
