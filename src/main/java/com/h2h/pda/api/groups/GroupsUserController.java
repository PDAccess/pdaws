package com.h2h.pda.api.groups;

import com.h2h.pda.entity.GroupUserEntity;
import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.pojo.UserDTO;
import com.h2h.pda.pojo.group.*;
import com.h2h.pda.service.api.ActionPdaService;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.UsersOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/group/user")
public class GroupsUserController {
    private final Logger log = LoggerFactory.getLogger(GroupsUserController.class);

    @Autowired
    GroupOps groupOps;

    @Autowired
    UsersOps usersOps;

    @Autowired
    ActionPdaService actionPdaService;

    @PostMapping(path = "/{groupId}")
    public ResponseEntity<String> addGroupUser(@PathVariable String groupId, @RequestBody GroupUserCreateParams params) {
        log.info("adding group user");
        UserEntity user = usersOps.securedUser();

        Optional<GroupsEntity> optionalGroupsEntity = groupOps.byId(groupId);
        if (!optionalGroupsEntity.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);


        if (params.getExpiredate() != null) {
            if (params.getExpiredate().before(new Timestamp(System.currentTimeMillis())))
                return new ResponseEntity<>("Expire Date cannot be before now!", HttpStatus.BAD_REQUEST);
        }

        GroupRole groupRole = GroupRole.of(params.getRole());

        int i = groupOps.addUsersTo(groupId, params.getUserlist(), GroupMembership.NORMAL, params.getExpiredate(), groupRole);

        actionPdaService.saveAction("Group users updated");

        return ResponseEntity.ok(Integer.toString(i));
    }

    @GetMapping(path = "members/{groupId}")
    public ResponseEntity<List<GroupUserGetParams>> groupMembers(@PathVariable String groupId) {
        List<GroupUserGetParams> getParams = new ArrayList<>();
        Iterable<GroupUserEntity> groupUserEntities = groupOps.effectiveMembers(groupId);

        for (GroupUserEntity groupUserEntity : groupUserEntities) {
            GroupUserGetParams groupParam = new GroupUserGetParams();
            groupParam.setUser(new UserDTO(groupUserEntity.getUser()));
            groupParam.setCreatedAt(groupUserEntity.getCreatedAt());
            groupParam.setExpiredatetime(groupUserEntity.getExpireDate());
            groupParam.setMembershipType(groupUserEntity.getMembershipType() != null ? groupUserEntity.getMembershipType().name() : null);
            groupParam.setMembershipRole(groupUserEntity.getMembershipRole() != null ? groupUserEntity.getMembershipRole().name() : null);
            getParams.add(groupParam);
        }
        return new ResponseEntity<>(getParams, HttpStatus.OK);
    }

    @GetMapping(path = "user/{userId}")
    public ResponseEntity<List<GroupsEntityWrapper>> getUserGroups(@PathVariable String userId) {
        return getUserGroups(userId, Pagination.of(0, Integer.MAX_VALUE));
    }

    @PostMapping(path = "user/{userid}")
    public ResponseEntity<List<GroupsEntityWrapper>> getUserGroups(@PathVariable String userid, @RequestBody Pagination pagination) {

        PageRequest request = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage());
        if (pagination.getSort().equals("create"))
            request = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("createdAt"));
        else if (pagination.getSort().equals("create-desc"))
            request = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("createdAt").descending());


        List<GroupsEntity> groupUserEntities = groupOps.searchBy(userid, request, GroupCategory.NORMAL);

        actionPdaService.saveAction("Group users updated");

        return new ResponseEntity<>(groupUserEntities.stream()
                .map(g -> new GroupsEntityWrapper(g)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<Integer> deleteGroupUser(@RequestBody GroupUserDeleteParams param) {
        Optional<GroupsEntity> optionalGroupsEntity = groupOps.byId(param.getGroupid());
        if (!optionalGroupsEntity.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Integer removeUsersFrom = groupOps.removeUsersFrom(param.getGroupid(), Collections.singletonList(param.getUserid()));
        actionPdaService.saveAction(String.format("Group user with id %s is deleted", param.getUserid()));
        return ResponseEntity.ok(removeUsersFrom);
    }
}