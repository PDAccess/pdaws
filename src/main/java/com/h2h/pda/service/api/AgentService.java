package com.h2h.pda.service.api;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.ExecShellGroupSession;
import com.h2h.pda.pojo.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface AgentService {
    Page<AgentStatusEntity> currentStatus(String serviceId, PageRequest pageRequest);

    Long commandCount(String serviceId);

    Page<ExecSessionEntity> getSessions(String serviceId, Pagination pagination);

    Page<ExecShellSessionEntity> getShellSessions(String serviceId, Pagination pagination);

    Page<ExecTrace> getSessionActivities(Long sessionId, Pagination pagination);

    Page<ExecShellTraceDataEntity> getShellSessionActivities(Long sessionId, Pagination pagination);

    ExecShellSessionEntity getShellSession(Long sessionId);

    List<ExecShellTraceDataEntity> getAllShellSessionActivities(Long sessionId, Pagination pagination);

    List<ExecShellTraceDataEntity> getAllShellSessionActivities(Long sessionId);

    List<ExecTrace> getAllSessionActivities(Long sessionId, Pagination pagination);

    Page<ExecShellGroupSession> getGroupSessions(String groupId, Pagination pagination);

}
