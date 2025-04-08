package com.h2h.pda.api.agent;

import com.h2h.pda.entity.ExecShellSessionEntity;
import com.h2h.pda.entity.ExecShellTraceDataEntity;
import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.pojo.*;
import com.h2h.pda.service.api.AgentService;
import com.h2h.pda.service.api.ServiceOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/shell/action")
public class ExShellActionController {

    @Autowired
    AgentService agentService;

    @Autowired
    ServiceOps serviceOps;

    @PostMapping(path = "/sessions/{service_id}")
    public ResponseEntity<ExecShellSessionResponse> getShellSessions(@PathVariable("service_id") String serviceId, @RequestBody Pagination pagination) {
        Page<ExecShellSessionEntity> execShellSessionPage = agentService.getShellSessions(serviceId, pagination);

        ExecShellSessionResponse execShellSessionResponse = new ExecShellSessionResponse();
        execShellSessionResponse.setLogs(execShellSessionPage.getContent());
        execShellSessionResponse.setTotalPages(execShellSessionPage.getTotalPages());

        return ResponseEntity.ok(execShellSessionResponse);
    }

    @PostMapping(path = "/session/{session_id}")
    public ResponseEntity<ExecShellTraceResponse> getSessionActivity(@PathVariable("session_id") Long sessionId, @RequestBody Pagination pagination) {
        Page<ExecShellTraceDataEntity> execShellTracePage = agentService.getShellSessionActivities(sessionId, pagination);
        if (execShellTracePage == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        ExecShellTraceResponse execShellTraceResponse = new ExecShellTraceResponse();
        execShellTraceResponse.setTotalPages(execShellTracePage.getTotalPages());
        execShellTraceResponse.setLogs(execShellTracePage.getContent());

        return ResponseEntity.ok(execShellTraceResponse);
    }

    @GetMapping(path = "/session/{session_id}")
    public ResponseEntity<ExecShellSessionInfo> getSession(@PathVariable("session_id") Long sessionId) {
        ExecShellSessionEntity execShellSessionEntity = agentService.getShellSession(sessionId);
        if (execShellSessionEntity == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Optional<ServiceEntity> serviceEntityOptional = serviceOps.byId(execShellSessionEntity.getServiceId());

        ExecShellSessionInfo execShellSessionInfo = new ExecShellSessionInfo();
        execShellSessionInfo.setExecShellSessionEntity(execShellSessionEntity);
        execShellSessionInfo.setServiceEntity(serviceEntityOptional.orElse(null));

        return ResponseEntity.ok(execShellSessionInfo);
    }

    @PostMapping(path = "/all/session/{session_id}")
    public ResponseEntity<List<ExecShellTraceDataEntity>> getAllSessionActivity(@PathVariable("session_id") Long sessionId, @RequestBody Pagination pagination) {
        List<ExecShellTraceDataEntity> execShellTraceDataEntities = agentService.getAllShellSessionActivities(sessionId, pagination);
        return ResponseEntity.ok(execShellTraceDataEntities);
    }

    @GetMapping(path = "/all/session/{session_id}")
    public ResponseEntity<List<ExecShellTraceDataEntity>> getAllSessionActivity(@PathVariable("session_id") Long sessionId) {
        List<ExecShellTraceDataEntity> execShellTraceDataEntities = agentService.getAllShellSessionActivities(sessionId);
        return ResponseEntity.ok(execShellTraceDataEntities);
    }

    @PostMapping("/sessions/group/{group_id}")
    public ResponseEntity<ExecShellGroupSessionResponse> getGroupSessions(@PathVariable("group_id") String groupId,
                                                                       @RequestBody Pagination pagination) {
        Page<ExecShellGroupSession> groupAgentActivitiesPage = agentService.getGroupSessions(groupId, pagination);

        ExecShellGroupSessionResponse execShellGroupSessionResponse = new ExecShellGroupSessionResponse();
        execShellGroupSessionResponse.setLogs(groupAgentActivitiesPage.getContent());
        execShellGroupSessionResponse.setTotalPages(groupAgentActivitiesPage.getTotalPages());

        return ResponseEntity.ok(execShellGroupSessionResponse);
    }

}
