package com.h2h.pda.api.services;

import com.h2h.pda.entity.*;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.pojo.*;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;
import com.h2h.pda.pojo.session.SessionsStatistics;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.repository.ActionRepository;
import com.h2h.pda.repository.SessionRepository;
import com.h2h.pda.service.api.ActionPdaService;
import com.h2h.pda.service.api.CredentialManager;
import com.h2h.pda.service.api.ServiceOps;
import com.h2h.pda.service.api.UsersOps;
import com.h2h.pda.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/session")
public class SessionController {

    private final Logger logger = LoggerFactory.getLogger(SessionController.class);

    public static final String RANGE_FROM_DATE_FORMAT = "MM-dd-yyyy";
    private static final String USERNAME = "username";
    private static final String START_TIME = "startTime";

    @Value("${database.url}")
    private String databaseUrl;

    @Value("${terminal.url}")
    private String terminalUrl;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    UsersOps usersOps;

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    CredentialManager credentialManager;

    @Autowired
    ActionPdaService actionPdaService;

    @GetMapping()
    public ResponseEntity<List<SessionEntityWrapper>> getSessions() {
        List<SessionEntity> desc = sessionRepository.findByStartimeDesc(PageRequest.of(0, 1000));
        return ResponseEntity.ok(desc.stream().map(s -> new SessionEntityWrapper(s)).collect(Collectors.toList()));
    }

    @GetMapping(path = "/{id}")
    public SessionEntityWrapper getSessionById(@PathVariable int id) {
        Optional<SessionEntity> session = sessionRepository.findById(id);
        return session.map(SessionEntityWrapper::new).get();
    }

    @GetMapping(path = "/actions/{sessionId}")
    public ResponseEntity<List<ActionWrapper>> getSessionActions(@PathVariable int sessionId) {

        List<ActionEntity> actionlist = new ArrayList<>();

        for (ActionEntity actionEntity : actionRepository.findAll()) {

            if (actionEntity.getSessionId() == sessionId) {
                actionlist.add(actionEntity);
            }
        }

        return ResponseEntity.ok(actionlist.stream().map(a -> new ActionWrapper().wrap(a)).collect(Collectors.toList()));
    }


    @PostMapping(path = "/credentials")
    public ResponseEntity<AuthCredentials> getauthcredientialss() {
        AuthCredentials auth = new AuthCredentials();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        auth.setClientToken((String) authentication.getDetails());
        auth.setSessionId(UUID.randomUUID().toString());
        return ResponseEntity.ok(auth);
    }

    @PostMapping(path = "/live")
    public ResponseEntity<List<SessionEntityWrapper>> getLiveSessions(@RequestBody Pagination pagination) {
        UserEntity userEntity = usersOps.securedUser();

        PageRequest req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(USERNAME));

        if (pagination.getSort() != null) {
            switch (pagination.getSort()) {
                case "userdesc":
                    req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(USERNAME).descending());

                    break;
                case "created":
                    req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(START_TIME));

                    break;
                case "createddesc":
                    req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(START_TIME).descending());

                    break;
                default:
                    break;
            }
        }

        List<String> usersFilter = pagination.getUsersFilter();
        List<String> servicesFilter = pagination.getServicesFilter();
        Specification<SessionEntity> where = null;

        if (userEntity.getRole().equals(UserRole.USER)) {
            where = Specification.where(SessionRepository.QueryFilter.findBySessionFilterByUrole(userEntity.getUsername())).and(Specification.where(SessionRepository.QueryFilter.findBySessionFilterByEndTime(true)));
        }

        if (usersFilter != null && !usersFilter.isEmpty()) {
            Specification<SessionEntity> filterUsers = Specification.where(SessionRepository.QueryFilter.findBySessionFilterByUsers(usersFilter));
            where = where == null ? filterUsers : where.and(filterUsers);
        }

        if (servicesFilter != null && !servicesFilter.isEmpty()) {
            Specification<SessionEntity> filterServices = Specification.where(SessionRepository.QueryFilter.findBySessionFilterByServices(servicesFilter));
            where = where == null ? filterServices : where.and(filterServices);
        }

        where = where == null ? Specification.where(SessionRepository.QueryFilter.extractSessionType("PDA")) :
                where.and(SessionRepository.QueryFilter.extractSessionType("PDA"));

        where = where.and(SessionRepository.QueryFilter.findBySessionFilterByEndTime(true));

        return ResponseEntity.ok(sessionRepository.findAll(where, req).getContent().stream().map(SessionEntityWrapper::new).collect(Collectors.toList()));
    }

    @PostMapping(path = "/lives/count")
    public ResponseEntity<Long> getLiveSessionsCount() {
        Long byLive;
        UserEntity userEntity = usersOps.securedUser();

        if (userEntity.getRole() == UserRole.USER) {
            byLive = sessionRepository.countByLiveUser(userEntity.getUsername());
        } else {
            byLive = sessionRepository.countByLive();
        }

        return new ResponseEntity<>(byLive, HttpStatus.OK);
    }

    @PostMapping(path = "/live/user")
    public ResponseEntity<List<SessionEntityWrapper>> liveSessionfilterbyuser(@RequestBody LiveSessionParams params) {

        ArrayList<SessionEntity> sessionlist = new ArrayList<>();
        if (!params.getUserEntities().isEmpty()) {
            for (UserDTO userEntity : params.getUserEntities()) {
                List<SessionEntity> sessions = sessionRepository.findByLiveUsername(userEntity.getUsername(),
                        PageRequest.of(params.getPagination().getCurrentPage(),
                                params.getPagination().getPerPage()));
                sessionlist.addAll(sessions);
            }

        } else {
            List<SessionEntity> sessions = sessionRepository.findByLive(PageRequest.of(params.getPagination().getCurrentPage(), params.getPagination().getPerPage()));
            sessionlist.addAll(sessions);
        }

        return new ResponseEntity<>(sessionlist.stream().map(s -> new SessionEntityWrapper(s)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @PostMapping(path = "/database/down/{sessionId}")
    public ResponseEntity<Void> dropDatabaseSession(@PathVariable String sessionId) {
        logger.info("dropDatabaseSession: {}", sessionId);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(databaseUrl + "/rest/serviceDown/" + sessionId, sessionId, String.class);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/terminal/down/{sessionId}")
    public ResponseEntity<Void> dropTerminalSession(@PathVariable int sessionId) {
        logger.info("dropTerminalSession: {}, {}", sessionId, terminalUrl);
        RestTemplate restTemplate = new RestTemplate();
        SessionEntity sessionEntity = sessionRepository.findBySessionId(sessionId);
        sessionEntity.setEndTime(new Timestamp(System.currentTimeMillis()));
        sessionRepository.save(sessionEntity);
        restTemplate.postForEntity(terminalUrl + "/rest/sessionDown/" + sessionId, sessionId, String.class);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "session/{serviceId}")
    public ResponseEntity<List<SessionEntityWrapper>> getSessionInvId(@PathVariable String serviceId) {
        List<SessionEntity> service = sessionRepository.findByService(serviceId, PageRequest.of(0, 10));
        return ResponseEntity.ok(service.stream().map(s -> new SessionEntityWrapper(s)).collect(Collectors.toList()));
    }

    @PostMapping(path = "/last")
    public ResponseEntity<SessionEntityWrapper> getlastsession(@RequestBody SessionDTO data) {
        List<SessionEntity> sessionlist;

        sessionlist = sessionRepository.findByLastSession(data.getInventoryId());
        return ResponseEntity.ok(new SessionEntityWrapper(sessionlist.get(0)));
    }

    @PostMapping(path = "/filter")
    public ResponseEntity<SessionFilterResponse> filterSessions(@RequestBody SearchParams2 searchParams) {
        UserEntity userEntity = usersOps.securedUser();

        Specification<SessionEntity> and = null;

        if (userEntity.getRole() != UserRole.ADMIN) {
            and = Specification
                    .where(SessionRepository.QueryFilter.findByUserList(Collections.singletonList(userEntity.getUserId())));
        } else {
            if (searchParams.getUserids() != null && !searchParams.getUserids().isEmpty()) {
                and = Specification
                        .where(SessionRepository.QueryFilter.findByUserList(searchParams.getUserids()));
            }
        }

        if (searchParams.getServiceids() != null && !searchParams.getServiceids().isEmpty()) {
            and = and == null ? Specification.where(SessionRepository.QueryFilter.findByServicenameList(searchParams.getServiceids())) :
                    and.and(SessionRepository.QueryFilter.findByServicenameList(searchParams.getServiceids()));
        }

        if (searchParams.getDateRange() != null && hasStartAndEndDate(searchParams)) {
            and = and == null ? Specification.where(SessionRepository.QueryFilter.findByDate(searchParams.getDateRange().getStart(), searchParams.getDateRange().getEnd())) :
                    and.and(SessionRepository.QueryFilter.findByDate(searchParams.getDateRange().getStart(), searchParams.getDateRange().getEnd()));
        }

        PageRequest req = PageRequest.of(searchParams.getPagination().getCurrentPage(), searchParams.getPagination().getPerPage(), Sort.by(USERNAME));

        if (searchParams.getPagination().getSort() != null) {
            if (searchParams.getPagination().getSort().equals("userdesc")) {
                req = PageRequest.of(searchParams.getPagination().getCurrentPage(), searchParams.getPagination().getPerPage(), Sort.by(USERNAME).descending());

            } else if (searchParams.getPagination().getSort().equals("created")) {
                req = PageRequest.of(searchParams.getPagination().getCurrentPage(), searchParams.getPagination().getPerPage(), Sort.by(START_TIME));

            } else if (searchParams.getPagination().getSort().equals("createddesc")) {
                req = PageRequest.of(searchParams.getPagination().getCurrentPage(), searchParams.getPagination().getPerPage(), Sort.by(START_TIME).descending());

            }
        }
        and = and == null ? Specification.where(SessionRepository.QueryFilter.findBySessionFilterByEndTime(false)) :
                and.and(SessionRepository.QueryFilter.findBySessionFilterByEndTime(false));

        Page all = sessionRepository.findAll(and, req);

        List<SessionEntity> content = all.getContent();
        Integer count = Math.toIntExact(all.getTotalElements());

        return ResponseEntity.ok(new SessionFilterResponse(content, count));
    }

    @GetMapping("/last/actions")
    public ResponseEntity<List<ServiceEntityWrapper>> getLastActions(){
        UserEntity userEntity = usersOps.securedUser();
        PageRequest req = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(START_TIME).descending());
        Specification<SessionEntity> and;
        and = Specification
                .where(SessionRepository.QueryFilter.findByUserList(Collections.singletonList(userEntity.getUserId())));
        List<String> services = new ArrayList<>();


        Page all = sessionRepository.findAll(and, req);

        List<SessionEntity> content = all.getContent();
        List<ServiceEntityWrapper> serviceEntities = new ArrayList<>();

        for (SessionEntity s : content) {
            if (!s.getServiceEntity().getInventoryId().equals("123") && !services.contains(s.getInventoryId())) {
                services.add(s.getInventoryId());
                Optional<ServiceEntity> service = serviceOps.byId(s.getInventoryId());
                if (service.isPresent()) {
                    ServiceEntityWrapper serviceEntityWrapper = new ServiceEntityWrapper(service.get());
                    serviceEntities.add(serviceEntityWrapper);
                }
            }
        }
        return new ResponseEntity<>(serviceEntities, HttpStatus.OK);
    }

    @GetMapping(path = "/last/connection/{serviceId}")
    public ResponseEntity<Timestamp> getServiceLastConnection(@PathVariable String serviceId) {
        UserEntity user = usersOps.securedUser();
        Optional<SessionEntity> session = sessionRepository.findFirstByUsernameAndInventoryIdOrderByStartTimeDesc(user.getUsername(), serviceId);
        if (session.isPresent()) {
            SessionEntity s = session.get();
            return new ResponseEntity<>(s.getStartTime(), HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping(path = "/create")
    public ResponseEntity<Integer> createSession(@RequestBody SessionEntityWrapper wrapper, HttpServletRequest request) throws Exception {

        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setEndTime(wrapper.getEndTime());
        sessionEntity.setExternalSessionId(wrapper.getExternalSessionId());
        sessionEntity.setInventoryId(wrapper.getInventoryId());
        sessionEntity.setSessionType(wrapper.getSessionType());
        sessionEntity.setStartTime(wrapper.getStartTime() == null ? new Timestamp(System.currentTimeMillis()) : wrapper.getStartTime());
        sessionEntity.setUsername(wrapper.getUsername());

        CredentialEntity credentialEntity = credentialManager.getCredential(wrapper.getCredentialId());
        if (credentialEntity != null) {
            sessionEntity.setCredentialEntity(credentialEntity);
        }

        if (wrapper.getUsername() == null || wrapper.getUsername().equals("")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = (String) authentication.getPrincipal();
            sessionEntity.setUsername(username);
        }

        if (wrapper.getIpAddress() == null || "".equals(wrapper.getIpAddress())) {
            String remoteAddr = RequestUtil.remoteAddress(request);
            sessionEntity.setIpAddress(remoteAddr);
        } else {
            sessionEntity.setIpAddress(wrapper.getIpAddress());
        }

        sessionEntity = sessionRepository.save(sessionEntity);
        getSessionEntityForMessage(sessionEntity);

        return new ResponseEntity<>(sessionEntity.getSessionId(), HttpStatus.OK);
    }

    @PutMapping(path = "/logout")
    public ResponseEntity<Void> dropSession(@RequestBody SessionEntityWrapper wrapper) throws Exception {
        Optional<SessionEntity> optionalSessionEntity = sessionRepository.findById(wrapper.getSessionId());
        if (optionalSessionEntity.isPresent()) {
            SessionEntity sessionEntity = optionalSessionEntity.get();
            sessionEntity.setEndTime(new Timestamp(System.currentTimeMillis()));
            sessionRepository.save(sessionEntity);
            getSessionEntityForMessage(sessionEntity);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping(path = "/external")
    public ResponseEntity<Void> setExternalSessionId(@RequestBody SessionEntityWrapper wrapper) {
        SessionEntity sessionEntity = sessionRepository.findBySessionId(wrapper.getSessionId());
        if (sessionEntity == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        sessionEntity.setExternalSessionId(wrapper.getExternalSessionId());
        sessionRepository.save(sessionEntity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void getSessionEntityForMessage(SessionEntity sessionEntity) throws IOException {
        Optional<UserEntity> byName = usersOps.byName(sessionEntity.getUsername());
        if (sessionEntity.getUserEntity() == null && byName.isPresent())
            sessionEntity.setUserEntity(byName.get());
        if (sessionEntity.getServiceEntity() == null) {
            Optional<ServiceEntity> serviceEntityOptional = serviceOps.byId(sessionEntity.getInventoryId());
            serviceEntityOptional.ifPresent(sessionEntity::setServiceEntity);
        }
    }

    @PostMapping(path = "/pda/logout")
    public ResponseEntity<Void> createPDAlogout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails tokenDetails = (TokenDetails) authentication.getDetails();
        //int sessionid = tokenDetails.getSessionId();

        Optional<SessionEntity> sessionEntity = sessionRepository.findById(0);
        if (sessionEntity.isPresent()) {
            SessionEntity session = sessionEntity.get();
            session.setEndTime(new Timestamp(System.currentTimeMillis()));
            sessionRepository.save(session);
            actionPdaService.saveAction("Logout");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @GetMapping(path = "/statistic/admin")
    public ResponseEntity<List<SessionsStatistics>> sessionsStat() {

        List<SessionsStatistics> sessionsByDate = sessionRepository.findSessionsByDate(LocalDate.now().
                minusYears(1).format(DateTimeFormatter.ofPattern(RANGE_FROM_DATE_FORMAT)));

        return new ResponseEntity<>(sessionsByDate, HttpStatus.OK);
    }

    @GetMapping(path = "/statistic/user/{userId}")
    public ResponseEntity<List<SessionsStatistics>> sessionsStatsUser(@PathVariable String userId) {
        Optional<UserEntity> entity = usersOps.byId(userId);

        if (entity.isPresent()) {
            UserEntity userEntity = entity.get();
            List<SessionsStatistics> sessionsByDate = sessionRepository.findSessionsByDateForUsername(userEntity.getUsername(), LocalDate.now().
                    minusYears(1).format(DateTimeFormatter.ofPattern(RANGE_FROM_DATE_FORMAT)));

            return new ResponseEntity<>(sessionsByDate, HttpStatus.OK);
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/statistic/service/{serviceId}")
    public ResponseEntity<List<SessionsStatistics>> sessionsStatforService(@PathVariable String serviceId) {

        List<SessionsStatistics> sessionsByDate = sessionRepository.findSessionsByDateForService(serviceId, LocalDate.now().
                minusYears(1).format(DateTimeFormatter.ofPattern(RANGE_FROM_DATE_FORMAT)));

        return new ResponseEntity<>(sessionsByDate, HttpStatus.OK);
    }

    @PostMapping(path = "database/filter/{sessionId}")
    public ResponseEntity<List<ActionWrapper>> databaseSessionFilter(@PathVariable int sessionId, @RequestBody String query) {
        Optional<SessionEntity> sessionEntity = sessionRepository.findById(sessionId);
        if (sessionEntity.isPresent()) {
            SessionEntity session = sessionEntity.get();
            List<ActionEntity> actionEntityList = actionRepository.findByQueryTypeDatabaseSessions(session.getSessionId(), query);
            List<ActionEntity> actionEntityListLower = actionRepository.findByQueryTypeDatabaseSessions(session.getSessionId(), query.toLowerCase());
            actionEntityList.addAll(actionEntityListLower);
            return new ResponseEntity<>(actionEntityList.stream().map(a -> new ActionWrapper().wrap(a)).collect(Collectors.toList()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    private Boolean hasStartAndEndDate(SearchParams2 searchParams) {
        return (searchParams.getDateRange().getStart() != null && searchParams.getDateRange().getEnd() != null);
    }
}