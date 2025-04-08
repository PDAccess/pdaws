package com.h2h.pda.service.api;

import com.h2h.pda.entity.JobHistoryEntity;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface JobService {

    JobHistoryEntity saveJob(JobHistoryEntity jobHistoryEntity);

    List<JobHistoryEntity> getAllJobs(PageRequest pageRequest);

    List<JobHistoryEntity> getRunningJobs(PageRequest pageRequest);

    List<JobHistoryEntity> getPassedJobs(PageRequest pageRequest);

    List<JobHistoryEntity> getFailedJobs(PageRequest pageRequest);

    List<JobHistoryEntity> getFinishedJobs(PageRequest pageRequest);

}
