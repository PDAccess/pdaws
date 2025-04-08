package com.h2h.test.it;

import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.pojo.SettingParam;
import com.h2h.pda.pojo.group.*;
import com.h2h.pda.pojo.system.SystemSettingTags;
import com.h2h.pda.service.api.GroupOps;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupControllerTests extends BaseIntegrationTests{

    @Autowired
    GroupOps groupOps;

    final String url = "/api/v1/group/";
    @Test
    @Order(400)
    public void groupTests() {
        loginWithDefaultUserToken();
        GroupsEntityWrapper params = new GroupsEntityWrapper();
        params.setGroupType("public");
        params.setGroupName("test group");
        params.setDescription("test description");
        params.setGroupCategory("normal");
        ResponseEntity<String> createCall = call(url + "create", HttpMethod.POST, params, String.class);
        assertThat(createCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        String groupId = createCall.getBody();

        ResponseEntity<GroupsEntity[]> allGroupList = call(url + "query/yours", HttpMethod.POST, new Pagination(15, 10, "create-desc"), GroupsEntity[].class);
        assertThat(allGroupList.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<GroupsEntity[]> groupListWithPagination = call(url + "query/all", HttpMethod.POST, new Pagination(15, 10, "create-desc"), GroupsEntity[].class);
        assertThat(groupListWithPagination.getStatusCode()).isEqualTo(HttpStatus.OK);

        deleteGroup(groupId);
    }

    @Test
    @Order(401)
    public void groupMembershipTestsByNormalMembership() {
        loginWithDefaultUserToken();

        List<SettingParam> settingParams = new ArrayList<>();
        settingParams.add(new SettingParam(SystemSettingTags.ADD_ALL_ADMIN_TO_GROUPS, "false"));
        settingParams.add(new SettingParam(SystemSettingTags.ADD_EXTERNAL_ADMIN_TO_GROUPS, "false"));

        ResponseEntity<Void> call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        AssertionsForClassTypes.assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        String tenantId = createTenant();
        UserEntity adminUser = createAdminUser(tenantId);
        UserEntity user = createUser(tenantId);
        UserEntity externalAdminUser = createExternalAdminUser(tenantId);
        UserEntity externalUser = createExternalUser(tenantId);

        GroupsEntityWrapper params = new GroupsEntityWrapper();
        params.setGroupType("public");
        params.setGroupName("test group");
        params.setDescription("test description");
        params.setGroupCategory("normal");
        ResponseEntity<String> createCall = call(url + "create", HttpMethod.POST, params, String.class);
        assertThat(createCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        String groupId = createCall.getBody();

        ResponseEntity<GroupUserGetParams[]> callGroupUsers = call("/api/v1/group/user/members/" + groupId, HttpMethod.GET, GroupUserGetParams[].class);
        AssertionsForClassTypes.assertThat(callGroupUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        AssertionsForClassTypes.assertThat(callGroupUsers.getBody()).isNotNull();

        List<String> memberList = Arrays.stream(callGroupUsers.getBody()).map(groupUserGetParams -> groupUserGetParams.getUser().getUserId()).collect(Collectors.toList());
        assertThat(memberList).doesNotContain(adminUser.getUserId(), externalAdminUser.getUserId(), user.getUserId(), externalUser.getUserId());

        deleteGroup(groupId);
        hardDeleteUser(adminUser.getUserId());
        hardDeleteUser(user.getUserId());
        hardDeleteUser(externalAdminUser.getUserId());
        hardDeleteUser(externalUser.getUserId());
    }

    @Test
    @Order(402)
    public void groupMembershipTestsByAllAdminMembership() {
        loginWithDefaultUserToken();

        List<SettingParam> settingParams = new ArrayList<>();
        settingParams.add(new SettingParam(SystemSettingTags.ADD_ALL_ADMIN_TO_GROUPS, "true"));
        settingParams.add(new SettingParam(SystemSettingTags.ADD_EXTERNAL_ADMIN_TO_GROUPS, "false"));

        ResponseEntity<Void> call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        AssertionsForClassTypes.assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        String tenantId = createTenant();
        UserEntity adminUser = createAdminUser(tenantId);
        UserEntity user = createUser(tenantId);
        UserEntity externalAdminUser = createExternalAdminUser(tenantId);
        UserEntity externalUser = createExternalUser(tenantId);

        GroupsEntityWrapper params = new GroupsEntityWrapper();
        params.setGroupType("public");
        params.setGroupName("test group");
        params.setDescription("test description");
        params.setGroupCategory("normal");
        ResponseEntity<String> createCall = call(url + "create", HttpMethod.POST, params, String.class);
        assertThat(createCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        String groupId = createCall.getBody();

        ResponseEntity<GroupUserGetParams[]> callGroupUsers = call("/api/v1/group/user/members/" + groupId, HttpMethod.GET, GroupUserGetParams[].class);
        AssertionsForClassTypes.assertThat(callGroupUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        AssertionsForClassTypes.assertThat(callGroupUsers.getBody()).isNotNull();

        List<String> memberList = Arrays.stream(callGroupUsers.getBody()).map(groupUserGetParams -> groupUserGetParams.getUser().getUserId()).collect(Collectors.toList());

        assertThat(memberList).doesNotContain(user.getUserId(), externalUser.getUserId());
        assertThat(memberList).contains(adminUser.getUserId(), externalAdminUser.getUserId());

        deleteGroup(groupId);
        hardDeleteUser(adminUser.getUserId());
        hardDeleteUser(user.getUserId());
        hardDeleteUser(externalAdminUser.getUserId());
        hardDeleteUser(externalUser.getUserId());
    }

    @Test
    @Order(403)
    public void groupMembershipTestsByExternalAdminMembership() {
        loginWithDefaultUserToken();

        List<SettingParam> settingParams = new ArrayList<>();
        settingParams.add(new SettingParam(SystemSettingTags.ADD_ALL_ADMIN_TO_GROUPS, "true"));
        settingParams.add(new SettingParam(SystemSettingTags.ADD_EXTERNAL_ADMIN_TO_GROUPS, "true"));

        ResponseEntity<Void> call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        AssertionsForClassTypes.assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        String tenantId = createTenant();
        UserEntity adminUser = createAdminUser(tenantId);
        UserEntity user = createUser(tenantId);
        UserEntity externalAdminUser = createExternalAdminUser(tenantId);
        UserEntity externalUser = createExternalUser(tenantId);

        GroupsEntityWrapper params = new GroupsEntityWrapper();
        params.setGroupType("public");
        params.setGroupName("test group");
        params.setDescription("test description");
        params.setGroupCategory("normal");
        ResponseEntity<String> createCall = call(url + "create", HttpMethod.POST, params, String.class);
        assertThat(createCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        String groupId = createCall.getBody();

        ResponseEntity<GroupUserGetParams[]> callGroupUsers = call("/api/v1/group/user/members/" + groupId, HttpMethod.GET, GroupUserGetParams[].class);
        AssertionsForClassTypes.assertThat(callGroupUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        AssertionsForClassTypes.assertThat(callGroupUsers.getBody()).isNotNull();

        List<String> memberList = Arrays.stream(callGroupUsers.getBody()).map(groupUserGetParams -> groupUserGetParams.getUser().getUserId()).collect(Collectors.toList());

        assertThat(memberList).doesNotContain(adminUser.getUserId(), user.getUserId(), externalUser.getUserId());
        assertThat(memberList).contains(externalAdminUser.getUserId());

        deleteGroup(groupId);
        hardDeleteUser(adminUser.getUserId());
        hardDeleteUser(user.getUserId());
        hardDeleteUser(externalAdminUser.getUserId());
        hardDeleteUser(externalUser.getUserId());
    }

    @Test
    @Order(404)
    public void groupParentTest() {
        loginWithDefaultUserToken();

        GroupsEntityWrapper params = new GroupsEntityWrapper();
        params.setGroupType("public");
        params.setGroupName("test group");
        params.setDescription("test description");
        params.setGroupCategory("normal");
        ResponseEntity<String> createCall = call(url + "create", HttpMethod.POST, params, String.class);
        assertThat(createCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        String otherGroupId = createCall.getBody();
        assertThat(otherGroupId).isNotNull();

        String parentGroupId = createGroup();
        assertThat(parentGroupId).isNotNull();

        params = new GroupsEntityWrapper();
        params.setGroupType("public");
        params.setGroupName("test group2");
        params.setDescription("test description");
        params.setGroupCategory("normal");
        params.setParent(parentGroupId);
        createCall = call(url + "create", HttpMethod.POST, params, String.class);
        assertThat(createCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        String childGroupId = createCall.getBody();
        assertThat(childGroupId).isNotNull();

        ResponseEntity<GroupsEntityWrapper[]> groupListWithPagination = call(url + "query/all", HttpMethod.POST, new Pagination(0, 10, "create-desc"), GroupsEntityWrapper[].class);
        assertThat(groupListWithPagination.getStatusCode()).isEqualTo(HttpStatus.OK);

        Optional<GroupsEntityWrapper> optionalOtherGroup = Arrays.stream(Objects.requireNonNull(groupListWithPagination.getBody())).filter(groupsEntity -> otherGroupId.equals(groupsEntity.getGroupId())).findFirst();
        Optional<GroupsEntityWrapper> optionalParentGroup = Arrays.stream(Objects.requireNonNull(groupListWithPagination.getBody())).filter(groupsEntity -> parentGroupId.equals(groupsEntity.getGroupId())).findFirst();
        Optional<GroupsEntityWrapper> optionalChildGroup = Arrays.stream(Objects.requireNonNull(groupListWithPagination.getBody())).filter(groupsEntity -> childGroupId.equals(groupsEntity.getGroupId())).findFirst();

        assertThat(optionalOtherGroup).isNotEmpty();
        assertThat(optionalParentGroup).isNotEmpty();
        assertThat(optionalChildGroup).isNotEmpty();

        GroupsEntityWrapper otherGroup = optionalOtherGroup.get();
        GroupsEntityWrapper parentGroup = optionalParentGroup.get();
        GroupsEntityWrapper childGroup = optionalChildGroup.get();

        assertThat(otherGroup.getParent()).isNull();
        assertThat(parentGroup.getParent()).isNull();
        assertThat(childGroup.getParent()).isNotNull();

        deleteGroup(otherGroup.getGroupId());
        deleteGroup(parentGroup.getGroupId());
        deleteGroup(childGroup.getGroupId());
    }

    @Test
    @Order(515)
    public void groupParentTestByUpdate() {
        loginWithDefaultUserToken();
        String parentGroupId = createGroup();
        String tenantId = createTenant();

        GroupsEntityWrapper groupParams = new GroupsEntityWrapper();
        groupParams.setGroupType("public");
        groupParams.setGroupName("test group2");
        groupParams.setDescription("test description");
        groupParams.setGroupCategory("normal");
        ResponseEntity<String> callCreate = call("/api/v1/group/create", HttpMethod.POST, groupParams, String.class);
        AssertionsForClassTypes.assertThat(callCreate.getStatusCode()).isEqualTo(HttpStatus.OK);
        String childGroupId = callCreate.getBody();
        AssertionsForClassTypes.assertThat(childGroupId).isNotNull();

        UserEntity parentUser = createUser(tenantId);
        GroupUserCreateParams params = new GroupUserCreateParams();
        params.setUserlist(Collections.singletonList(parentUser.getUserId()));
        params.setRole(GroupRole.USER.name());
        ResponseEntity<Void> callVoid = call("/api/v1/group/user/" + parentGroupId, HttpMethod.POST, params, Void.class);
        AssertionsForClassTypes.assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);


        Optional<GroupsEntity> childGroup = groupOps.byId(childGroupId);
        groupOps.update(childGroup.get(), parentGroupId);
        assertThat(childGroup.get().getParent()).isNotNull();

        ResponseEntity<GroupUserGetParams[]> callGroupUsers = call("/api/v1/group/user/members/" + childGroupId, HttpMethod.GET, GroupUserGetParams[].class);
        AssertionsForClassTypes.assertThat(callGroupUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        AssertionsForClassTypes.assertThat(callGroupUsers.getBody()).isNotNull();
        AssertionsForClassTypes.assertThat(Arrays.stream(callGroupUsers.getBody()).anyMatch(groupUserGetParams -> groupUserGetParams.getUser().getUserId().equals(parentUser.getUserId()))).isTrue();

        childGroup = groupOps.byId(childGroupId);
        groupOps.update(childGroup.get(), null);
        childGroup = groupOps.byId(childGroupId);
        assertThat(childGroup.get().getParent()).isNull();

        callGroupUsers = call("/api/v1/group/user/members/" + childGroupId, HttpMethod.GET, GroupUserGetParams[].class);
        AssertionsForClassTypes.assertThat(callGroupUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        AssertionsForClassTypes.assertThat(callGroupUsers.getBody()).isNotNull();
        AssertionsForClassTypes.assertThat(Arrays.stream(callGroupUsers.getBody()).anyMatch(groupUserGetParams -> groupUserGetParams.getUser().getUserId().equals(parentUser.getUserId()))).isTrue();

        deleteTenant(tenantId);
        deleteGroup(childGroupId);
        deleteGroup(parentGroupId);
    }


}
