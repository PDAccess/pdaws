package com.h2h.test.it;

import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.pojo.AlarmWrapper;
import com.h2h.pda.pojo.service.ServiceOs;
import com.h2h.pda.pojo.service.ServiceType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AlarmControllerTests extends BaseIntegrationTests {

    @Test
    @Order(475)
    @Disabled
    public void serviceAlarmCreateTest() {
        loginWithDefaultUserToken();

        String tenantId = createTenant();

        String groupId = createGroup("serviceAlarmCreateTest");
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);
        AlarmWrapper alarmWrapper = new AlarmWrapper();
        alarmWrapper.setName("service alarm test");
        alarmWrapper.setDescription("service alarm test description");
        alarmWrapper.setMessage("service alarm create message test");
        List<String> regexs = new ArrayList<>();
        regexs.add("test1");
        regexs.add("test2");
        regexs.add("test3");
        alarmWrapper.setRegex(regexs);
        List<String> userList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            userList.add(createUser(tenantId).getUserId());
        }
        alarmWrapper.setUsers(userList);

        ResponseEntity<Void> call = call("/api/v1/alarm/groups/" + groupId, HttpMethod.POST, alarmWrapper, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<AlarmWrapper[]> call2 = call("/api/v1/alarm/services/" + serviceId, HttpMethod.GET, AlarmWrapper[].class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(call2.getBody()).isNotNull();

        AlarmWrapper alarm = call2.getBody()[0];

        ResponseEntity<AlarmWrapper> call4 = call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.GET, AlarmWrapper.class);
        assertThat(call4.getStatusCode()).isEqualTo(HttpStatus.OK);

        AlarmWrapper alarmWrapper1 = call4.getBody();
        assertThat(alarmWrapper1.getDescription()).isEqualTo(alarmWrapper.getDescription());
        assertThat(alarmWrapper1.getName()).isEqualTo(alarmWrapper.getName());
        assertThat(alarmWrapper1.getDescription()).isEqualTo(alarmWrapper.getDescription());
        assertThat(alarmWrapper1.getMessage()).isEqualTo(alarmWrapper.getMessage());

        for (String regex : alarmWrapper1.getRegex()) {
            assertThat(regexs.contains(regex));
        }

        for (String user : alarmWrapper1.getUsers()) {
            assertThat(userList.contains(user));
        }

        alarmWrapper.setUsers(null);
        try {
            call("/api/v1/alarm/services/" + serviceId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setUsers(new ArrayList<>());
        try {
            call("/api/v1/alarm/services/" + serviceId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setUsers(userList);
        alarmWrapper.setRegex(null);
        try {
            call("/api/v1/alarm/services/" + serviceId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setRegex(new ArrayList<>());
        try {
            call("/api/v1/alarm/services/" + serviceId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setRegex(regexs);
        alarmWrapper.setName(null);
        try {
            call("/api/v1/alarm/services/" + serviceId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setName("");
        try {
            call("/api/v1/alarm/services/" + serviceId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setName("new service name");
        alarmWrapper.setMessage(null);
        try {
            call("/api/v1/alarm/services/" + serviceId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setMessage("");
        try {
            call("/api/v1/alarm/services/" + serviceId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setMessage("aaaaa");
        try {
            call("/api/v1/alarm/services/" + serviceId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setMessage("service alarm create test");
        try {
            call("/api/v1/alarm/services/test", HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        call = call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.DELETE, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        call = call("/api/v1/service/" + serviceId, HttpMethod.DELETE, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        for (String userId : userList) {
            deleteUser(userId);
        }

        deleteTenant(tenantId);
        deleteGroup(groupId);
    }

    @Test
    @Order(476)
    public void groupAlarmCreateTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup("groupAlarmCreateTest");
        String tenantId = createTenant();

        AlarmWrapper alarmWrapper = new AlarmWrapper();
        alarmWrapper.setName("service alarm test");
        alarmWrapper.setDescription("service alarm test description");
        alarmWrapper.setMessage("service alarm create message test");
        List<String> regexs = new ArrayList<>();
        regexs.add("test1");
        regexs.add("test2");
        regexs.add("test3");
        alarmWrapper.setRegex(regexs);
        List<String> userList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            userList.add(createUser(tenantId).getUserId());
        }
        alarmWrapper.setUsers(userList);

        ResponseEntity<Void> call = call("/api/v1/alarm/groups/" + groupId, HttpMethod.POST, alarmWrapper, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<AlarmWrapper[]> call2 = call("/api/v1/alarm/groups/" + groupId, HttpMethod.GET, AlarmWrapper[].class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(call2.getBody()).isNotNull();

        AlarmWrapper alarm = call2.getBody()[0];

        ResponseEntity<AlarmWrapper> call4 = call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.GET, AlarmWrapper.class);
        assertThat(call4.getStatusCode()).isEqualTo(HttpStatus.OK);

        AlarmWrapper alarmWrapper1 = call4.getBody();
        assertThat(alarmWrapper1.getDescription()).isEqualTo(alarmWrapper.getDescription());
        assertThat(alarmWrapper1.getName()).isEqualTo(alarmWrapper.getName());
        assertThat(alarmWrapper1.getDescription()).isEqualTo(alarmWrapper.getDescription());
        assertThat(alarmWrapper1.getMessage()).isEqualTo(alarmWrapper.getMessage());

        for (String regex : alarmWrapper1.getRegex()) {
            assertThat(regexs.contains(regex));
        }

        for (String user : alarmWrapper1.getUsers()) {
            assertThat(userList.contains(user));
        }

        alarmWrapper.setUsers(null);
        try {
            call("/api/v1/alarm/groups/" + groupId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setUsers(new ArrayList<>());
        try {
            call("/api/v1/alarm/groups/" + groupId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setUsers(userList);
        alarmWrapper.setRegex(null);
        try {
            call("/api/v1/alarm/groups/" + groupId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setRegex(new ArrayList<>());
        try {
            call("/api/v1/alarm/groups/" + groupId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setRegex(regexs);
        alarmWrapper.setName(null);
        try {
            call("/api/v1/alarm/groups/" + groupId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setName("");
        try {
            call("/api/v1/alarm/groups/" + groupId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setName("new service name");
        alarmWrapper.setMessage(null);
        try {
            call("/api/v1/alarm/groups/" + groupId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setMessage("");
        try {
            call("/api/v1/alarm/groups/" + groupId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setMessage("aaaaa");
        try {
            call("/api/v1/alarm/groups/" + groupId, HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setMessage("service alarm create test");
        try {
            call("/api/v1/alarm/groups/test", HttpMethod.POST, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        call = call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.DELETE, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        for (String userId : userList) {
            deleteUser(userId);
        }

        deleteGroup(groupId);
    }

    @Test
    @Order(477)
    public void updateAlarmTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup();
        String tenantId = createTenant();

        AlarmWrapper alarmWrapper = new AlarmWrapper();
        alarmWrapper.setName("service alarm test");
        alarmWrapper.setDescription("service alarm test description");
        alarmWrapper.setMessage("service alarm create message test");
        List<String> regexs = new ArrayList<>();
        regexs.add("test1");
        regexs.add("test2");
        regexs.add("test3");
        alarmWrapper.setRegex(regexs);
        List<String> userList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            userList.add(createUser(tenantId).getUserId());
        }
        alarmWrapper.setUsers(userList);

        ResponseEntity<Void> call = call("/api/v1/alarm/groups/" + groupId, HttpMethod.POST, alarmWrapper, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<AlarmWrapper[]> call2 = call("/api/v1/alarm/groups/" + groupId, HttpMethod.GET, AlarmWrapper[].class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(call2.getBody()).isNotNull();

        AlarmWrapper alarm = call2.getBody()[0];
        regexs.add("test4");
        String deletedUser = userList.get(4);
        userList.remove(4);
        alarmWrapper.setRegex(regexs);
        alarmWrapper.setUsers(userList);

        call = call("/api/v1/alarm/groups/" + groupId, HttpMethod.POST, alarmWrapper, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<AlarmWrapper> call5 = call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.GET, AlarmWrapper.class);

        call5.getBody().getRegex().contains("test4");

        for (String user : userList) {
            if (!call5.getBody().getUsers().contains(user)) assertThat(user.equals(deletedUser)).isTrue();
        }

        alarmWrapper.setUsers(null);
        try {
            call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.PUT, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setUsers(new ArrayList<>());
        try {
            call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.PUT, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setUsers(userList);
        alarmWrapper.setRegex(null);
        try {
            call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.PUT, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setRegex(new ArrayList<>());
        try {
            call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.PUT, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setRegex(regexs);
        alarmWrapper.setName(null);
        try {
            call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.PUT, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setName("");
        try {
            call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.PUT, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setName("new service name");
        alarmWrapper.setMessage(null);
        try {
            call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.PUT, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setMessage("");
        try {
            call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.PUT, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setMessage("aaaaa");
        try {
            call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.PUT, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        alarmWrapper.setMessage("service alarm create test");
        try {
            call("/api/v1/alarm/999", HttpMethod.PUT, alarmWrapper, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }


        call = call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.DELETE, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        for (String userId : userList) {
            deleteUser(userId);
        }

        deleteTenant(tenantId);
        deleteGroup(groupId);
    }

    @Test
    @Order(478)
    public void alarmActiveDeactivateTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup();
        String tenantId = createTenant();

        AlarmWrapper alarmWrapper = new AlarmWrapper();
        alarmWrapper.setName("service alarm test");
        alarmWrapper.setDescription("service alarm test description");
        alarmWrapper.setMessage("service alarm create message test");
        List<String> regexs = new ArrayList<>();
        regexs.add("test1");
        regexs.add("test2");
        regexs.add("test3");
        alarmWrapper.setRegex(regexs);
        List<String> userList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            userList.add(createUser(tenantId).getUserId());
        }
        alarmWrapper.setUsers(userList);

        ResponseEntity<Void> call = call("/api/v1/alarm/groups/" + groupId, HttpMethod.POST, alarmWrapper, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<AlarmWrapper[]> call2 = call("/api/v1/alarm/groups/" + groupId, HttpMethod.GET, AlarmWrapper[].class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(call2.getBody()).isNotNull();

        AlarmWrapper alarm = call2.getBody()[0];
        call = call("/api/v1/alarm/activate/" + alarm.getAlarmId(), HttpMethod.POST, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        call2 = call("/api/v1/alarm/groups/" + groupId, HttpMethod.GET, AlarmWrapper[].class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(call2.getBody()).isNotNull();

        alarm = call2.getBody()[0];

        assertThat(alarm.isActive()).isTrue();

        call = call("/api/v1/alarm/deactivate/" + alarm.getAlarmId(), HttpMethod.POST, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        call2 = call("/api/v1/alarm/groups/" + groupId, HttpMethod.GET, AlarmWrapper[].class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(call2.getBody()).isNotEmpty();

        alarm = call2.getBody()[0];

        assertThat(alarm.isActive()).isFalse();


        call = call("/api/v1/alarm/" + alarm.getAlarmId(), HttpMethod.DELETE, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        for (String userId : userList) {
            deleteUser(userId);
        }

        deleteTenant(tenantId);
        deleteGroup(groupId);
    }
}
