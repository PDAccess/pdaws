package com.h2h.pda.repository;

import com.h2h.pda.entity.JobHistoryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobHistoryRepository extends CrudRepository<JobHistoryEntity, Integer> {

    List<JobHistoryEntity> findAll(Pageable pageable);

    @Query(value = "SELECT j FROM JobHistoryEntity j WHERE j.finishedAt IS NULL")
    List<JobHistoryEntity> findAllByRunningJobs(Pageable pageable);

    @Query(value = "SELECT j FROM JobHistoryEntity j WHERE j.finishedAt IS NOT NULL AND j.isSuccess IS TRUE")
    List<JobHistoryEntity> findAllByPassedJobs(Pageable pageable);

    @Query(value = "SELECT j FROM JobHistoryEntity j WHERE j.finishedAt IS NOT NULL AND j.isSuccess IS FALSE")
    List<JobHistoryEntity> findAllByFailedJobs(Pageable pageable);

    @Query(value = "SELECT j FROM JobHistoryEntity j WHERE j.finishedAt IS NOT NULL")
    List<JobHistoryEntity> findAllByFinishedJobs(Pageable pageable);

}
