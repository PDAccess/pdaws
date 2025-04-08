package com.h2h.pda.api.groups;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.pojo.PropertyParams;
import com.h2h.pda.pojo.group.*;
import com.h2h.pda.pojo.system.SystemSettingTags;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.repository.ConnectionUserRepository;
import com.h2h.pda.service.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/v1/group")
public class GroupsController {

    private static final String LDAP = "ldap";
    private static final String CREATED_AT = "createdAt";

    @Autowired
    GroupOps groupOps;

    @Autowired
    UsersOps usersOps;

    @Autowired
    JobManager jobManager;

    @Autowired
    ConnectionUserRepository connectionUserRepository;

    @Autowired
    ActionPdaService actionPdaService;

    @Autowired
    SystemSettings systemSettings;

    @GetMapping("/property/get")
    public ResponseEntity<GroupProperty> getGroupProperty(@RequestParam("groupid") String groupId, @RequestParam String key) {
        GroupProperties groupProperty = GroupProperties.of(key);
        Optional<GroupsEntity> byId = groupOps.byId(groupId);
        Optional<GroupProperty> first = byId.get().getProperties().stream().filter(s -> s.getKey() == groupProperty).findFirst();

        return first.map(property -> new ResponseEntity<>(property, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @PostMapping(path = "/property/save")
    public ResponseEntity<Void> saveGroupProperties(@RequestBody PropertyParams params) {
        Optional<GroupsEntity> optionalGroupsEntity = groupOps.byId(params.getGroupId());
        if (optionalGroupsEntity.isPresent()) {
            GroupsEntity groupsEntity = optionalGroupsEntity.get();
            GroupProperty groupProperty = new GroupProperty().setKey(GroupProperties.ofElseThrow(params.getKey())).setValue(params.getValue());
            groupsEntity.getProperties().remove(groupProperty);
            groupsEntity.getProperties().add(groupProperty);
            groupOps.update(groupsEntity);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();

    }

    @PostMapping(path = "/create")
    public ResponseEntity<String> addGroup(@RequestBody GroupsEntityWrapper data) {

        Optional<GroupsEntity> optionalGroupsEntity = groupOps.byName(data.getGroupName());
        if (optionalGroupsEntity.isPresent()) {
            return new ResponseEntity<>("Group All Ready Exists", HttpStatus.CONFLICT);
        }

        Optional<GroupCategory> groupCategory = GroupCategory.of(data.getGroupCategory());

        if (!groupCategory.isPresent()) {
            return new ResponseEntity<>("Empty or Unknown Group Category", HttpStatus.BAD_REQUEST);
        }

        UserEntity securedUser = usersOps.securedUser();
        GroupsEntity groupsEntity = new GroupsEntity();
        groupsEntity.setDescription(data.getDescription());
        groupsEntity.setGroupName(data.getGroupName());
        groupsEntity.setGroupType(data.getGroupType());
        groupsEntity.setLdapRdn(data.getLdapRdn());
        groupsEntity.setLdapDn(data.getLdapDn());
        groupsEntity.setGroupCategory(groupCategory.get());

        if (StringUtils.hasText(data.getParent())) {
            System.out.println("Parent is found");
            Optional<GroupsEntity> optionalParentGroup = groupOps.byId(data.getParent());
            optionalParentGroup.ifPresent(groupsEntity::setParent);
        }

        String groupId = groupOps.newGroup(groupsEntity, securedUser);

        if (systemSettings.checkTagValue(SystemSettingTags.ADD_ALL_ADMIN_TO_GROUPS, "true")) {
            List<String> userIds;
            if (systemSettings.checkTagValue(SystemSettingTags.ADD_EXTERNAL_ADMIN_TO_GROUPS, "true")) {
                userIds = usersOps.findUsersByExternalAndRole(true, UserRole.ADMIN).stream().map(UserEntity::getUserId).collect(Collectors.toList());
            } else {
                userIds = usersOps.findUsersByRole(UserRole.ADMIN).stream().map(UserEntity::getUserId).collect(Collectors.toList());
            }
            groupOps.addUsersTo(groupId, userIds, GroupRole.ADMIN);
        }

        actionPdaService.saveAction(String.format("%s group is created", groupsEntity.getGroupName()));

        return new ResponseEntity<>(groupId, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<GroupsEntityWrapper>> queryGroups() {
        return queryGroups("yours", new Pagination().setCurrentPage(0).setPerPage(Integer.MAX_VALUE));
    }

    @PostMapping("/counters")
    public ResponseEntity<UserGroupCounter> userCounters() {
        UserEntity securedUser = usersOps.securedUser();
        UserGroupCounter userGroupCounter = groupOps.userCounters(securedUser.getUserId());
        return ResponseEntity.ok(userGroupCounter);
    }

    @PostMapping("/query/{type}")
    public ResponseEntity<List<GroupsEntityWrapper>> queryGroups(@PathVariable("type") String type, @RequestBody(required = false) Pagination pagination) {
        UserEntity securedUser = usersOps.securedUser();
        PageRequest request = pagination.toRequest(this::getPage);

        GroupRole[] roles = "all".equals(type) ? new GroupRole[]{GroupRole.ADMIN, GroupRole.USER}
                : "yours".equals(type) ? new GroupRole[]{GroupRole.ADMIN} : new GroupRole[]{GroupRole.USER};

        List<GroupsEntity> groups = groupOps.searchBy(securedUser.getUserId(), request, GroupCategory.NORMAL, pagination.getFilter(), roles);

        List<GroupsEntityWrapper> collect = new ArrayList<>();

        for (GroupsEntity groupsEntity : groups) {
            GroupsEntityWrapper groupsEntityWrapper = new GroupsEntityWrapper();
            groupsEntityWrapper.setGroupId(groupsEntity.getGroupId());
            groupsEntityWrapper.setGroupName(groupsEntity.getGroupName());
            groupsEntityWrapper.setDescription(groupsEntity.getDescription());
            groupsEntityWrapper.setCreateTime(groupsEntity.getCreatedAt());

            if (groupsEntity.getParent() != null) {
                groupsEntityWrapper.setParent(groupsEntity.getParent().getGroupId());
            }

            GroupRole groupRole;
            if (groupOps.isEqualsMembershipRole(groupsEntityWrapper.getGroupId(), securedUser.getUserId(), GroupRole.ADMIN)) {
                groupRole = GroupRole.ADMIN;
            } else {
                groupRole = GroupRole.USER;
            }

            groupsEntityWrapper.setGroupCounter(groupOps.counters(groupsEntity.getGroupId(), groupRole));
            groupsEntityWrapper.setOwnMembership(groupRole);

            collect.add(groupsEntityWrapper);
        }

        return ResponseEntity.ok(collect);
    }

    public Sort getPage(String sort) {
        Sort srt = Sort.by(CREATED_AT);
        if (sort != null) {
            switch (sort) {
                case "create":
                    srt = Sort.by(CREATED_AT);
                    break;
                case "create-desc":
                    srt = Sort.by(CREATED_AT).descending();
                    break;
                case "name":
                    srt = Sort.by("groupName").ascending();
                    break;
                case "name-desc":
                    srt = Sort.by("groupName").descending();
                    break;
            }
        }

        return srt;
    }

    @DeleteMapping(path = "/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable String groupId) {
        groupOps.deleteById(groupId);
        actionPdaService.saveAction(String.format("%s group is deleted", groupId));
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/info/{groupId}")
    public ResponseEntity<GroupsEntityWrapper> groupInfo(@PathVariable String groupId) {
        Optional<GroupsEntity> optionalGroupsEntity = groupOps.byId(groupId);
        Optional<GroupsEntityWrapper> optionalGroupsEntityWrapper = optionalGroupsEntity.map(GroupsEntityWrapper::new);
        if (!optionalGroupsEntityWrapper.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        GroupsEntityWrapper groupsEntityWrapper = optionalGroupsEntityWrapper.get();

        GroupsEntity groupsEntity = optionalGroupsEntity.get();
        if (groupsEntity.getParent() != null) {
            groupsEntityWrapper.setParent(groupsEntity.getParent().getGroupId());
        }

        GroupRole groupRole;
        if (groupOps.isEqualsMembershipRole(groupsEntityWrapper.getGroupId(), usersOps.securedUser().getUserId(), GroupRole.ADMIN)) {
            groupRole = GroupRole.ADMIN;
        } else {
            groupRole = GroupRole.USER;
        }
        groupsEntityWrapper.setOwnMembership(groupRole);

        return new ResponseEntity<>(groupsEntityWrapper, HttpStatus.OK);
    }

    @PutMapping(path = "/{groupId}")
    public ResponseEntity<String> updateGroup(@PathVariable String groupId,
                                              @RequestParam(name = "group_name") String name,
                                              @RequestParam(name = "group_description") String description,
                                              @RequestParam(name = "group_parent", required = false) String parent) {

        Optional<GroupsEntity> optionalGroupsEntity = groupOps.byId(groupId);
        if (optionalGroupsEntity.isPresent()) {
            GroupsEntity groupsEntity = optionalGroupsEntity.get();
            groupsEntity.setGroupName(name);
            groupsEntity.setDescription(description);
            groupOps.update(groupsEntity, parent);
            actionPdaService.saveAction(String.format("The name of the group with %s ids has been changed to %s", groupId, name));
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Group not found:" + groupId);
        }
    }

    // TODO: Entity Fix
    @PostMapping(path = "/ldap/logs/{group_id}")
    public ResponseEntity<List<LdapSynchronizationLogEntity>> getLdapSyncLogs(@PathVariable("group_id") String groupId, @RequestBody Pagination pagination) {
        PageRequest req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(CREATED_AT).descending());
        Iterable<LdapSynchronizationLogEntity> synchronizationLogEntities = jobManager.currentScheduleList(groupId, pagination.getFilter(), req);
        return new ResponseEntity<>(StreamSupport.stream(synchronizationLogEntities.spliterator(), false).collect(Collectors.toList()), HttpStatus.OK);
    }

    @PostMapping(path = "/public/ldap/log")
    public ResponseEntity<Void> saveLdapSyncLogs(@RequestBody LdapSynchronizationLogEntity logEntity) {
        jobManager.newJobRequest(logEntity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO: handle in service logic
    @PostMapping(path = "/ldap/action")
    @Deprecated
    public ResponseEntity<Void> saveLdapAction(@RequestBody ActionEntity actionEntity) {
//        UserEntity user = usersOps.securedUser();
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        TokenDetails tokenDetails = (TokenDetails) authentication.getDetails();
//        int sessionid = tokenDetails.getSessionid();
//
//        actionEntity.setSessionId(sessionid);
//        actionRepository.save(actionEntity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/public/ldap/user")
    public ResponseEntity<List<UserEntity>> getLdapGroupUser(@RequestBody GroupUserDeleteParams params) {

        List<UserEntity> userEntities = groupOps.effectiveUsers(params.getGroupid());

        return new ResponseEntity<>(userEntities, HttpStatus.OK);
    }

    @GetMapping(path = "/connection-users/{group_id}")
    @Deprecated
    public ResponseEntity<List<ConnectionUserEntity>> getConnectionUsers(@PathVariable("group_id") String groupId) {
        List<ConnectionUserEntity> users = connectionUserRepository.findByGroupId(groupId);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping(path = "/parent")
    public ResponseEntity<List<GroupsEntityWrapper>> getParentGroup() {
        List<GroupsEntity> groupsEntities = groupOps.searchParentGroupBy(usersOps.securedUser().getUserId(), GroupRole.ADMIN);
        return ResponseEntity.ok(groupsEntities.stream().map(groupsEntity -> new GroupsEntityWrapper(groupsEntity)).collect(Collectors.toList()));
    }
}
