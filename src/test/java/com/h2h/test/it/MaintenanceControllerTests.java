package com.h2h.test.it;

import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class MaintenanceControllerTests extends BaseIntegrationTests {

    @Test
    @Order(513)
    public void addMaintenance() {
        loginWithDefaultUserToken();
        String groupId = createGroup("test");
        String tenantId = createTenant();

        MaintenanceParams params = new MaintenanceParams();
        params.setGroupId(groupId);

        List<String> userList = new ArrayList<>();
        List<UserEntity> users = new ArrayList<>();

        users.add(createUser(tenantId));
        users.add(createUser(tenantId));
        userList.add(users.get(0).getUserId());
        userList.add(users.get(1).getUserId());

        params.setUserIds(userList);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        DateRange dateRange = new DateRange();
        Timestamp start = new Timestamp(cal.getTime().getTime());
        dateRange.setStart(start);
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Timestamp end = new Timestamp(cal.getTime().getTime());
        dateRange.setEnd(end);

        params.setDateRange(dateRange);

        ResponseEntity<Void> callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<MaintenanceSingleParams[]> callMain = call("/api/v1/maintenance/" + groupId, HttpMethod.GET, MaintenanceSingleParams[].class);
        assertThat(callMain.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callMain.getBody()).length).isEqualTo(2);

        for (MaintenanceSingleParams maintenance : callMain.getBody()) {
            assertThat(maintenance.getGroupId()).isEqualTo(groupId);
            assertThat(userList.contains(maintenance.getUserId())).isTrue();
            assertThat(maintenance.getStartDate()).isEqualTo(dateRange.getStart());
            assertThat(maintenance.getEndDate()).isEqualTo(dateRange.getEnd());
        }

        DeleteMaintenanceParams deleteParams = new DeleteMaintenanceParams();
        deleteParams.setDateRange(dateRange);
        deleteParams.setGroupId(groupId);

        callVoid = call("/api/v1/maintenance", HttpMethod.DELETE, deleteParams, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callMain = call("/api/v1/maintenance/" + groupId, HttpMethod.GET, MaintenanceSingleParams[].class);
        assertThat(callMain.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callMain.getBody()).length).isEqualTo(0);


        params.setDateRange(null);
        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        params.setDateRange(new DateRange());
        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(Timestamp.valueOf(LocalDateTime.of(2021, 5, 25, 0, 0, 0)));
        start = new Timestamp(cal.getTime().getTime());
        params.getDateRange().setStart(start);
        params.getDateRange().setEnd(new Timestamp(System.currentTimeMillis()));
        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        params.getDateRange().setStart(end);
        params.getDateRange().setEnd(start);
        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        start = new Timestamp(cal.getTime().getTime());
        params.getDateRange().setStart(start);
        cal.add(Calendar.DAY_OF_MONTH, 2);
        params.getDateRange().setEnd(start);
        params.setGroupId(null);
        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        params.setGroupId("");
        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        params.setGroupId("test");
        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        params.setGroupId(groupId);
        params.setUserIds(null);
        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        params.setUserIds(new ArrayList<>());
        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        List<String> testUsers = new ArrayList<>();
        testUsers.add("testUser");
        testUsers.add("testuser");
        params.setUserIds(testUsers);
        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        deleteGroup(groupId);
        deleteTenant(tenantId);
        for (UserEntity user : users) {
            deleteUser(user.getUserId());
        }
        callVoid = call("/api/v1/group/" + groupId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(514)
    public void editMaintenanceTest() {
        loginWithDefaultUserToken();
        String groupId = createGroup();
        String tenantId = createTenant();

        MaintenanceParams params = new MaintenanceParams();
        params.setGroupId(groupId);

        List<String> userList = new ArrayList<>();
        List<UserEntity> users = new ArrayList<>();

        users.add(createUser(tenantId));
        users.add(createUser(tenantId));
        userList.add(users.get(0).getUserId());
        userList.add(users.get(1).getUserId());

        params.setUserIds(userList);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        DateRange dateRange = new DateRange();
        Timestamp start = new Timestamp(cal.getTime().getTime());
        dateRange.setStart(start);
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Timestamp end = new Timestamp(cal.getTime().getTime());
        dateRange.setEnd(end);

        params.setDateRange(dateRange);

        ResponseEntity<Void> callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        MaintenanceEditParams editParams = new MaintenanceEditParams();
        editParams.setGroupId(groupId);
        editParams.setOldDateRange(dateRange);
        DateRange newDateRange = new DateRange();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        start = new Timestamp(cal.getTime().getTime());
        newDateRange.setStart(start);
        cal.add(Calendar.DAY_OF_MONTH, 3);
        end = new Timestamp(cal.getTime().getTime());
        newDateRange.setEnd(end);
        editParams.setNewDateRange(newDateRange);
        users.add(createUser(tenantId));
        userList.add(users.get(2).getUserId());
        editParams.setUserIds(userList);

        callVoid = call("/api/v1/maintenance/edit", HttpMethod.PUT, editParams, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<MaintenanceSingleParams[]> callMain = call("/api/v1/maintenance/" + groupId, HttpMethod.GET, MaintenanceSingleParams[].class);
        assertThat(callMain.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callMain.getBody()).length).isEqualTo(3);

        for (MaintenanceSingleParams maintenance : callMain.getBody()) {
            assertThat(maintenance.getGroupId()).isEqualTo(groupId);
            assertThat(userList.contains(maintenance.getUserId())).isTrue();
            assertThat(maintenance.getStartDate()).isEqualTo(newDateRange.getStart());
            assertThat(maintenance.getEndDate()).isEqualTo(newDateRange.getEnd());
        }

        DeleteMaintenanceParams deleteParams = new DeleteMaintenanceParams();
        deleteParams.setDateRange(newDateRange);
        deleteParams.setGroupId(groupId);

        callVoid = call("/api/v1/maintenance", HttpMethod.DELETE, deleteParams, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callMain = call("/api/v1/maintenance/" + groupId, HttpMethod.GET, MaintenanceSingleParams[].class);
        assertThat(callMain.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callMain.getBody()).length).isEqualTo(0);


        editParams.setNewDateRange(null);
        try {
            callVoid = call("/api/v1/maintenance/edit", HttpMethod.PUT, editParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        editParams.setNewDateRange(new DateRange());
        try {
            callVoid = call("/api/v1/maintenance/edit", HttpMethod.PUT, editParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(Timestamp.valueOf(LocalDateTime.of(2021, 5, 25, 0, 0, 0)));
        start = new Timestamp(cal.getTime().getTime());
        editParams.getNewDateRange().setStart(start);
        editParams.getNewDateRange().setEnd(new Timestamp(System.currentTimeMillis()));
        try {
            callVoid = call("/api/v1/maintenance/edit", HttpMethod.PUT, editParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        editParams.getNewDateRange().setStart(end);
        editParams.getNewDateRange().setEnd(start);
        try {
            callVoid = call("/api/v1/maintenance/edit", HttpMethod.PUT, editParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        start = new Timestamp(cal.getTime().getTime());
        editParams.getNewDateRange().setStart(start);
        cal.add(Calendar.DAY_OF_MONTH, 2);
        editParams.getNewDateRange().setEnd(start);
        editParams.setGroupId(null);
        try {
            callVoid = call("/api/v1/maintenance/edit", HttpMethod.PUT, editParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        editParams.setGroupId("");
        try {
            callVoid = call("/api/v1/maintenance/edit", HttpMethod.PUT, editParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        editParams.setGroupId("test");
        try {
            callVoid = call("/api/v1/maintenance/edit", HttpMethod.PUT, editParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        editParams.setGroupId(groupId);
        editParams.setUserIds(null);
        try {
            callVoid = call("/api/v1/maintenance/edit", HttpMethod.PUT, editParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        editParams.setUserIds(new ArrayList<>());
        try {
            callVoid = call("/api/v1/maintenance/edit", HttpMethod.PUT, editParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        List<String> testUsers = new ArrayList<>();
        testUsers.add("testUser");
        testUsers.add("testuser");
        editParams.setUserIds(testUsers);
        try {
            callVoid = call("/api/v1/maintenance/edit", HttpMethod.PUT, editParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        deleteGroup(groupId);
        deleteTenant(tenantId);
        for (UserEntity user : users) {
            deleteUser(user.getUserId());
        }
        callVoid = call("/api/v1/group/" + groupId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(515)
    public void deleteMaintenanceTest() {
        loginWithDefaultUserToken();
        String groupId = createGroup();
        String tenantId = createTenant();

        MaintenanceParams params = new MaintenanceParams();
        params.setGroupId(groupId);

        List<String> userList = new ArrayList<>();
        List<UserEntity> users = new ArrayList<>();

        users.add(createUser(tenantId));
        users.add(createUser(tenantId));
        userList.add(users.get(0).getUserId());
        userList.add(users.get(1).getUserId());

        params.setUserIds(userList);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        DateRange dateRange = new DateRange();
        Timestamp start = new Timestamp(cal.getTime().getTime());
        dateRange.setStart(start);
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Timestamp end = new Timestamp(cal.getTime().getTime());
        dateRange.setEnd(end);

        params.setDateRange(dateRange);

        ResponseEntity<Void> callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<MaintenanceSingleParams[]> callMain = call("/api/v1/maintenance/" + groupId, HttpMethod.GET, MaintenanceSingleParams[].class);
        assertThat(callMain.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callMain.getBody()).length).isEqualTo(2);

        for (MaintenanceSingleParams maintenance : callMain.getBody()) {
            assertThat(maintenance.getGroupId()).isEqualTo(groupId);
            assertThat(userList.contains(maintenance.getUserId())).isTrue();
            assertThat(maintenance.getStartDate()).isEqualTo(dateRange.getStart());
            assertThat(maintenance.getEndDate()).isEqualTo(dateRange.getEnd());
        }

        DeleteMaintenanceParams deleteParams = new DeleteMaintenanceParams();
        deleteParams.setDateRange(dateRange);
        deleteParams.setGroupId(groupId);

        deleteParams.setGroupId(null);
        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.DELETE, deleteParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        deleteParams.setGroupId("");
        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.DELETE, deleteParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        deleteParams.setGroupId("test");
        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.DELETE, deleteParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        deleteParams.setGroupId(groupId);
        deleteParams.setDateRange(null);
        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.DELETE, deleteParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        deleteParams.setDateRange(new DateRange());
        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.DELETE, deleteParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        deleteParams.setDateRange(dateRange);
        callVoid = call("/api/v1/maintenance", HttpMethod.DELETE, deleteParams, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        try {
            callVoid = call("/api/v1/maintenance", HttpMethod.DELETE, deleteParams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        callMain = call("/api/v1/maintenance/" + groupId, HttpMethod.GET, MaintenanceSingleParams[].class);
        assertThat(callMain.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callMain.getBody()).length).isEqualTo(0);

        deleteGroup(groupId);
        deleteTenant(tenantId);
        for (UserEntity user : users) {
            deleteUser(user.getUserId());
        }
        callVoid = call("/api/v1/group/" + groupId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(516)
    public void deleteUserFromMaintenanceTest() {
        loginWithDefaultUserToken();
        String groupId = createGroup();
        String tenantId = createTenant();

        MaintenanceParams params = new MaintenanceParams();
        params.setGroupId(groupId);

        List<String> userList = new ArrayList<>();
        List<UserEntity> users = new ArrayList<>();

        users.add(createUser(tenantId));
        users.add(createUser(tenantId));
        userList.add(users.get(0).getUserId());
        userList.add(users.get(1).getUserId());

        params.setUserIds(userList);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        DateRange dateRange = new DateRange();
        Timestamp start = new Timestamp(cal.getTime().getTime());
        dateRange.setStart(start);
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Timestamp end = new Timestamp(cal.getTime().getTime());
        dateRange.setEnd(end);

        params.setDateRange(dateRange);

        ResponseEntity<Void> callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<MaintenanceSingleParams[]> callMain = call("/api/v1/maintenance/" + groupId, HttpMethod.GET, MaintenanceSingleParams[].class);
        assertThat(callMain.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callMain.getBody()).length).isEqualTo(2);

        for (MaintenanceSingleParams maintenance : callMain.getBody()) {
            assertThat(maintenance.getGroupId()).isEqualTo(groupId);
            assertThat(userList.contains(maintenance.getUserId())).isTrue();
            assertThat(maintenance.getStartDate()).isEqualTo(dateRange.getStart());
            assertThat(maintenance.getEndDate()).isEqualTo(dateRange.getEnd());
        }

        MaintenanceSinglestParams deleteUserParam = new MaintenanceSinglestParams();
        deleteUserParam.setDateRange(dateRange);
        deleteUserParam.setGroupId(groupId);
        deleteUserParam.setUserId(userList.get(0));

        callVoid = call("/api/v1/maintenance/delete/user", HttpMethod.DELETE, deleteUserParam, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callMain = call("/api/v1/maintenance/" + groupId, HttpMethod.GET, MaintenanceSingleParams[].class);
        assertThat(callMain.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callMain.getBody()).length).isEqualTo(1);

        assertThat(callMain.getBody()[0].getUserId()).isEqualTo(userList.get(1));

        deleteUserParam.setGroupId(null);
        try {
            callVoid = call("/api/v1/maintenance/delete/user", HttpMethod.DELETE, deleteUserParam, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        deleteUserParam.setGroupId("");
        try {
            callVoid = call("/api/v1/maintenance/delete/user", HttpMethod.DELETE, deleteUserParam, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        deleteUserParam.setGroupId("test");
        try {
            callVoid = call("/api/v1/maintenance/delete/user", HttpMethod.DELETE, deleteUserParam, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        deleteUserParam.setGroupId(groupId);
        deleteUserParam.setDateRange(null);
        try {
            callVoid = call("/api/v1/maintenance/delete/user", HttpMethod.DELETE, deleteUserParam, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        deleteUserParam.setDateRange(new DateRange());
        try {
            callVoid = call("/api/v1/maintenance/delete/user", HttpMethod.DELETE, deleteUserParam, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        deleteUserParam.setDateRange(dateRange);
        deleteUserParam.setUserId(null);
        try {
            callVoid = call("/api/v1/maintenance/delete/user", HttpMethod.DELETE, deleteUserParam, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        deleteUserParam.setUserId("");
        try {
            callVoid = call("/api/v1/maintenance/delete/user", HttpMethod.DELETE, deleteUserParam, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        deleteUserParam.setUserId(userList.get(1));
        callVoid = call("/api/v1/maintenance/delete/user", HttpMethod.DELETE, deleteUserParam, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callMain = call("/api/v1/maintenance/" + groupId, HttpMethod.GET, MaintenanceSingleParams[].class);
        assertThat(callMain.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callMain.getBody()).length).isEqualTo(0);

        deleteGroup(groupId);
        deleteTenant(tenantId);
        for (UserEntity user : users) {
            deleteUser(user.getUserId());
        }
        callVoid = call("/api/v1/group/" + groupId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(517)
    public void getMaintenanceListForGroupTest() {
        loginWithDefaultUserToken();
        String groupId = createGroup();
        String tenantId = createTenant();

        MaintenanceParams params = new MaintenanceParams();
        params.setGroupId(groupId);

        List<String> userList = new ArrayList<>();
        List<UserEntity> users = new ArrayList<>();

        users.add(createUser(tenantId));
        users.add(createUser(tenantId));
        userList.add(users.get(0).getUserId());
        userList.add(users.get(1).getUserId());

        params.setUserIds(userList);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        DateRange dateRange = new DateRange();
        Timestamp start = new Timestamp(cal.getTime().getTime());
        dateRange.setStart(start);
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Timestamp end = new Timestamp(cal.getTime().getTime());
        dateRange.setEnd(end);

        params.setDateRange(dateRange);

        ResponseEntity<Void> callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<MaintenanceSingleParams[]> callMain = call("/api/v1/maintenance/" + groupId, HttpMethod.GET, MaintenanceSingleParams[].class);
        assertThat(callMain.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callMain.getBody()).length).isEqualTo(2);

        try {
            callMain = call("/api/v1/maintenance/test", HttpMethod.GET, MaintenanceSingleParams[].class);
            assertThat(callMain.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        DeleteMaintenanceParams deleteParams = new DeleteMaintenanceParams();
        deleteParams.setDateRange(dateRange);
        deleteParams.setGroupId(groupId);

        callVoid = call("/api/v1/maintenance", HttpMethod.DELETE, deleteParams, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        deleteGroup(groupId);
        deleteTenant(tenantId);
        for (UserEntity user : users) {
            deleteUser(user.getUserId());
        }
        callVoid = call("/api/v1/group/" + groupId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(518)
    public void getUniqueMaintenanceList() {
        loginWithDefaultUserToken();
        String groupId = createGroup();
        String tenantId = createTenant();

        MaintenanceParams params = new MaintenanceParams();
        params.setGroupId(groupId);

        List<String> userList = new ArrayList<>();
        List<UserEntity> users = new ArrayList<>();

        users.add(createUser(tenantId));
        users.add(createUser(tenantId));
        userList.add(users.get(0).getUserId());
        userList.add(users.get(1).getUserId());

        params.setUserIds(userList);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        DateRange dateRange = new DateRange();
        Timestamp start = new Timestamp(cal.getTime().getTime());
        dateRange.setStart(start);
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Timestamp end = new Timestamp(cal.getTime().getTime());
        dateRange.setEnd(end);

        params.setDateRange(dateRange);

        ResponseEntity<Void> callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        DateRange newDateRange = new DateRange();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        start = new Timestamp(cal.getTime().getTime());
        newDateRange.setStart(start);
        cal.add(Calendar.DAY_OF_MONTH, 5);
        end = new Timestamp(cal.getTime().getTime());
        newDateRange.setEnd(end);

        params.setDateRange(newDateRange);
        users.add(createUser(tenantId));
        userList.add(users.get(2).getUserId());
        params.setUserIds(userList);

        callVoid = call("/api/v1/maintenance", HttpMethod.PUT, params, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        DeleteMaintenanceParams deleteParams = new DeleteMaintenanceParams();
        deleteParams.setDateRange(dateRange);
        deleteParams.setGroupId(groupId);

        ResponseEntity<MaintenanceSingleParams[]> callMain = call("/api/v1/maintenance/list", HttpMethod.POST, deleteParams, MaintenanceSingleParams[].class);
        assertThat(callMain.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callMain.getBody()).length).isEqualTo(2);

        for (MaintenanceSingleParams maintenance : callMain.getBody()) {
            assertThat(maintenance.getGroupId()).isEqualTo(groupId);
            assertThat(userList.contains(maintenance.getUserId())).isTrue();
            assertThat(maintenance.getStartDate()).isEqualTo(dateRange.getStart());
            assertThat(maintenance.getEndDate()).isEqualTo(dateRange.getEnd());
        }

        deleteParams.setDateRange(newDateRange);
        deleteParams.setGroupId(groupId);

        callMain = call("/api/v1/maintenance/list", HttpMethod.POST, deleteParams, MaintenanceSingleParams[].class);
        assertThat(callMain.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callMain.getBody()).length).isEqualTo(3);

        for (MaintenanceSingleParams maintenance : callMain.getBody()) {
            assertThat(maintenance.getGroupId()).isEqualTo(groupId);
            assertThat(userList.contains(maintenance.getUserId())).isTrue();
            assertThat(maintenance.getStartDate()).isEqualTo(newDateRange.getStart());
            assertThat(maintenance.getEndDate()).isEqualTo(newDateRange.getEnd());
        }

        deleteParams.setDateRange(dateRange);
        deleteParams.setGroupId(groupId);

        callVoid = call("/api/v1/maintenance", HttpMethod.DELETE, deleteParams, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        deleteParams.setDateRange(newDateRange);
        deleteParams.setGroupId(groupId);

        callVoid = call("/api/v1/maintenance", HttpMethod.DELETE, deleteParams, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        deleteGroup(groupId);
        deleteTenant(tenantId);
        for (UserEntity user : users) {
            deleteUser(user.getUserId());
        }
        callVoid = call("/api/v1/group/" + groupId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
