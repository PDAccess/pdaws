package com.h2h.pda.api.auth;

import com.h2h.pda.entity.AuthenticationAttemptEntity;
import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.AuthenticationLogResponse;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.pojo.auth.AuthenticationAttemptEntityWrapper;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.service.api.AuthenticationService;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.UsersOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auths")
public class AuthSessionController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UsersOps usersOps;

    @Autowired
    GroupOps groupOps;

    @PostMapping(path = "/live")
    public ResponseEntity<Page<AuthenticationAttemptEntityWrapper>> authSessions(@RequestBody Pagination pagination) {
        UserEntity userEntity = usersOps.securedUser();

        Page<AuthenticationAttemptEntity> entities = authenticationService.byUserService(userEntity.getUserId(), pagination.getFilter(), createSort(pagination));
        Page<AuthenticationAttemptEntityWrapper> collect = entities.map(a -> new AuthenticationAttemptEntityWrapper().wrap(a));

        return new ResponseEntity<>(collect, HttpStatus.OK);
    }

    @PostMapping(path = "/user/{userId}")
    public ResponseEntity<Page<AuthenticationAttemptEntityWrapper>> userAuthSessions(@PathVariable String userId, @RequestBody Pagination pagination) {
        Optional<UserEntity> optionalUser = usersOps.byId(userId);
        UserEntity userEntity = usersOps.securedUser();

        if (optionalUser.isPresent() && (userEntity.getUserId().equals(userId) || userEntity.getRole() == UserRole.ADMIN)) {
            Page<AuthenticationAttemptEntity> entities = authenticationService.byUserName(optionalUser.get().getUsername(), pagination.getFilter(), createSort(pagination));
            Page<AuthenticationAttemptEntityWrapper> collect = entities.map(a -> new AuthenticationAttemptEntityWrapper().wrap(a));

            return new ResponseEntity<>(collect, HttpStatus.OK);
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/service/{serviceId}")
    public ResponseEntity<AuthenticationLogResponse> serviceAuthSessions(@PathVariable String serviceId, @RequestBody Pagination pagination) {
        Page<AuthenticationAttemptEntity> attemptEntityPage = authenticationService.byService(serviceId, pagination.getFilter(), createSort(pagination));
        List<AuthenticationAttemptEntityWrapper> authenticationAttemptEntityWrappers = attemptEntityPage.getContent().stream().map(a -> new AuthenticationAttemptEntityWrapper().wrap(a)).collect(Collectors.toList());

        AuthenticationLogResponse authenticationLogResponse = new AuthenticationLogResponse();
        authenticationLogResponse.setLogs(authenticationAttemptEntityWrappers);
        authenticationLogResponse.setTotalPages(attemptEntityPage.getTotalPages());

        return new ResponseEntity<>(authenticationLogResponse, HttpStatus.OK);
    }

    @PostMapping(path = "/group/{groupId}")
    public ResponseEntity<AuthenticationLogResponse> groupAuthSessions(@PathVariable String groupId, @RequestBody Pagination pagination) {
        List<String> serviceIds = new ArrayList<>();
        for (ServiceEntity serviceEntity:groupOps.effectiveServices(groupId)) {
            serviceIds.add(serviceEntity.getInventoryId());
        }

        Page<AuthenticationAttemptEntity> attemptEntityPage = authenticationService.byServices(serviceIds, pagination.getFilter(), createSort(pagination));
        List<AuthenticationAttemptEntityWrapper> authenticationAttemptEntityWrappers = attemptEntityPage.getContent().stream().map(a -> new AuthenticationAttemptEntityWrapper().wrap(a)).collect(Collectors.toList());

        AuthenticationLogResponse authenticationLogResponse = new AuthenticationLogResponse();
        authenticationLogResponse.setLogs(authenticationAttemptEntityWrappers);
        authenticationLogResponse.setTotalPages(attemptEntityPage.getTotalPages());

        return new ResponseEntity<>(authenticationLogResponse, HttpStatus.OK);
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @PostMapping(path = "/portal")
    public ResponseEntity<Page<AuthenticationAttemptEntityWrapper>> allAuthentication(@RequestBody Pagination pagination) {
        String filter = pagination.getFilter();
        Page<AuthenticationAttemptEntity> pageEntity = authenticationService.history(filter, createSort(pagination));
        return new ResponseEntity<>(pageEntity.map(a -> new AuthenticationAttemptEntityWrapper().wrap(a)), HttpStatus.OK);
    }

    PageRequest createSort(Pagination pagination) {
        PageRequest req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("username"));

        if (pagination.getSort() != null) {
            if (pagination.getSort().equals("userdesc")) {
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("username").descending());

            } else if (pagination.getSort().equals("created")) {
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("attemptedAt"));

            } else if (pagination.getSort().equals("createddesc")) {
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("attemptedAt").descending());

            }
        }

        return req;
    }

    @GetMapping(path = "/logout")
    public ResponseEntity<Void> userLogout() {
        authenticationService.logout();
        return ResponseEntity.ok().build();
    }
}