package com.h2h.test.it;

import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.Credential;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.pojo.UserServiceCreateParams;
import com.h2h.pda.pojo.group.GroupCategory;
import com.h2h.pda.pojo.group.GroupUserWrapper;
import com.h2h.pda.pojo.group.GroupsEntityWrapper;
import com.h2h.pda.pojo.policy.PolicyCreateParam;
import com.h2h.pda.pojo.policy.PolicyEntityWrapper;
import com.h2h.pda.pojo.service.ServiceCreateParams;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;
import com.h2h.pda.pojo.service.ServiceOs;
import com.h2h.pda.pojo.service.ServiceType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ServiceControllerTests extends BaseIntegrationTests {

    @Test
    @Order(2)
    @Disabled
    public void createService() {
        loginWithDefaultUserToken();

        String tenantId = createTenant();
        UserEntity user = createUser(tenantId);
        String userId = user.getUserId();

        // for correct group
        GroupsEntityWrapper groupParams = new GroupsEntityWrapper();
        groupParams.setGroupType("public");
        groupParams.setGroupName("test group");
        groupParams.setDescription("test description");
        groupParams.setGroupCategory(GroupCategory.NORMAL.name());
        ResponseEntity<String> createCall = call("/api/v1/group/create", HttpMethod.POST, groupParams, String.class);
        Assertions.assertThat(createCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        String groupId = createCall.getBody();


        ServiceCreateParams params = new ServiceCreateParams();
        params.setIpaddress("1.1.1.1");
        params.setPort(22);
        Credential inventory = new Credential();
        inventory.setUsername("admin");

        params.setGroupid(groupId);

        params.setVaults(Collections.singletonList(inventory));
        ServiceEntity entity = new ServiceEntity();
        entity.setName("test-service");
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.WEBAPP);
        params.setServiceEntity(new ServiceEntityWrapper(entity));
        params.setAdmin(inventory);

        // success service added
        ResponseEntity<String> call = call("/api/v1/service/service", HttpMethod.POST, params, String.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        String id = call.getBody();

        ResponseEntity<ServiceEntityWrapper> call2 = call("/api/v1/service/id/" + id, HttpMethod.GET, params, ServiceEntityWrapper.class);

        assertThat(call2.getBody().getInventoryId()).isNotNull();

        ResponseEntity<Void> call3 = call("/api/v1/service/" + id, HttpMethod.DELETE, Void.class);
        assertThat(call3.getStatusCode()).isEqualTo(HttpStatus.OK);


        // normal user test
        loginWithUserToken(user.getUsername(), "123123123");
        try {
            call("/api/v1/service/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN).describedAs(ex.getResponseBodyAsString());
        }


        loginWithUserToken("admin", "H2HSecure123");

        // wrong group tests
        params.setGroupid(null);
        try {
            call = call("/api/v1/service/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }

        params.setGroupid("");
        try {
            call = call("/api/v1/service/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }

        params.setGroupid(groupId);
        // wrong service name tests
        params.getServiceEntity().setName(null);
        try {
            call = call("/api/v1/service/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }

        params.getServiceEntity().setName("");
        try {
            call = call("/api/v1/service/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }


        params.getServiceEntity().setName("test");
        // wrong service type testing
        params.getServiceEntity().setServiceTypeId(null);
        try {
            call = call("/api/v1/service/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }

        params.getServiceEntity().setServiceTypeId(ServiceType.UNKNOWN);
        try {
            call = call("/api/v1/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }
        params.getServiceEntity().setServiceTypeId(ServiceType.LDAP);
        try {
            call = call("/api/v1/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }

        params.getServiceEntity().setServiceTypeId(ServiceType.MYSQL);
        // wrong dbname tests
        params.setDbname(null);
        try {
            call = call("/api/v1/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }

        params.setDbname("");
        try {
            call = call("/api/v1/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }

        // if service is not db service pass dbname control test
        params.getServiceEntity().setServiceTypeId(ServiceType.SSH);
        params.setDbname(null);
        call = call("/api/v1/service", HttpMethod.POST, params, String.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        id = call.getBody();

        call2 = call("/api/v1/service/id/" + id, HttpMethod.GET, params, ServiceEntityWrapper.class);

        assertThat(call2.getBody().getInventoryId()).isNotNull();

        call3 = call("/api/v1/service/" + id, HttpMethod.DELETE, Void.class);
        assertThat(call3.getStatusCode()).isEqualTo(HttpStatus.OK);


        // if service is not db service pass dbname control test
        params.setDbname("");
        call = call("/api/v1/service", HttpMethod.POST, params, String.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        id = call.getBody();

        call2 = call("/api/v1/service/id/" + id, HttpMethod.GET, params, ServiceEntityWrapper.class);

        assertThat(call2.getBody().getInventoryId()).isNotNull();

        call3 = call("/api/v1/service/" + id, HttpMethod.DELETE, Void.class);
        assertThat(call3.getStatusCode()).isEqualTo(HttpStatus.OK);


        params.setDbname("deneme");
        // wrong os tests
        params.getServiceEntity().setOperatingSystemId(null);
        try {
            call = call("/api/v1/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }

        params.getServiceEntity().setOperatingSystemId(ServiceOs.UNKNOWN_SERVICE);
        try {
            call = call("/api/v1/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }

        params.getServiceEntity().setOperatingSystemId(ServiceOs.UNKNOWN_SERVICE);
        try {
            call = call("/api/v1/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }

        params.getServiceEntity().setOperatingSystemId(ServiceOs.UBUNTU);
        // wrong ip address tests
        params.setIpaddress(null);
        try {
            call = call("/api/v1/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }

        params.setIpaddress("");
        try {
            call = call("/api/v1/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }

        /*
        params.setIpaddress("192.168.1.1.");
        try {
            call = call("/api/service", HttpMethod.PUT, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }
         */


        // ip address localhost test
        params.setIpaddress("localhost");
        call = call("/api/v1/service", HttpMethod.POST, params, String.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        id = call.getBody();

        call2 = call("/api/v1/service/id/" + id, HttpMethod.GET, params, ServiceEntityWrapper.class);

        assertThat(call2.getBody().getInventoryId()).isNotNull();

        call3 = call("/api/v1/service/" + id, HttpMethod.DELETE, Void.class);
        assertThat(call3.getStatusCode()).isEqualTo(HttpStatus.OK);


        params.setIpaddress("192.168.1.1");
        // wrong port tests
        params.setPort(-1);
        try {
            call = call("/api/v1/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }

        params.setPort(65536);
        try {
            call = call("/api/v1/service", HttpMethod.POST, params, String.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(call.getBody());
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST).describedAs(ex.getResponseBodyAsString());
        }

        deleteUser(user.getUserId());
        deleteTenant(tenantId);
    }

    @Test
    @Order(300)
    @Disabled
    public void serviceUserAdd() {
        loginWithDefaultUserToken();
        List<UserEntity> userList = new ArrayList<>();
        List<String> userListId = new ArrayList<>();

        String tenantId = createTenant();

        userList.add(createUser(tenantId));
        userList.add(createUser(tenantId));

        userListId.add(userList.get(0).getUserId());
        userListId.add(userList.get(1).getUserId());

        // for correct group
        String groupId = createGroup();


        ServiceCreateParams params = new ServiceCreateParams();
        params.setIpaddress("1.1.1.1");
        params.setPort(22);
        Credential inventory = new Credential();
        inventory.setUsername("admin");

        params.setGroupid(groupId);

        params.setVaults(Collections.singletonList(inventory));
        ServiceEntity entity = new ServiceEntity();
        entity.setName("test-service");
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);
        params.setServiceEntity(new ServiceEntityWrapper(entity));
        params.setAdmin(inventory);

        // success service added
        ResponseEntity<String> call = call("/api/v1/service", HttpMethod.POST, params, String.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        String serviceId = call.getBody();


        PolicyCreateParam policyCreateParam = new PolicyCreateParam();
        PolicyEntityWrapper policyEntity = new PolicyEntityWrapper();
        policyEntity.setId(serviceId);
//        policyEntity.setBehavior("W");
//        policyEntity.setServicetype("1");
//        policyEntity.setOperatingsystem("1");
//        policyEntity.setServicemeta("T");
        policyEntity.setName("Test");
        List<String> regexList = new ArrayList<>();
        regexList.add("asdasda");
        regexList.add("sdfs");
        policyCreateParam.setRegexList(regexList);
        policyCreateParam.setUserList(userListId);
        policyCreateParam.setPolicyEntity(policyEntity);

        call = call("/api/v1/policy", HttpMethod.PUT, policyCreateParam, String.class);

        String policyId = call.getBody();

        UserServiceCreateParams userServiceCreateParams = new UserServiceCreateParams();
        userServiceCreateParams.setUsers(userListId);
        LocalDateTime start = LocalDateTime.now();
        Calendar cal = Calendar.getInstance();
        cal.setTime(Timestamp.valueOf(start));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        start = LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());
        userServiceCreateParams.setExpiredate(Timestamp.valueOf(start));
        userServiceCreateParams.setPolicyid(call.getBody());
        ResponseEntity<Void> call2 = call("/api/userservice/service/" + serviceId, HttpMethod.POST, userServiceCreateParams, Void.class);

        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);

        loginWithUserToken(userList.get(0).getUsername(), "123123123");

        try {
            call2 = call("/api/userservice/service/" + serviceId, HttpMethod.POST, userServiceCreateParams, Void.class);
            assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        loginWithUserToken("admin", "H2HSecure123");
        userServiceCreateParams.setPolicyid(null);
        call2 = call("/api/userservice/service/" + serviceId, HttpMethod.POST, userServiceCreateParams, Void.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);


        userServiceCreateParams.setPolicyid("");
        call2 = call("/api/userservice/service/" + serviceId, HttpMethod.POST, userServiceCreateParams, Void.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);

        userServiceCreateParams.setExpiredate(Timestamp.valueOf(LocalDateTime.of(2021, 4, 27, 0, 0, 0)));
        try {
            call2 = call("/api/userservice/service/" + serviceId, HttpMethod.POST, userServiceCreateParams, Void.class);
            assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        call2 = call("/api/v1/service/" + serviceId, HttpMethod.DELETE, Void.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Integer> callInt = call("/api/v1/policy/delete/" + policyId, HttpMethod.DELETE, Integer.class);
        assertThat(callInt.getStatusCode()).isEqualTo(HttpStatus.OK);

        for (String userId : userListId) {
            deleteUser(userId);
        }

        deleteGroup(groupId);
        deleteTenant(tenantId);
    }

    @Test
    @Order(301)
    @Disabled
    public void serviceWrapperTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup();

        ServiceEntity entity = new ServiceEntity();
        entity.setName("test-service");
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);

        String serviceId = createService(entity, groupId);

        ResponseEntity<Void> call2 = call("/api/v1/service/" + serviceId, HttpMethod.GET, Void.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);

        call2 = call("/api/v1/service/" + serviceId, HttpMethod.DELETE, Void.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);


        try {
            call2 = call("/api/v1/service/" + serviceId, HttpMethod.GET, Void.class);
            assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }


        try {
            call2 = call("/api/servicewrapper/test", HttpMethod.GET, Void.class);
            assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }


        try {
            call2 = call("/api/servicewrapper/", HttpMethod.GET, Void.class);
            assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        deleteGroup(groupId);
    }

    @Test
    @Order(302)
    @Disabled
    public void serviceAllListTest() {
        loginWithDefaultUserToken();
        List<String> serviceList = new ArrayList<>();

        // for correct group
        String groupId = createGroup();

        ServiceEntity entity = new ServiceEntity();
        entity.setName("test-service");
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);

        serviceList.add(createService(entity, groupId));

        entity.setName("test-service2");
        serviceList.add(createService(entity, groupId));

        entity.setName("test-service3");
        serviceList.add(createService(entity, groupId));

        ResponseEntity<Void> call2 = call("/api/v1/service/" + serviceList.get(1), HttpMethod.DELETE, Void.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ServiceEntityWrapper[]> call3 = call("/api/v1/service/all", HttpMethod.GET, ServiceEntityWrapper[].class);

        List<String> serviceList2 = new ArrayList<>();

        for (ServiceEntityWrapper serviceEntityWrapper : Objects.requireNonNull(Objects.requireNonNull(call3.getBody()))) {
            serviceList2.add(serviceEntityWrapper.getInventoryId());
        }

        for (String id : serviceList) {
            if (!serviceList2.contains(id)) {
                assertThat(id).isEqualTo(serviceList.get(1));
            } else {
                assertThat(serviceList2.contains(id)).isEqualTo(true);
            }
        }

        serviceList.add("test");

        for (String id : serviceList) {
            if (!serviceList2.contains(id)) {
                if (id.equals("test")) {
                    assertThat(id).isEqualTo("test");
                } else {
                    assertThat(id).isEqualTo(serviceList.get(1));
                }
            } else {
                assertThat(serviceList2.contains(id)).isEqualTo(true);
            }
        }

        call2 = call("/api/v1/service/" + serviceList.get(0), HttpMethod.DELETE, Void.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);

        call2 = call("/api/v1/service/" + serviceList.get(2), HttpMethod.DELETE, Void.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);

        deleteGroup(groupId);
    }

    @Test
    @Order(303)
    @Disabled
    public void serviceAllListWithSortTest() {
        loginWithDefaultUserToken();
        List<String> serviceList = new ArrayList<>();

        // for correct group
        String groupId = createGroup();

        ServiceEntity entity = new ServiceEntity();

        for (int i = 1; i <= 5; i++) {
            entity.setName("test-service " + i);
            entity.setOperatingSystemId(ServiceOs.of(i));
            entity.setServiceTypeId(ServiceType.of(i));
            serviceList.add(createService(entity, groupId));
        }

        ResponseEntity<Void> call2 = call("/api/v1/service/" + serviceList.get(0), HttpMethod.DELETE, Void.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);


        ResponseEntity<ServiceEntityWrapper[]> call3 = call("/api/v1/service/filter", HttpMethod.GET, ServiceEntityWrapper[].class);

        List<String> serviceList2 = new ArrayList<>();

        List<ServiceEntityWrapper> sews = Arrays.asList(Objects.requireNonNull(Objects.requireNonNull(call3.getBody())));


        for (ServiceEntityWrapper serviceEntityWrapper : sews) {
            serviceList2.add(serviceEntityWrapper.getInventoryId());
        }

        for (String id : serviceList) {
            if (!serviceList2.contains(id)) {
                assertThat(id).isEqualTo(serviceList.get(0));
            } else {
                assertThat(serviceList2.contains(id)).isEqualTo(true);
            }
        }

        for (int i = 0; i < (sews.size() - 1); i++) {
            boolean check = sews.get(i).getCreatedAt().before(sews.get(i + 1).getCreatedAt());
            if (check)
                assertThat(check).isEqualTo(true);
            else
                assertThat(sews.get(i).getCreatedAt().equals(sews.get(i + 1).getCreatedAt())).isEqualTo(true);
        }

        call3 = call("/api/v1/service/create-desc", HttpMethod.GET, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            boolean check = sews.get(i).getCreatedAt().after(sews.get(i + 1).getCreatedAt());
            if (check)
                assertThat(check).isEqualTo(true);
            else
                assertThat(sews.get(i).getCreatedAt().equals(sews.get(i + 1).getCreatedAt())).isEqualTo(true);
        }

        call3 = call("/api/v1/service/os", HttpMethod.GET, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            assertThat(sews.get(i).getOperatingSystemId() != sews.get(i + 1).getOperatingSystemId()).isEqualTo(true);
        }

        call3 = call("/api/v1/service/os-desc", HttpMethod.GET, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            assertThat(sews.get(i).getOperatingSystemId() != sews.get(i + 1).getOperatingSystemId()).isEqualTo(true);
        }

        call3 = call("/api/v1/service/opservice", HttpMethod.GET, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            assertThat(sews.get(i).getServiceTypeId() != sews.get(i + 1).getServiceTypeId()).isEqualTo(true);
        }

        call3 = call("/api/v1/service/opservice-desc", HttpMethod.GET, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            assertThat(sews.get(i).getServiceTypeId() != sews.get(i + 1).getServiceTypeId()).isEqualTo(true);
        }

        call3 = call("/api/v1/service/name", HttpMethod.GET, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            assertThat(sews.get(i).getName().toLowerCase().compareTo(sews.get(i).getName().toLowerCase()) >= 0).isEqualTo(true);
        }

        call3 = call("/api/v1/service/name-desc", HttpMethod.GET, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            assertThat(sews.get(i).getName().toLowerCase().compareTo(sews.get(i).getName().toLowerCase()) <= 0).isEqualTo(true);
        }

        call3 = call("/api/v1/service/test", HttpMethod.GET, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            boolean check = sews.get(i).getCreatedAt().after(sews.get(i + 1).getCreatedAt());
            if (check)
                assertThat(check).isEqualTo(true);
            else
                assertThat(sews.get(i).getCreatedAt().equals(sews.get(i + 1).getCreatedAt())).isEqualTo(true);
        }

        deleteServices(serviceList);
        deleteGroup(groupId);
    }

    @Test
    @Order(304)
    public void getServiceByInventoryidTest() {
        loginWithDefaultUserToken();


        String groupId = createGroup();

        ServiceEntity entity = new ServiceEntity();
        entity.setName("test-service");
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);

        String serviceId = createService(entity, groupId);

        ResponseEntity<ServiceEntityWrapper> call2 = call("/api/v1/service/id/" + serviceId, HttpMethod.GET, ServiceEntityWrapper.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Void> call3 = call("/api/v1/service/" + serviceId, HttpMethod.DELETE, Void.class);
        assertThat(call3.getStatusCode()).isEqualTo(HttpStatus.OK);

        deleteGroup(groupId);
    }

    @Test
    @Order(305)
    @Disabled
    public void enableAndDisableVideoRecordTest() {
        loginWithDefaultUserToken();

        // for correct group
        String groupId = createGroup();


        ServiceEntity entity = new ServiceEntity();
        entity.setName("test-service");
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);


        String serviceId = createService(entity, groupId);

        ResponseEntity<Void> call2 = call("/api/v1/service/record/enable/" + serviceId, HttpMethod.POST, Void.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ServiceEntityWrapper> call3 = call("/api/v1/service/id/" + serviceId, HttpMethod.GET, ServiceEntityWrapper.class);
        assertThat(Objects.requireNonNull(call3.getBody()).getVideoRecord()).isEqualTo(true);

        call2 = call("/api/v1/service/record/disable/" + serviceId, HttpMethod.POST, Void.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);

        call3 = call("/api/v1/service/id/" + serviceId, HttpMethod.GET, ServiceEntityWrapper.class);
        assertThat(Objects.requireNonNull(call3.getBody()).getVideoRecord()).isEqualTo(false);

        call2 = call("/api/v1/service/" + serviceId, HttpMethod.DELETE, Void.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);

        call2 = call("/api/v1/service/record/enable/" + serviceId, HttpMethod.POST, Void.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        call2 = call("/api/v1/service/record/enable/TEST", HttpMethod.POST, Void.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        deleteGroup(groupId);
    }

    @Test
    @Order(306)
    @Disabled
    public void serviceListWithPaginationAndSortTest() {
        loginWithDefaultUserToken();
        List<String> serviceList = new ArrayList<>();

        // for correct group
        String groupId = createGroup();

        for (int i = 0; i < 15; i++) {
            ServiceEntity entity = new ServiceEntity();
            entity.setName("test-service " + i);
            if (i == 0 || i > 9) {
                entity.setOperatingSystemId(ServiceOs.UBUNTU);
                entity.setServiceTypeId(ServiceType.SSH);
            } else {
                entity.setOperatingSystemId(ServiceOs.of(i));
                entity.setServiceTypeId(ServiceType.of(i));
            }

            serviceList.add(createService(entity, groupId));
        }

        ResponseEntity<Void> call2 = call("/api/v1/service/" + serviceList.get(0), HttpMethod.DELETE, Void.class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);


        Pagination pagination = new Pagination();

        pagination.setPerPage(10);
        pagination.setCurrentPage(0);

        ResponseEntity<ServiceEntityWrapper[]> call3 = call("/api/v1/service/filter", HttpMethod.POST, pagination, ServiceEntityWrapper[].class);

        List<ServiceEntityWrapper> sews = Arrays.asList(Objects.requireNonNull(Objects.requireNonNull(call3.getBody())));

        assertThat(sews.size()).isEqualTo(10);

        List<String> serviceList2 = new ArrayList<>();

        for (ServiceEntityWrapper serviceEntityWrapper : sews) {
            serviceList2.add(serviceEntityWrapper.getInventoryId());
        }

        for (int i = 0; i < (sews.size() - 1); i++) {
            boolean check = sews.get(i).getCreatedAt().before(sews.get(i + 1).getCreatedAt());
            if (check)
                assertThat(check).isEqualTo(true);
            else
                assertThat(sews.get(i).getCreatedAt().equals(sews.get(i + 1).getCreatedAt())).isEqualTo(true);
        }

        call3 = call("/api/v1/service/create-desc", HttpMethod.POST, pagination, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            boolean check = sews.get(i).getCreatedAt().after(sews.get(i + 1).getCreatedAt());
            if (check)
                assertThat(check).isEqualTo(true);
            else
                assertThat(sews.get(i).getCreatedAt().equals(sews.get(i + 1).getCreatedAt())).isEqualTo(true);
        }

        call3 = call("/api/v1/service/os", HttpMethod.POST, pagination, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            assertThat(sews.get(i).getOperatingSystemId() != sews.get(i + 1).getOperatingSystemId()).isEqualTo(true);
        }

        call3 = call("/api/v1/service/os-desc", HttpMethod.POST, pagination, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            assertThat(sews.get(i).getOperatingSystemId() != sews.get(i + 1).getOperatingSystemId()).isEqualTo(true);
        }

        call3 = call("/api/v1/service/opservice", HttpMethod.POST, pagination, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            assertThat(sews.get(i).getServiceTypeId() != sews.get(i + 1).getServiceTypeId()).isEqualTo(true);
        }

        call3 = call("/api/v1/service/opservice-desc", HttpMethod.POST, pagination, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            assertThat(sews.get(i).getServiceTypeId() != sews.get(i + 1).getServiceTypeId()).isEqualTo(true);
        }

        call3 = call("/api/v1/service/name", HttpMethod.POST, pagination, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            assertThat(sews.get(i).getName().toLowerCase().compareTo(sews.get(i).getName().toLowerCase()) >= 0).isEqualTo(true);
        }

        call3 = call("/api/v1/service/name-desc", HttpMethod.POST, pagination, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            assertThat(sews.get(i).getName().toLowerCase().compareTo(sews.get(i).getName().toLowerCase()) <= 0).isEqualTo(true);
        }

        call3 = call("/api/v1/service/test", HttpMethod.POST, pagination, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(call3.getBody()));

        for (int i = 0; i < (sews.size() - 1); i++) {
            boolean check = sews.get(i).getCreatedAt().after(sews.get(i + 1).getCreatedAt());
            if (check)
                assertThat(check).isEqualTo(true);
            else
                assertThat(sews.get(i).getCreatedAt().equals(sews.get(i + 1).getCreatedAt())).isEqualTo(true);
        }

        pagination.setCurrentPage(1);
        call3 = call("/api/v1/service/create", HttpMethod.POST, pagination, ServiceEntityWrapper[].class);

        sews = Arrays.asList(Objects.requireNonNull(Objects.requireNonNull(call3.getBody())));

        //assertThat(sews.size()).isEqualTo(4);

        for (ServiceEntityWrapper serviceEntityWrapper : sews) {
            serviceList2.add(serviceEntityWrapper.getInventoryId());
        }

        for (String id : serviceList) {
            if (!serviceList2.contains(id)) {
                assertThat(id).isEqualTo(serviceList.get(0));
            } else {
                assertThat(serviceList2.contains(id)).isEqualTo(true);
            }
        }

        deleteServices(serviceList);
        deleteGroup(groupId);
    }

    @Test
    @Order(307)
    public void getServiceEntityTest(){
        loginWithDefaultUserToken();

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");

        String groupId = createGroup();

        String serviceId = createService(serviceEntity, groupId);


        ResponseEntity<ServiceEntity> callService = call("/api/v1/service/id/"+serviceId, HttpMethod.GET, ServiceEntity.class);
        assertThat(callService.getStatusCode()).isEqualTo(HttpStatus.OK);

        callService = call("/api/v1/service/id/test", HttpMethod.GET, ServiceEntity.class);
        assertThat(callService.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @Order(308)
    public void getServiceGroupsTest(){

        loginWithDefaultUserToken();

        String groupId = createGroup();

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        ResponseEntity<GroupsEntityWrapper[]> serviceGroupsResponse = call("/api/v1/service/groups/"+serviceId, HttpMethod.POST, new Pagination(""), GroupsEntityWrapper[].class);
        assertThat(serviceGroupsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(serviceGroupsResponse.getBody()).length).isNotZero();

        serviceGroupsResponse = call("/api/v1/service/groups/"+serviceId, HttpMethod.POST, new Pagination(null), GroupsEntityWrapper[].class);
        assertThat(serviceGroupsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(serviceGroupsResponse.getBody()).length).isNotZero();

        serviceGroupsResponse = call("/api/v1/service/groups/"+serviceId, HttpMethod.POST, new Pagination(serviceGroupsResponse.getBody()[0].getGroupName()), GroupsEntityWrapper[].class);
        assertThat(serviceGroupsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(serviceGroupsResponse.getBody()).length).isNotZero();

        serviceGroupsResponse = call("/api/v1/service/groups/"+serviceId, HttpMethod.POST, new Pagination(UUID.randomUUID().toString()), GroupsEntityWrapper[].class);
        assertThat(serviceGroupsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(serviceGroupsResponse.getBody()).length).isZero();
    }
}
