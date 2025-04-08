package com.h2h.pda.service.impl;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.ExecShellGroupSession;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.repository.*;
import com.h2h.pda.service.api.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AgentServiceImpl implements AgentService {

    @Autowired
    AgentStatusRepository agentStatusRepository;

    @Autowired
    ExecTraceRepository execTraceRepository;

    @Autowired
    ExecSessionRepository execSessionRepository;

    @Autowired
    ExecShellSessionRepository execShellSessionRepository;

    @Autowired
    ExecShellTraceDataRepository execShellTraceDataRepository;

    @Override
    @PreAuthorize("@securityService.hasAdmin(authentication)")
    public Page<AgentStatusEntity> currentStatus(String serviceId, PageRequest pageRequest) {
        return agentStatusRepository.findByService(serviceId, Timestamp.valueOf(LocalDateTime.now().minusDays(1)), pageRequest);
    }

    @Override
    public Long commandCount(String serviceId) {
        return execTraceRepository.findCountByServiceId(serviceId);
    }

    @Override
    public Page<ExecSessionEntity> getSessions(String serviceId, Pagination pagination) {
        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("sessionId").descending());
        return execSessionRepository.findByServiceId(serviceId, pagination.getFilter(), pagination.getStartTime(), pagination.getEndTime(), pageRequest);
    }

    @Override
    public Page<ExecShellSessionEntity> getShellSessions(String serviceId, Pagination pagination) {
        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("id").descending());
        return execShellSessionRepository.findByServiceId(serviceId, pagination.getFilter(), pagination.getStartTime(), pagination.getEndTime(), pageRequest);
    }

    @Override
    public Page<ExecTrace> getSessionActivities(Long sessionId, Pagination pagination) {
        Optional<ExecSessionEntity> optionalExecSessionEntity = execSessionRepository.findBySessionId(sessionId);
        if (!optionalExecSessionEntity.isPresent()) {
            return null;
        }

        ExecSessionEntity execSessionEntity = optionalExecSessionEntity.get();
        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("id").descending());
        //return execTraceRepository.findByServiceAndTimeAndTerminalWithPipe(execSessionEntity.getServiceId(), execSessionEntity.getSessionStartTime(), execSessionEntity.getLoginTerminal(), pagination.getFilter(), pagination.getStartTime(), pagination.getEndTime(), pagination.getPerPage(), pagination.getCurrentPage() * pagination.getPerPage());
        return execTraceRepository.findByServiceAndTimeAndTerminal(execSessionEntity.getServiceId(), execSessionEntity.getSessionStartTime(), execSessionEntity.getLoginTerminal(), pagination.getFilter(), pagination.getStartTime(), pagination.getEndTime(), pageRequest);
    }

    @Override
    public Page<ExecShellTraceDataEntity> getShellSessionActivities(Long sessionId, Pagination pagination) {
        Optional<ExecShellSessionEntity> optionalExecSessionEntity = execShellSessionRepository.findById(sessionId);
        if (!optionalExecSessionEntity.isPresent()) {
            return null;
        }

        ExecShellSessionEntity execShellSessionEntity = optionalExecSessionEntity.get();
        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("id").descending());

        return execShellTraceDataRepository.findByServiceAndSessionId(execShellSessionEntity.getServiceId(), execShellSessionEntity.getSessionId(), pagination.getFilter(), pagination.getStartTime(), pagination.getEndTime(), pageRequest);
    }

    @Override
    public ExecShellSessionEntity getShellSession(Long sessionId) {
        Optional<ExecShellSessionEntity> optionalExecSessionEntity = execShellSessionRepository.findById(sessionId);
        return optionalExecSessionEntity.orElse(null);
    }

    @Override
    public List<ExecShellTraceDataEntity> getAllShellSessionActivities(Long sessionId, Pagination pagination) {
        Optional<ExecShellSessionEntity> optionalExecShellSessionEntity = execShellSessionRepository.findById(sessionId);
        if (!optionalExecShellSessionEntity.isPresent()) {
            return null;
        }

        ExecShellSessionEntity execShellSessionEntity = optionalExecShellSessionEntity.get();

        return execShellTraceDataRepository.findAllByServiceAndSessionId(execShellSessionEntity.getServiceId(), execShellSessionEntity.getSessionId(), pagination.getFilter(), pagination.getStartTime(), pagination.getEndTime());
    }

    @Override
    public List<ExecShellTraceDataEntity> getAllShellSessionActivities(Long sessionId) {
        Optional<ExecShellSessionEntity> optionalExecShellSessionEntity = execShellSessionRepository.findById(sessionId);
        if (!optionalExecShellSessionEntity.isPresent()) {
            return null;
        }

        ExecShellSessionEntity execShellSessionEntity = optionalExecShellSessionEntity.get();

        return execShellTraceDataRepository.findAllByServiceAndSessionId(execShellSessionEntity.getServiceId(), execShellSessionEntity.getSessionId());
    }

    @Override
    public List<ExecTrace> getAllSessionActivities(Long sessionId, Pagination pagination) {
        Optional<ExecSessionEntity> optionalExecSessionEntity = execSessionRepository.findBySessionId(sessionId);
        if (!optionalExecSessionEntity.isPresent()) {
            return null;
        }

        ExecSessionEntity execSessionEntity = optionalExecSessionEntity.get();
        //return execTraceRepository.findAllByServiceAndTimeAndTerminalWithPipe(execSessionEntity.getServiceId(), execSessionEntity.getSessionStartTime(), execSessionEntity.getLoginTerminal(), pagination.getFilter(), pagination.getStartTime(), pagination.getEndTime());
        return execTraceRepository.findAllByServiceAndTimeAndTerminal(execSessionEntity.getServiceId(), execSessionEntity.getSessionStartTime(), execSessionEntity.getLoginTerminal(), pagination.getFilter(), pagination.getStartTime(), pagination.getEndTime());
    }

    @Override
    public Page<ExecShellGroupSession> getGroupSessions(String groupId, Pagination pagination) {
        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("id").descending());
        return execShellSessionRepository.findByGroupId(groupId, pagination.getFilter(), pagination.getStartTime(), pagination.getEndTime(), pageRequest);
    }

}
