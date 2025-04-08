package com.h2h.pda.api.services;

import com.h2h.pda.entity.ActionEntity;
import com.h2h.pda.entity.SessionEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.pojo.ActionPaginationParams;
import com.h2h.pda.pojo.ActionPayload;
import com.h2h.pda.pojo.ActionResponse;
import com.h2h.pda.pojo.ActionWrapper;
import com.h2h.pda.pojo.service.ActionsStatistics;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.repository.ActionRepository;
import com.h2h.pda.repository.SessionRepository;
import com.h2h.pda.service.api.AlarmService;
import com.h2h.pda.service.api.UsersOps;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/action")
public class ActionController {

    public static final String SESSION_ENTITY = "sessionEntity";
    static final String START_TIME = "sessionEntity.startTime";
    static final String USERNAME = "sessionEntity.username";

    @Autowired
    ActionRepository actionRepository;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    UsersOps usersOps;

    @Autowired
    AlarmService alarmService;

    @PostMapping()
    public ResponseEntity<ActionResponse> getActions(@RequestBody ActionPaginationParams param) {
        UserEntity userEntity = usersOps.securedUser();

        PageRequest req = PageRequest.of(param.getCurrentPage(), param.getPerPage(), Sort.by(USERNAME));
        if (param.getSort() != null) {
            switch (param.getSort()) {
                case "userdesc":
                    req = PageRequest.of(param.getCurrentPage(), param.getPerPage(), Sort.by(USERNAME).descending());
                    break;
                case "createddesc":
                    req = PageRequest.of(param.getCurrentPage(), param.getPerPage(), Sort.by(START_TIME).descending());
                    break;
                case "created":
                default:
                    req = PageRequest.of(param.getCurrentPage(), param.getPerPage(), Sort.by(START_TIME));
                    break;
            }
        }

        Specification<ActionEntity> where = null;
        String filter = param.getFilter();

        if (userEntity.getRole() == UserRole.USER) {
            where = Specification.where(ActionRepository.QueryFilter.findByActionsForUser(userEntity.getUsername()));
        }

        if (param.getDateRange() != null && hasStartAndEndDate(param)) {
            where = where == null ? Specification.where(ActionRepository.QueryFilter.findByDate(param.getDateRange().getStart(), param.getDateRange().getEnd())) :
                    where.and(ActionRepository.QueryFilter.findByDate(param.getDateRange().getStart(), param.getDateRange().getEnd()));
        }

        if (filter != null && !filter.isEmpty()) {
            Specification<ActionEntity> filterWhere = Specification.where(ActionRepository.QueryFilter.findByActionsFilterByUsername(filter)
                    .or(ActionRepository.QueryFilter.findByActionsFilterByAction(filter))
                    .or(ActionRepository.QueryFilter.findByActionsFilterByService(filter)));

            where = where == null ? filterWhere : where.and(filterWhere);
        }

        Page<ActionEntity> all = actionRepository.findAll(where, req);
        List<ActionEntity> content = all.getContent();


        return ResponseEntity.ok(new ActionResponse(content.stream().map(a -> new ActionWrapper().wrap(a)).collect(Collectors.toList()), (int) all.getTotalElements()));
    }

    // TODO: Entity Fix
    @GetMapping(path = "/all")
    public ResponseEntity<List<ActionEntity>> getAllActions() {
        UserEntity userEntity = usersOps.securedUser();

        PageRequest req = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(USERNAME));
        Specification<ActionEntity> where = null;

        if (userEntity.getRole() == UserRole.USER) {
            where = Specification.where(ActionRepository.QueryFilter.findByActionsForUser(userEntity.getUsername()));
        }

        Page<ActionEntity> all = actionRepository.findAll(where, req);
        List<ActionEntity> content = all.getContent();

        return new ResponseEntity<>(content, HttpStatus.OK);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<Void> createAction(@RequestBody ActionWrapper actionWrapper) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        ActionPayload actionPayload = new ActionPayload();

        SessionEntity sessionEntity = sessionRepository.findBySessionId(actionWrapper.getSessionId());
        if (sessionEntity == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (actionWrapper.getActionTime() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (actionWrapper.getActionTime().after(new Timestamp(System.currentTimeMillis())))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (actionWrapper.getProxyAction() == null || actionWrapper.getProxyAction().equals(""))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        actionPayload.setActionTime(actionWrapper.getActionTime());
        actionPayload.setProxyAction(actionWrapper.getProxyAction());
        actionPayload.setSessionId(actionWrapper.getSessionId());
        actionPayload.setUsername(username);

        alarmService.pushAction(actionPayload);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO: Entity Fix
    @PostMapping(path = "/user/{userid}")
    public List<ActionEntity> getActionByUserid(@PathVariable String userid, @RequestBody ActionPaginationParams param) {

        Optional<UserEntity> userEntity = usersOps.byId(userid);
        if (userEntity.isPresent()) {
            UserEntity user = userEntity.get();
            return actionRepository.findByUsername(user.getUsername(), PageRequest.of(param.getCurrentPage(),
                    param.getPerPage(),
                    Sort.by(Sort.Direction.DESC, START_TIME)));
        }

        return Collections.emptyList();
    }

    // TODO: Entity Fix
    @PostMapping(path = "/service/{serviceId}")
    public List<ActionEntity> getServiceActions(@PathVariable String serviceId, @RequestBody ActionPaginationParams param) {
        return actionRepository.findByServiceId(serviceId, PageRequest.of(param.getCurrentPage(), param.getPerPage()));
    }

    // TODO: make it in service layer
    @PostMapping(path = "/pda")
    @Deprecated
    public ResponseEntity<Void> createActionPDA(@RequestBody String proxyAction) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails tokenDetails = (TokenDetails) authentication.getDetails();

        ActionEntity action = new ActionEntity();
        action.setSessionId(0);
        action.setActionTime(new Timestamp(System.currentTimeMillis()));
        action.setProxyAction(proxyAction);

        actionRepository.save(action);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @GetMapping(path = "/statistics")
    public ResponseEntity<List<ActionsStatistics>> actionStatForAdmin() {

        List<ActionsStatistics> stats = actionRepository.findActionsByDateForAdmin(Timestamp.valueOf(LocalDateTime.now().minusYears(1)));

        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @GetMapping(path = "/statistics/user/{username}")
    public ResponseEntity<List<ActionsStatistics>> actionStatForUsername(@PathVariable String username) {

        List<ActionsStatistics> stats = actionRepository.findActionsByDateForUsername(username, Timestamp.valueOf(LocalDateTime.now().minusYears(1)));

        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @GetMapping(path = "/statistics/service/{serviceId}")
    public ResponseEntity<List<ActionsStatistics>> actionStatForService(@PathVariable String serviceId) {

        List<ActionsStatistics> stats = actionRepository.findActionsByDateForService(serviceId, Timestamp.valueOf(LocalDateTime.now().minusYears(1)));

        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    private Boolean hasStartAndEndDate(ActionPaginationParams searchParams) {
        return (searchParams.getDateRange().getStart() != null && searchParams.getDateRange().getEnd() != null);
    }
}