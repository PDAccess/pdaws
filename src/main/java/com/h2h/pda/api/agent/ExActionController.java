package com.h2h.pda.api.agent;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.*;
import com.h2h.pda.pojo.group.GroupsService;
import com.h2h.pda.repository.*;
import com.h2h.pda.service.api.AgentService;
import com.h2h.pda.service.api.ServiceOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/action")
public class ExActionController {
    private final Logger logger = LoggerFactory.getLogger(ExActionController.class);

    @Autowired
    ExecTraceRepository execTraceRepository;
    @Autowired
    ExecAuthRepository execAuthRepository;
    @Autowired
    ExecFileRepository execFileRepository;
    @Autowired
    ExecFileFilterRepository execFileFilterRepository;
    @Autowired
    ExecTraceFilterRepository execTraceFilterRepository;

    @Autowired
    ServiceOps serviceOps;
    @Autowired
    AgentService agentService;


    @GetMapping("/filter/file/service/{serviceId}")
    public ResponseEntity<List<ExecFileFilter>> getFileServiceFilter(@PathVariable String serviceId) {

        List<ExecFileFilter> execFileFilters = execFileFilterRepository.findAllByServiceId(serviceId);

        return new ResponseEntity<>(execFileFilters, HttpStatus.OK);
    }

    @GetMapping("/filter/file/group/{groupId}")
    public ResponseEntity<List<ExecFileFilter>> getFileGroupFilter(@PathVariable String groupId) {

        List<ExecFileFilter> execFileFilters = execFileFilterRepository.findAllByGroupId(groupId);

        return new ResponseEntity<>(execFileFilters, HttpStatus.OK);
    }

    @GetMapping("/filter/command/service/{serviceId}")
    public ResponseEntity<List<ExecTraceFilter>> getCommandServiceFilter(@PathVariable String serviceId) {

        List<ExecTraceFilter> execTraceFilters = execTraceFilterRepository.findAllByServiceId(serviceId);

        return new ResponseEntity<>(execTraceFilters, HttpStatus.OK);
    }

    @GetMapping("/filter/command/group/{groupId}")
    public ResponseEntity<List<ExecTraceFilter>> getCommandGroupFilter(@PathVariable String groupId) {

        List<ExecTraceFilter> execTraceFilters = execTraceFilterRepository.findAllByGroupId(groupId);

        return new ResponseEntity<>(execTraceFilters, HttpStatus.OK);
    }

    @PostMapping("/filter/file")
    public ResponseEntity<Void> createFileFilter(@RequestParam(value = "name") String name,
                                                 @RequestParam(value = "description") String description,
                                                 @RequestParam(value = "users") String users,
                                                 @RequestParam(value = "paths") String paths,
                                                 @RequestParam(value = "service_id", required = false) String serviceId,
                                                 @RequestParam(value = "group_id", required = false) String groupId) {

        ExecFileFilter fileFilter = new ExecFileFilter();
        fileFilter.setName(name);
        fileFilter.setDescription(description);
        fileFilter.setServiceId(serviceId);
        fileFilter.setGroupId(groupId);
        fileFilter.setUsers(users);
        fileFilter.setPaths(paths);
        execFileFilterRepository.save(fileFilter);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/v1/filter/command")
    public ResponseEntity<Void> createCommandFilter(@RequestParam(value = "name") String name,
                                                    @RequestParam(value = "description") String description,
                                                    @RequestParam(value = "users") String users,
                                                    @RequestParam(value = "regexes") String regexes,
                                                    @RequestParam(value = "service_id", required = false) String serviceId,
                                                    @RequestParam(value = "group_id", required = false) String groupId) {

        ExecTraceFilter execTraceFilter = new ExecTraceFilter();
        execTraceFilter.setName(name);
        execTraceFilter.setDescription(description);
        execTraceFilter.setUsers(users);
        execTraceFilter.setRegexes(regexes);
        execTraceFilter.setServiceId(serviceId);
        execTraceFilter.setGroupId(groupId);
        execTraceFilterRepository.save(execTraceFilter);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/filter/file/{filterId}")
    public ResponseEntity<ExecFileFilter> getFileFilter(@PathVariable Integer filterId) {
        Optional<ExecFileFilter> optional = execFileFilterRepository.findById(filterId);
        return optional.map(execFileFilter -> new ResponseEntity<>(execFileFilter, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @GetMapping("/filter/command/{filterId}")
    public ResponseEntity<ExecTraceFilter> getCommandFilter(@PathVariable Integer filterId) {
        Optional<ExecTraceFilter> optional = execTraceFilterRepository.findById(filterId);
        return optional.map(execTraceFilter -> new ResponseEntity<>(execTraceFilter, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @PutMapping("/filter/file/{filterId}")
    public ResponseEntity<Void> editFileFilter(@RequestParam(value = "name") String name,
                                               @RequestParam(value = "description") String description,
                                               @RequestParam(value = "users") String users,
                                               @RequestParam(value = "paths") String paths,
                                               @PathVariable Integer filterId) {

        Optional<ExecFileFilter> optional = execFileFilterRepository.findById(filterId);
        if (!optional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        ExecFileFilter execFileFilter = optional.get();
        execFileFilter.setName(name);
        execFileFilter.setDescription(description);
        execFileFilter.setPaths(paths);
        execFileFilter.setUsers(users);
        execFileFilterRepository.save(execFileFilter);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/filter/command/{filterId}")
    public ResponseEntity<Void> editCommandFilter(@RequestParam(value = "name") String name,
                                                  @RequestParam(value = "description") String description,
                                                  @RequestParam(value = "users") String users,
                                                  @RequestParam(value = "regexes") String regexes,
                                                  @PathVariable Integer filterId) {

        Optional<ExecTraceFilter> optional = execTraceFilterRepository.findById(filterId);
        if (!optional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        ExecTraceFilter execTraceFilter = optional.get();
        execTraceFilter.setName(name);
        execTraceFilter.setDescription(description);
        execTraceFilter.setRegexes(regexes);
        execTraceFilter.setUsers(users);
        execTraceFilterRepository.save(execTraceFilter);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/filter/file/{filterId}")
    public ResponseEntity<Void> deleteFileFilter(@PathVariable Integer filterId) {

        execFileFilterRepository.deleteById(filterId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/filter/command/{filterId}")
    public ResponseEntity<Void> deleteCommandFilter(@PathVariable Integer filterId) {

        execTraceFilterRepository.deleteById(filterId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/users/{host}")
    public ResponseEntity<List<String>> listUser(@PathVariable(name = "host") String host) {
        return new ResponseEntity<>(execTraceRepository.findAllUser(), HttpStatus.OK);
    }

    @PostMapping("/list/{host}")
    public ResponseEntity<List<ExecTrace>> listCommand(@PathVariable(name = "host") String host,
                                                       @RequestBody Pagination pagination,
                                                       HttpServletRequest request) {
        PageRequest req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("user"));
        if (pagination.getSort() != null) {
            if (pagination.getSort().equals("userdesc")) {
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("user").descending());

            } else if (pagination.getSort().equals("created")) {
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("time"));

            } else if (pagination.getSort().equals("createddesc")) {
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("time").descending());

            }
        }

        Specification<ExecTrace> where = null;
        List<String> usersFilter = pagination.getUsersFilter();
        String commandFilter = pagination.getFilter();

        if (usersFilter != null && !usersFilter.isEmpty()) {
            where = Specification.where(ExecTraceRepository.QueryFilter.findByExecFilterByUsers(usersFilter));
        }

        if (commandFilter != null && !commandFilter.isEmpty()) {
            Specification<ExecTrace> filterCommand = Specification.where(ExecTraceRepository.QueryFilter.findByExecFilterByCommand(commandFilter));
            where = where == null ? filterCommand : where.and(filterCommand);
        }

        Page<ExecTrace> all = execTraceRepository.findAll(where, req);

        List<ExecTrace> result = all.stream().collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/list/findbyserviceid/{serviceid}")
    public ResponseEntity<ExecTraceResponse> listCommandByServiceId(@PathVariable(name = "serviceid") String serviceid,
                                                                    @RequestBody Pagination pagination) {

        /*
        List<ExecTrace> execTraceList = execTraceRepository.findByServiceIdWithPipe(serviceid, pagination.getFilter(), pagination.getStartTime(), pagination.getEndTime(), pagination.getPerPage(), pagination.getCurrentPage() * pagination.getPerPage());

        ExecTraceResponse execTraceResponse = new ExecTraceResponse();
        execTraceResponse.setLogs(execTraceList);
        execTraceResponse.setTotalPages(execTraceList.size() >= pagination.getPerPage() ? pagination.getCurrentPage() + 2 : pagination.getCurrentPage() + 1);
         */

        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("time").descending());
        Page<ExecTrace> execTracePage = execTraceRepository.findByServiceId(serviceid, pagination.getFilter(), pagination.getStartTime(), pagination.getEndTime(), pageRequest);

        ExecTraceResponse execTraceResponse = new ExecTraceResponse();
        execTraceResponse.setLogs(execTracePage.getContent());
        execTraceResponse.setTotalPages(execTracePage.getTotalPages());

        return ResponseEntity.ok(execTraceResponse);
    }

    @PostMapping("/list/all/findbyserviceid/{serviceid}")
    public ResponseEntity<List<ExecTrace>> listAllCommandByServiceId(@PathVariable(name = "serviceid") String serviceid,
                                                                    @RequestBody Pagination pagination) {
        //List<ExecTrace> execTraceList = execTraceRepository.findAllByServiceIdWithPipe(serviceid, pagination.getFilter(), pagination.getStartTime(), pagination.getEndTime());
        List<ExecTrace> execTraceList = execTraceRepository.findAllByServiceId(serviceid, pagination.getFilter(), pagination.getStartTime(), pagination.getEndTime());
        return ResponseEntity.ok(execTraceList);
    }

    @PostMapping("/list/findbygroupid/{groupid}")
    public ResponseEntity<List<ExecTraceWrapper>> listCommandByGroupId(@PathVariable String groupid,
                                                                       @RequestBody Pagination pagination) {

        PageRequest req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("time").descending());

        String commandFilter = pagination.getFilter();
        List<ExecTraceWrapper> allByServices = execTraceRepository.findByGroupIdAndCommand(groupid, commandFilter, pagination.getStartTime(), pagination.getEndTime(), req);

        return ResponseEntity.ok(allByServices);
    }

    @PostMapping("/auth/list/service/{serviceid}")
    public ResponseEntity<ExecAuthResponse> listAuthActionByServiceId(@PathVariable String serviceid,
                                                                      @RequestBody Pagination pagination) {

        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("time").descending());
        Page<ExecAuth> execAuthPage = execAuthRepository.findByServiceId(serviceid, pagination.getFilter(), pageRequest);

        ExecAuthResponse execAuthResponse = new ExecAuthResponse();
        execAuthResponse.setLogs(execAuthPage.getContent());
        execAuthResponse.setTotalPages(execAuthPage.getTotalPages());

        return ResponseEntity.ok(execAuthResponse);
    }

    @PostMapping("/auth/list/group/{groupid}")
    public ResponseEntity<List<ExecAuthWrapper>> listAuthActionByGroupId(@PathVariable String groupid,
                                                                         @RequestBody Pagination pagination) {

        PageRequest req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("time").descending());

        String commandFilter = pagination.getFilter();
        List<ExecAuthWrapper> execAuthWrappers = execAuthRepository.findByGroupId(groupid, commandFilter, req);

        return ResponseEntity.ok(execAuthWrappers);
    }

    @PostMapping("/file/list/service/{serviceid}")
    public ResponseEntity<ExecFileResponse> listFileActionByServiceId(@PathVariable String serviceid,
                                                                      @RequestBody Pagination pagination) {

        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("time").descending());
        Page<ExecFile> execFilePage = execFileRepository.findByServiceId(serviceid, pagination.getFilter(), pageRequest);

        ExecFileResponse execFileResponse = new ExecFileResponse();
        execFileResponse.setLogs(execFilePage.getContent());
        execFileResponse.setTotalPages(execFilePage.getTotalPages());

        return ResponseEntity.ok(execFileResponse);
    }

    @PostMapping("/file/list/group/{groupid}")
    public ResponseEntity<List<ExecFileWrapper>> listFileActionByGroupId(@PathVariable String groupid,
                                                                         @RequestBody Pagination pagination) {

        PageRequest req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("time").descending());

        String commandFilter = pagination.getFilter();
        List<ExecFileWrapper> execFileWrappers = execFileRepository.findByGroupId(groupid, commandFilter, req);

        return ResponseEntity.ok(execFileWrappers);
    }

    @PostMapping("/findgroupbyserviceid/{serviceid}")
    public ResponseEntity<List<GroupsService>> listCommandByServiceId(@PathVariable String serviceid) {
        Optional<ServiceEntity> serviceEntity = serviceOps.byId(serviceid);

        List<GroupsEntity> groupServiceEntityList = serviceOps.effectiveGroups(serviceid);
        List<GroupsService> groupsServices = new ArrayList<>();
        if (serviceEntity.isPresent()) {
            ServiceEntity entity = serviceEntity.get();
            for (GroupsEntity groupServiceEntity : groupServiceEntityList) {
                GroupsService groupsService = new GroupsService();
                groupsService.setGroupName(groupServiceEntity.getGroupName());
                groupsService.setServiceName(entity.getName());
                groupsService.setId(new GroupsService.Id(groupServiceEntity.getGroupId(), entity.getInventoryId()));
                groupsServices.add(groupsService);
            }
        }
        return new ResponseEntity<>(groupsServices, HttpStatus.OK);
    }

    String trim255(String original) {
        if (original == null || original.length() < 255)
            return original;
        else {
            byte[] bytes = original.getBytes();
            return new String(bytes, 0, 255);
        }
    }


    @PostMapping(path = "/agent/status/{service_id}")
    public ResponseEntity<ExecAgentStatusResponse> getAgentStatus(@PathVariable("service_id") String serviceId, @RequestBody Pagination pagination) {

        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("id").descending());
        Page<AgentStatusEntity> agentStatusPage = agentService.currentStatus(serviceId, pageRequest);

        ExecAgentStatusResponse execAgentStatusResponse = new ExecAgentStatusResponse();
        execAgentStatusResponse.setLogs(agentStatusPage.getContent());
        execAgentStatusResponse.setTotalPages(agentStatusPage.getTotalPages());

        return ResponseEntity.ok(execAgentStatusResponse);
    }

    @GetMapping(path = "/command/count/{service_id}")
    public ResponseEntity<Long> getAgentStatus(@PathVariable("service_id") String serviceId) {
        Long commandCount = agentService.commandCount(serviceId);
        return ResponseEntity.ok(commandCount);
    }

    @PostMapping(path = "/sessions/{service_id}")
    public ResponseEntity<ExecSessionResponse> getAllSessions(@PathVariable("service_id") String serviceId, @RequestBody Pagination pagination) {
        Page<ExecSessionEntity> execSessionPage = agentService.getSessions(serviceId, pagination);

        ExecSessionResponse execSessionResponse = new ExecSessionResponse();
        execSessionResponse.setLogs(execSessionPage.getContent());
        execSessionResponse.setTotalPages(execSessionPage.getTotalPages());

        return ResponseEntity.ok(execSessionResponse);
    }

    @PostMapping(path = "/session/{session_id}")
    public ResponseEntity<ExecTraceResponse> getSessionActivity(@PathVariable("session_id") Long sessionId, @RequestBody Pagination pagination) {
        Page<ExecTrace> execTracePage = agentService.getSessionActivities(sessionId, pagination);
        if (execTracePage == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        /*
        ExecTraceResponse execTraceResponse = new ExecTraceResponse();
        execTraceResponse.setTotalPages(execTraceList.size() >= pagination.getPerPage() ? pagination.getCurrentPage() + 2 : pagination.getCurrentPage() + 1);
        execTraceResponse.setLogs(execTraceList);
         */

        ExecTraceResponse execTraceResponse = new ExecTraceResponse();
        execTraceResponse.setTotalPages(execTracePage.getTotalPages());
        execTraceResponse.setLogs(execTracePage.getContent());

        return ResponseEntity.ok(execTraceResponse);
    }

    @PostMapping(path = "/all/session/{session_id}")
    public ResponseEntity<List<ExecTrace>> getAllSessionActivity(@PathVariable("session_id") Long sessionId, @RequestBody Pagination pagination) {
        List<ExecTrace> execTraceList = agentService.getAllSessionActivities(sessionId, pagination);
        return ResponseEntity.ok(execTraceList);
    }
}
