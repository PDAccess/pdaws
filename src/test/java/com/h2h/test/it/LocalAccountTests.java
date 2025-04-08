package com.h2h.test.it;

import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.AssignmentUserParams;
import com.h2h.pda.pojo.BreakData;
import com.h2h.pda.pojo.ConnectionUserWrapper;
import com.h2h.pda.pojo.Credential;
import com.h2h.pda.pojo.group.GroupsEntityWrapper;
import com.h2h.pda.pojo.service.ServiceCreateParams;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;
import com.h2h.pda.pojo.service.ServiceOs;
import com.h2h.pda.pojo.service.ServiceType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

@Disabled
public class LocalAccountTests extends BaseIntegrationTests {

    @Value("${opensshServer.hostname}")
    private String hostname;

    @Value("${opensshServer.port}")
    private Integer port;

    @Value("${opensshServer.username}")
    private String username;

    @Value("${opensshServer.password}")
    private String password;

    @Test
    @Order(600)
    public void getLocalAccounts() {

        loginWithDefaultUserToken();

        GroupsEntityWrapper groupParams = new GroupsEntityWrapper();
        groupParams.setGroupType("public");
        groupParams.setGroupName("test group");
        groupParams.setDescription("test description");
        ResponseEntity<String> createCall = call("/api/v1/group/create", HttpMethod.POST, groupParams, String.class);

        String groupId = createCall.getBody();

        Credential inventory = new Credential();
        inventory.setUsername(username);
        inventory.setPassword(password);

        ServiceEntity entity = new ServiceEntity();
        entity.setName(hostname);
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);

        ServiceCreateParams params = new ServiceCreateParams();
        params.setIpaddress(hostname);
        params.setPort(port);
        params.setGroupid(groupId);
        params.setServiceEntity(new ServiceEntityWrapper(entity));
        params.setAdmin(inventory);
        params.setVaults(new ArrayList<>());

        ResponseEntity<String> responseService = call("/api/v1/service", HttpMethod.POST, params, String.class);

        ResponseEntity<ConnectionUserWrapper[]> responseEntity = call("/api/v1/service/connection-users/" + responseService.getBody(), HttpMethod.GET, null, ConnectionUserWrapper[].class);

        assert responseEntity.getStatusCode().equals(HttpStatus.OK);
    }

    @Test
    @Order(601)
    public void addLocalAccount() {

        loginWithDefaultUserToken();

        GroupsEntityWrapper groupParams = new GroupsEntityWrapper();
        groupParams.setGroupType("public");
        groupParams.setGroupName("test group");
        groupParams.setDescription("test description");
        ResponseEntity<String> createCall = call("/api/v1/group/create", HttpMethod.POST, groupParams, String.class);
        Assertions.assertThat(createCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        String groupId = createCall.getBody();

        Credential inventory = new Credential();
        inventory.setUsername(username);
        inventory.setPassword(password);

        ServiceEntity entity = new ServiceEntity();
        entity.setName(hostname);
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);

        ServiceCreateParams params = new ServiceCreateParams();
        params.setIpaddress(hostname);
        params.setPort(port);
        params.setGroupid(groupId);
        params.setVaults(Collections.singletonList(inventory));
        params.setServiceEntity(new ServiceEntityWrapper(entity));
        params.setAdmin(inventory);

        ResponseEntity<String> responseService = call("/api/v1/service", HttpMethod.POST, params, String.class);

        String serviceId = responseService.getBody();


        ResponseEntity<ConnectionUserWrapper[]> responseEntity = call("/api/v1/service/connection-users/" + serviceId, HttpMethod.GET, null, ConnectionUserWrapper[].class);

        int userCount = Objects.requireNonNull(responseEntity.getBody()).length;

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("username", "ubuntu");

        callWithForm("/api/v1/service/connection-users/" + serviceId, HttpMethod.POST, map, String.class);

        responseEntity = call("/api/v1/service/connection-users/" + serviceId, HttpMethod.GET, null, ConnectionUserWrapper[].class);

        assert Objects.requireNonNull(responseEntity.getBody()).length == userCount + 1;
    }

    @Test
    @Order(602)
    public void removeLocalAccount() {
        loginWithDefaultUserToken();

        GroupsEntityWrapper groupParams = new GroupsEntityWrapper();
        groupParams.setGroupType("public");
        groupParams.setGroupName("test group");
        groupParams.setDescription("test description");
        ResponseEntity<String> createCall = call("/api/v1/group/create", HttpMethod.POST, groupParams, String.class);
        Assertions.assertThat(createCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        String groupId = createCall.getBody();

        Credential inventory = new Credential();
        inventory.setUsername(username);
        inventory.setPassword(password);

        ServiceEntity entity = new ServiceEntity();
        entity.setName(hostname);
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);

        ServiceCreateParams params = new ServiceCreateParams();
        params.setIpaddress(hostname);
        params.setPort(port);
        params.setGroupid(groupId);
        params.setVaults(Collections.singletonList(inventory));
        params.setServiceEntity(new ServiceEntityWrapper(entity));
        params.setAdmin(inventory);

        ResponseEntity<String> responseService = call("/api/v1/service", HttpMethod.POST, params, String.class);

        String serviceId = responseService.getBody();

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("username", "ubuntu");

        callWithForm("/api/v1/service/connection-users/" + serviceId, HttpMethod.POST, map, String.class);

        ResponseEntity<ConnectionUserWrapper[]> responseEntity = call("/api/v1/service/connection-users/" + serviceId, HttpMethod.GET, null, ConnectionUserWrapper[].class);

        int userCount = Objects.requireNonNull(responseEntity.getBody()).length;

        callWithForm("/api/v1/service/connection-users/" + responseEntity.getBody()[responseEntity.getBody().length - 1].getId(), HttpMethod.DELETE, null, String.class);

        responseEntity = call("/api/v1/service/connection-users/" + serviceId, HttpMethod.GET, null, ConnectionUserWrapper[].class);

        assert Objects.requireNonNull(responseEntity.getBody()).length == userCount - 1;
    }

    @Test
    @Order(603)
    public void breakLocalAccount() {
        loginWithDefaultUserToken();

        GroupsEntityWrapper groupParams = new GroupsEntityWrapper();
        groupParams.setGroupType("public");
        groupParams.setGroupName("test group");
        groupParams.setDescription("test description");
        ResponseEntity<String> createCall = call("/api/v1/group/create", HttpMethod.POST, groupParams, String.class);
        Assertions.assertThat(createCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        String groupId = createCall.getBody();

        Credential inventory = new Credential();
        inventory.setUsername(username);
        inventory.setPassword(password);

        ServiceEntity entity = new ServiceEntity();
        entity.setName(hostname);
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);

        ServiceCreateParams params = new ServiceCreateParams();
        params.setIpaddress(hostname);
        params.setPort(port);
        params.setGroupid(groupId);
        params.setVaults(Collections.singletonList(inventory));
        params.setServiceEntity(new ServiceEntityWrapper(entity));
        params.setAdmin(inventory);

        ResponseEntity<String> responseService = call("/api/v1/service", HttpMethod.POST, params, String.class);

        String serviceId = responseService.getBody();

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("username", "ubuntu");

        callWithForm("/api/v1/service/connection-users/" + serviceId, HttpMethod.POST, map, String.class);

        ResponseEntity<ConnectionUserWrapper[]> responseEntity = call("/api/v1/service/connection-users/" + serviceId, HttpMethod.GET, null, ConnectionUserWrapper[].class);

        ConnectionUserWrapper user = Objects.requireNonNull(responseEntity.getBody())[0];

        MultiValueMap<String, Integer> userMap= new LinkedMultiValueMap<String, Integer>();
        userMap.add("account_id", user.getId());

        ResponseEntity<BreakData> responseBreak = callWithForm("/api/v1/service/connection-users/break", HttpMethod.POST, userMap, BreakData.class);

        assert Objects.requireNonNull(responseBreak.getBody()).getUsername().equals(user.getUsername());

    }

    @Test
    @Order(604)
    public void addLocalAccountMember() {

        loginWithDefaultUserToken();

        GroupsEntityWrapper groupParams = new GroupsEntityWrapper();
        groupParams.setGroupType("public");
        groupParams.setGroupName("test group");
        groupParams.setDescription("test description");
        ResponseEntity<String> createCall = call("/api/v1/group/create", HttpMethod.POST, groupParams, String.class);
        Assertions.assertThat(createCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        String groupId = createCall.getBody();

        Credential inventory = new Credential();
        inventory.setUsername(username);
        inventory.setPassword(password);

        ServiceEntity entity = new ServiceEntity();
        entity.setName(hostname);
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);

        ServiceCreateParams params = new ServiceCreateParams();
        params.setIpaddress(hostname);
        params.setPort(port);
        params.setGroupid(groupId);
        params.setVaults(Collections.singletonList(inventory));
        params.setServiceEntity(new ServiceEntityWrapper(entity));
        params.setAdmin(inventory);

        ResponseEntity<String> responseService = call("/api/v1/service", HttpMethod.POST, params, String.class);

        String serviceId = responseService.getBody();

        ResponseEntity<ConnectionUserWrapper[]> responseEntity = call("/api/v1/service/connection-users/" + serviceId, HttpMethod.GET, null, ConnectionUserWrapper[].class);
        ConnectionUserWrapper user = Objects.requireNonNull(responseEntity.getBody())[0];

        ResponseEntity<UserEntity> responseWho = call("/api/v1/user/who", HttpMethod.GET, null, UserEntity.class);
        UserEntity userWho = Objects.requireNonNull(responseWho.getBody());

        AssignmentUserParams assignmentUserParams = new AssignmentUserParams();
        assignmentUserParams.setUserEntities(Collections.singletonList(userWho));

        ResponseEntity<String> response = call("/api/v1/service/account/assign/" + user.getId(), HttpMethod.POST, assignmentUserParams, String.class);

        assert response.getStatusCode().equals(HttpStatus.OK);

        ResponseEntity<UserEntity[]> responseAssign = call("/api/v1/service/account/assign/" + user.getId(), HttpMethod.GET, null, UserEntity[].class);

        assert Objects.requireNonNull(responseAssign.getBody())[0].getUserId().equals(userWho.getUserId());

    }

    @Test
    @Order(605)
    public void removeLocalAccountMember() {

        loginWithDefaultUserToken();

        GroupsEntityWrapper groupParams = new GroupsEntityWrapper();
        groupParams.setGroupType("public");
        groupParams.setGroupName("test group");
        groupParams.setDescription("test description");
        ResponseEntity<String> createCall = call("/api/v1/group/create", HttpMethod.POST, groupParams, String.class);
        Assertions.assertThat(createCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        String groupId = createCall.getBody();

        Credential inventory = new Credential();
        inventory.setUsername(username);
        inventory.setPassword(password);

        ServiceEntity entity = new ServiceEntity();
        entity.setName(hostname);
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);

        ServiceCreateParams params = new ServiceCreateParams();
        params.setIpaddress(hostname);
        params.setPort(port);
        params.setGroupid(groupId);
        params.setVaults(Collections.singletonList(inventory));
        params.setServiceEntity(new ServiceEntityWrapper(entity));
        params.setAdmin(inventory);

        ResponseEntity<String> responseService = call("/api/v1/service", HttpMethod.POST, params, String.class);

        String serviceId = responseService.getBody();

        ResponseEntity<ConnectionUserWrapper[]> responseEntity = call("/api/v1/service/connection-users/" + serviceId, HttpMethod.GET, null, ConnectionUserWrapper[].class);
        ConnectionUserWrapper user = Objects.requireNonNull(responseEntity.getBody())[0];

        ResponseEntity<UserEntity> responseWho = call("/api/v1/user/who", HttpMethod.GET, null, UserEntity.class);
        UserEntity userWho = Objects.requireNonNull(responseWho.getBody());

        AssignmentUserParams assignmentUserParams = new AssignmentUserParams();
        assignmentUserParams.setUserEntities(Collections.singletonList(userWho));

        call("/api/v1/service/account/assign/" + user.getId(), HttpMethod.POST, assignmentUserParams, String.class);

        ResponseEntity<UserEntity[]> responseAssign = call("/api/v1/service/account/assign/" + user.getId(), HttpMethod.GET, null, UserEntity[].class);

        int userCount = Objects.requireNonNull(responseAssign.getBody()).length;

        assignmentUserParams.setUserEntities(new ArrayList<>());

        ResponseEntity<String> response = call("/api/v1/service/account/assign/" + user.getId(), HttpMethod.POST, assignmentUserParams, String.class);

        assert response.getStatusCode().equals(HttpStatus.OK);

        responseAssign = call("/api/v1/service/account/assign/" + user.getId(), HttpMethod.GET, null, UserEntity[].class);

        assert Objects.requireNonNull(responseAssign.getBody()).length == userCount - 1;

    }

}
