package com.h2h.pda.api.job;

import com.h2h.pda.entity.JobHistoryEntity;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.service.api.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs/history")
public class JobHistoryController {

    @Autowired
    JobService jobService;

    // TODO: Entity Fix
    @PostMapping
    public ResponseEntity<List<JobHistoryEntity>> getAllJobs(@RequestBody Pagination pagination) {
        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("id").descending());
        return new ResponseEntity<>(jobService.getAllJobs(pageRequest), HttpStatus.OK);
    }

    // TODO: Entity Fix
    @PostMapping(path = "running")
    public ResponseEntity<List<JobHistoryEntity>> getRunningJobs(@RequestBody Pagination pagination) {
        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("id").descending());
        return new ResponseEntity<>(jobService.getRunningJobs(pageRequest), HttpStatus.OK);
    }

    // TODO: Entity Fix
    @PostMapping(path = "passed")
    public ResponseEntity<List<JobHistoryEntity>> getPassedJobs(@RequestBody Pagination pagination) {
        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("id").descending());
        return new ResponseEntity<>(jobService.getPassedJobs(pageRequest), HttpStatus.OK);
    }

    // TODO: Entity Fix
    @PostMapping(path = "failed")
    public ResponseEntity<List<JobHistoryEntity>> getFailedJobs(@RequestBody Pagination pagination) {
        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("id").descending());
        return new ResponseEntity<>(jobService.getFailedJobs(pageRequest), HttpStatus.OK);
    }

    // TODO: Entity Fix
    @PostMapping(path = "finished")
    public ResponseEntity<List<JobHistoryEntity>> getFinishedJobs(@RequestBody Pagination pagination) {
        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("id").descending());
        return new ResponseEntity<>(jobService.getFinishedJobs(pageRequest), HttpStatus.OK);
    }

}
