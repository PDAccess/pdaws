package com.h2h.pda.api.agent;

import com.h2h.pda.entity.AgentStatusEntity;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.service.api.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agent/status")
public class AgentStatusController {

    @Autowired
    AgentService agentService;

    // TODO: Entity Fix
    @PostMapping(path = "/{service_id}")
    public ResponseEntity<List<AgentStatusEntity>> getAgentStatus(@PathVariable("service_id") String serviceId, @RequestBody Pagination pagination) {

        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("id").descending());
        Page<AgentStatusEntity> agentStatusPage = agentService.currentStatus(serviceId, pageRequest);
        return ResponseEntity.ok(agentStatusPage.getContent());
    }
}
