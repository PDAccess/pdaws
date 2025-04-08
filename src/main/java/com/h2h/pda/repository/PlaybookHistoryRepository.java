package com.h2h.pda.repository;

import com.h2h.pda.entity.PlaybookHistoryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaybookHistoryRepository extends CrudRepository<PlaybookHistoryEntity, Integer> {

    @Query(value = "SELECT h FROM PlaybookHistoryEntity h WHERE h.installerEntity.id=:installer_id ORDER BY created_at DESC")
    List<PlaybookHistoryEntity> findLastByInstallerId(@Param("installer_id") Integer installerId);

}
