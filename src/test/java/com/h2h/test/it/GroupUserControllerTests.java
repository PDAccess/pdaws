package com.h2h.test.it;

import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.pojo.group.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GroupUserControllerTests extends BaseIntegrationTests{

    @Test
    @Order(512)
    public void groupUserCRUDTest() {
        loginWithDefaultUserToken();
        String groupId = createGroup();
        String tenantId = createTenant();

        GroupUserCreateParams params = new GroupUserCreateParams();
        List<String> userList = new ArrayList<>();
        List<UserEntity> users = new ArrayList<>();
        for (int i = 0 ; i < 5 ; i++) {
            UserEntity user = createUser(tenantId);
            users.add(user);
            userList.add(user.getUserId());
        }

        params.setUserlist(userList);
        params.setRole(GroupRole.USER.name());


        ResponseEntity<Void> callVoid = call("/api/v1/group/user/" + groupId, HttpMethod.POST, params, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);


        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(System.currentTimeMillis()));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        LocalDateTime exp = LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());

        ResponseEntity<GroupUserGetParams[]> callGroupUsers = call("/api/v1/group/user/members/" + groupId, HttpMethod.GET, GroupUserGetParams[].class);
        assertThat(callGroupUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callGroupUsers.getBody()).isNotNull();
/*
        for (GroupUserGetParams groupUser : callGroupUsers.getBody()) {
            assertThat(userList.contains(groupUser.getUser().getUserId())).isTrue();
        }*/

        Pagination pagination = new Pagination();
        pagination.setCurrentPage(0);
        pagination.setPerPage(15);
        pagination.setSort("cretae");

        ResponseEntity<GroupsEntityWrapper[]> callUserGroup = call("/api/v1/group/user/user/" + userList.get(0), HttpMethod.POST, pagination, GroupsEntityWrapper[].class);
        assertThat(callUserGroup.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callUserGroup.getBody()).isNotNull();

        for (GroupsEntityWrapper userGroup : callUserGroup.getBody()) {
            assertThat(userGroup.getGroupId()).isEqualTo(groupId);
        }

        GroupUserDeleteParams deleteParams = new GroupUserDeleteParams();
        deleteParams.setGroupid(groupId);
        deleteParams.setUserid(userList.get(1));

        ResponseEntity<Integer> callVoidInteger = call("/api/v1/group/user/delete", HttpMethod.DELETE, deleteParams, Integer.class);
        assertThat(callVoidInteger.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callVoidInteger.getBody()).isEqualTo(1);


        callGroupUsers = call("/api/v1/group/user/members/" + groupId, HttpMethod.GET, GroupUserGetParams[].class);
        assertThat(callGroupUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callGroupUsers.getBody()).isNotNull();

        for (GroupUserGetParams groupUser : callGroupUsers.getBody()) {
            if (!userList.contains(groupUser.getUser().getUserId()))
                assertThat(userList.contains(groupUser.getUser().getUserId()));
        }

        callUserGroup = call("/api/v1/group/user/user/" + userList.get(1), HttpMethod.POST, pagination, GroupsEntityWrapper[].class);
        assertThat(callUserGroup.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callUserGroup.getBody()).length).isEqualTo(0);

        deleteTenant(tenantId);
        for (UserEntity user : users) {
            deleteUser(user.getUserId());
        }
        deleteGroup(groupId);
    }

    @Test
    @Order(513)
    public void groupUserFromParentTest() {
        loginWithDefaultUserToken();
        String parentGroupId = createGroup();
        String tenantId = createTenant();

        ResponseEntity<GroupUserGetParams[]> callGroupUsers = call("/api/v1/group/user/members/" + parentGroupId, HttpMethod.GET, GroupUserGetParams[].class);
        assertThat(callGroupUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callGroupUsers.getBody()).isNotNull();

        int initialMemberSize = callGroupUsers.getBody().length;

        GroupUserCreateParams params = new GroupUserCreateParams();
        List<UserEntity> parentUsers = new ArrayList<>();
        for (int i = 0 ; i < 5 ; i++) {
            UserEntity user = createUser(tenantId);
            parentUsers.add(user);
        }

        params.setUserlist(parentUsers.stream().map(UserEntity::getUserId).collect(Collectors.toList()));
        params.setRole(GroupRole.USER.name());

        ResponseEntity<Void> callVoid = call("/api/v1/group/user/" + parentGroupId, HttpMethod.POST, params, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callGroupUsers = call("/api/v1/group/user/members/" + parentGroupId, HttpMethod.GET, GroupUserGetParams[].class);
        assertThat(callGroupUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callGroupUsers.getBody()).isNotNull();
        assertThat(callGroupUsers.getBody().length).isEqualTo(initialMemberSize + parentUsers.size());

        GroupsEntityWrapper groupParams = new GroupsEntityWrapper();
        groupParams.setGroupType("public");
        groupParams.setGroupName("test group2");
        groupParams.setDescription("test description");
        groupParams.setGroupCategory("normal");
        groupParams.setParent(parentGroupId);
        ResponseEntity<String> callCreate = call("/api/v1/group/create", HttpMethod.POST, groupParams, String.class);
        assertThat(callCreate.getStatusCode()).isEqualTo(HttpStatus.OK);
        String childGroupId = callCreate.getBody();
        assertThat(childGroupId).isNotNull();

        params = new GroupUserCreateParams();
        List<UserEntity> childUsers = new ArrayList<>();
        for (int i = 0 ; i < 5 ; i++) {
            UserEntity user = createUser(tenantId);
            childUsers.add(user);
        }

        params.setUserlist(childUsers.stream().map(UserEntity::getUserId).collect(Collectors.toList()));
        params.setRole(GroupRole.USER.name());

        callVoid = call("/api/v1/group/user/" + childGroupId, HttpMethod.POST, params, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callGroupUsers = call("/api/v1/group/user/members/" + childGroupId, HttpMethod.GET, GroupUserGetParams[].class);
        assertThat(callGroupUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callGroupUsers.getBody()).isNotNull();
        assertThat(callGroupUsers.getBody().length).isEqualTo(childUsers.size() + parentUsers.size() + initialMemberSize);

        deleteTenant(tenantId);
        for (UserEntity user : parentUsers) {
            deleteUser(user.getUserId());
        }
        for (UserEntity user : childUsers) {
            deleteUser(user.getUserId());
        }
        deleteGroup(childGroupId);
        deleteGroup(parentGroupId);
    }

    @Test
    @Order(514)
    public void groupUserFromParentTestByAdd() {
        loginWithDefaultUserToken();
        String parentGroupId = createGroup();
        String tenantId = createTenant();

        GroupsEntityWrapper groupParams = new GroupsEntityWrapper();
        groupParams.setGroupType("public");
        groupParams.setGroupName("test group2");
        groupParams.setDescription("test description");
        groupParams.setGroupCategory("normal");
        groupParams.setParent(parentGroupId);
        ResponseEntity<String> callCreate = call("/api/v1/group/create", HttpMethod.POST, groupParams, String.class);
        assertThat(callCreate.getStatusCode()).isEqualTo(HttpStatus.OK);
        String childGroupId = callCreate.getBody();
        assertThat(childGroupId).isNotNull();

        UserEntity parentUser = createUser(tenantId);
        GroupUserCreateParams params = new GroupUserCreateParams();
        params.setUserlist(Collections.singletonList(parentUser.getUserId()));
        params.setRole(GroupRole.USER.name());
        ResponseEntity<Void> callVoid = call("/api/v1/group/user/" + parentGroupId, HttpMethod.POST, params, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<GroupUserGetParams[]> callGroupUsers = call("/api/v1/group/user/members/" + childGroupId, HttpMethod.GET, GroupUserGetParams[].class);
        assertThat(callGroupUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callGroupUsers.getBody()).isNotNull();
        assertThat(Arrays.stream(callGroupUsers.getBody()).anyMatch(groupUserGetParams -> groupUserGetParams.getUser().getUserId().equals(parentUser.getUserId()))).isTrue();

        deleteTenant(tenantId);
        deleteGroup(childGroupId);
        deleteGroup(parentGroupId);
    }

    @Test
    @Order(515)
    public void groupUserFromParentTestByDelete() {
        loginWithDefaultUserToken();
        String parentGroupId = createGroup();
        String tenantId = createTenant();

        GroupsEntityWrapper groupParams = new GroupsEntityWrapper();
        groupParams.setGroupType("public");
        groupParams.setGroupName("test group2");
        groupParams.setDescription("test description");
        groupParams.setGroupCategory("normal");
        groupParams.setParent(parentGroupId);
        ResponseEntity<String> callCreate = call("/api/v1/group/create", HttpMethod.POST, groupParams, String.class);
        assertThat(callCreate.getStatusCode()).isEqualTo(HttpStatus.OK);
        String childGroupId = callCreate.getBody();
        assertThat(childGroupId).isNotNull();

        UserEntity parentUser = createUser(tenantId);
        GroupUserCreateParams params = new GroupUserCreateParams();
        params.setUserlist(Collections.singletonList(parentUser.getUserId()));
        params.setRole(GroupRole.USER.name());
        ResponseEntity<Void> callVoid = call("/api/v1/group/user/" + parentGroupId, HttpMethod.POST, params, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<GroupUserGetParams[]> callGroupUsers = call("/api/v1/group/user/members/" + childGroupId, HttpMethod.GET, GroupUserGetParams[].class);
        assertThat(callGroupUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callGroupUsers.getBody()).isNotNull();
        assertThat(Arrays.stream(callGroupUsers.getBody()).anyMatch(groupUserGetParams -> groupUserGetParams.getUser().getUserId().equals(parentUser.getUserId()))).isTrue();

        GroupUserDeleteParams deleteParams = new GroupUserDeleteParams();
        deleteParams.setUserid(parentUser.getUserId());
        deleteParams.setGroupid(parentGroupId);
        callVoid = call("/api/v1/group/user/delete", HttpMethod.DELETE, deleteParams, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callGroupUsers = call("/api/v1/group/user/members/" + childGroupId, HttpMethod.GET, GroupUserGetParams[].class);
        assertThat(callGroupUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callGroupUsers.getBody()).isNotNull();
        assertThat(Arrays.stream(callGroupUsers.getBody()).noneMatch(groupUserGetParams -> groupUserGetParams.getUser().getUserId().equals(parentUser.getUserId()))).isTrue();

        deleteTenant(tenantId);
        deleteGroup(childGroupId);
        deleteGroup(parentGroupId);
    }
}
