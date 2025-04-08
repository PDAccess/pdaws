package com.h2h.pda.service.impl;

import com.h2h.pda.entity.JobHistoryEntity;
import com.h2h.pda.repository.JobHistoryRepository;
import com.h2h.pda.service.api.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    JobHistoryRepository jobHistoryRepository;

    @Override
    public JobHistoryEntity saveJob(JobHistoryEntity jobHistoryEntity) {
        return jobHistoryRepository.save(jobHistoryEntity);
    }

    @Override
    public List<JobHistoryEntity> getAllJobs(PageRequest pageRequest) {
        return jobHistoryRepository.findAll(pageRequest);
    }

    @Override
    public List<JobHistoryEntity> getRunningJobs(PageRequest pageRequest) {
        return jobHistoryRepository.findAllByRunningJobs(pageRequest);
    }

    @Override
    public List<JobHistoryEntity> getPassedJobs(PageRequest pageRequest) {
        return jobHistoryRepository.findAllByPassedJobs(pageRequest);
    }

    @Override
    public List<JobHistoryEntity> getFailedJobs(PageRequest pageRequest) {
        return jobHistoryRepository.findAllByFailedJobs(pageRequest);
    }

    @Override
    public List<JobHistoryEntity> getFinishedJobs(PageRequest pageRequest) {
        return jobHistoryRepository.findAllByFinishedJobs(pageRequest);
    }
}
