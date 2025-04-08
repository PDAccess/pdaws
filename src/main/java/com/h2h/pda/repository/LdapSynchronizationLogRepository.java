package com.h2h.pda.repository;

import com.h2h.pda.entity.LdapSynchronizationLogEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LdapSynchronizationLogRepository extends CrudRepository<LdapSynchronizationLogEntity, Integer> {

    @Query("SELECT l FROM LdapSynchronizationLogEntity l WHERE l.groupId=:group_id")
    List<LdapSynchronizationLogEntity> findByGroupId(@Param("group_id") String groupId);

    @Query("SELECT l FROM LdapSynchronizationLogEntity l WHERE l.groupId=:group_id AND (:user IS NULL OR LOWER(l.createdUsers) LIKE %:user% OR LOWER(l.deletedUsers) LIKE %:user% OR LOWER(l.addedUsers) LIKE %:user%)")
    List<LdapSynchronizationLogEntity> findByGroupIdAndUser(@Param("group_id") String groupId, @Param("user") String filter, Pageable pageable);

}
