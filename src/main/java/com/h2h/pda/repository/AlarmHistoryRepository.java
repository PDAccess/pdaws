package com.h2h.pda.repository;

import com.h2h.pda.entity.AlarmHistoryEntity;
import com.h2h.pda.pojo.group.GroupRole;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlarmHistoryRepository extends CrudRepository<AlarmHistoryEntity, Long> {

    @Query(value = "SELECT COUNT(a) FROM AlarmHistoryEntity a INNER JOIN GroupUserEntity gu ON a.alarmEntity.groupsEntity.groupId=gu.group.groupId WHERE gu.user.userId=:userId AND gu.membershipRole=:role")
    Long countByGroupMembership(@Param("userId") String userId, @Param("role") GroupRole role);

    @Query(value = "SELECT a FROM AlarmHistoryEntity a INNER JOIN GroupUserEntity gu ON a.alarmEntity.groupsEntity.groupId=gu.group.groupId WHERE a.username LIKE %:filter% AND gu.user.userId=:userId AND gu.membershipRole=:role")
    List<AlarmHistoryEntity> findAllByGroupMembership(@Param("filter") String filter, @Param("userId") String userId, @Param("role") GroupRole role, Pageable pageable);

}
