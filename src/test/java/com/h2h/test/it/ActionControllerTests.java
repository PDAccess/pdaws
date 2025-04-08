package com.h2h.test.it;

import com.h2h.pda.entity.ActionEntity;
import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.ActionPaginationParams;
import com.h2h.pda.pojo.ActionResponse;
import com.h2h.pda.pojo.ActionWrapper;
import com.h2h.pda.pojo.SessionWrapper;
import com.h2h.pda.pojo.service.ServiceOs;
import com.h2h.pda.pojo.service.ServiceType;
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

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public class ActionControllerTests extends BaseIntegrationTests{

    @Test
    @Order(109)
    public void createActionTest() throws InterruptedException {
        loginWithDefaultUserToken();

        String groupId = createGroup("groupAction");

        ServiceEntity entity = new ServiceEntity();
        entity.setName("test-service");
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);
        String serviceId = createService(entity, groupId);

        SessionWrapper sessionWrapper = createActionSession(serviceId);


        Calendar cal;
        ActionWrapper actionWrapper = new ActionWrapper();
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(new Timestamp(System.nanoTime()));
        actionWrapper.setActionTime(Timestamp.valueOf(LocalDateTime.now()));
        actionWrapper.setProxyAction("test");
        actionWrapper.setSessionId(sessionWrapper.getSessionId());

        ResponseEntity<Void> callVoid = call("/api/v1/action/create", HttpMethod.POST, actionWrapper, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ActionPaginationParams actionGetParams = new ActionPaginationParams();
        actionGetParams.setDateRange(null);
        actionGetParams.setCurrentPage(0);
        actionGetParams.setSort("createddesc");
        actionGetParams.setPerPage(15);
        actionGetParams.setFilter(null);

        Thread.sleep(1000);

        ResponseEntity<ActionResponse> callActions = call("/api/v1/action", HttpMethod.POST, actionGetParams, ActionResponse.class);
        assert callActions.getStatusCode().equals(HttpStatus.OK);
        assert callActions.getBody() != null;

        ActionResponse actionResponse = callActions.getBody();
        List<ActionWrapper> actions = actionResponse.getActionEntities();

        boolean check = false;

        for (ActionWrapper action : actions) {
            if (action.getSessionId() == sessionWrapper.getSessionId()) {
                check = true;
                assertThat(action.getActionTime().getTime()).isEqualTo(1000 * ((actionWrapper.getActionTime().getTime() + 500) / 1000));
                assertThat(action.getProxyAction()).isEqualTo(actionWrapper.getProxyAction());
                //SessionEntity session = action.getSessionEntity();
//                assertThat(session.getExternalSessionId()).isEqualTo(sessionWrapper.getExternalSessionId());
//                assertThat(session.getInventoryId()).isEqualTo(sessionWrapper.getInventoryId());
//                assertThat(session.getSessionType()).isEqualTo(sessionWrapper.getSessionType());
//                assertThat(session.getUsername()).isEqualTo(sessionWrapper.getUserEntity().getUsername());
//                assertThat(session.getStartTime().getTime()).isEqualTo(1000 * ((sessionWrapper.getStartTime().getTime()+500) / 1000));
//                assertThat(session.getIpAddress()).isEqualTo(sessionWrapper.getIpAddress());
//                if (session.getServiceEntity() != null) {
//                    assertThat(session.getServiceEntity().getInventoryId()).isEqualTo(sessionWrapper.getServiceEntity().getInventoryId());
//                }
//                if (session.getUserEntity() != null) {
//                    assertThat(session.getUserEntity().getUserId()).isEqualTo(sessionWrapper.getUserEntity().getUserId());
//                }
//                break;
            }
        }
        assertThat(check).isTrue().withFailMessage("Cannot find session id in action list");

        actionWrapper.setActionTime(null);
        try {
            callVoid = call("/api/v1/action/create", HttpMethod.POST, actionWrapper, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        cal.setTime(new Timestamp(System.currentTimeMillis()));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        actionWrapper.setActionTime(Timestamp.valueOf(LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId())));
        try {
            callVoid = call("/api/v1/action/create", HttpMethod.POST, actionWrapper, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        cal.setTime(new Timestamp(System.currentTimeMillis()));
        actionWrapper.setActionTime(Timestamp.valueOf(LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId())));
        actionWrapper.setProxyAction(null);
        try {
            callVoid = call("/api/v1/action/create", HttpMethod.POST, actionWrapper, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        actionWrapper.setProxyAction("");
        try {
            callVoid = call("/api/v1/action/create", HttpMethod.POST, actionWrapper, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        actionWrapper.setProxyAction("test2");
        actionWrapper.setSessionId(-1);
        try {
            callVoid = call("/api/v1/action/create", HttpMethod.POST, actionWrapper, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        deleteGroup(groupId);
        deleteService(sessionWrapper.getInventoryId());
    }

    @Test
    @Order(110)
    public void getAllActionsTest() {
        loginWithDefaultUserToken();
        String groupId = createGroup("getAllActionsTest");

        ServiceEntity entity = new ServiceEntity();
        entity.setName("test-service");
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);
        String serviceId = createService(entity, groupId);


        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        for (int i = 0; i < 3; i++) {
            SessionWrapper sessionWrapper = createActionSession(serviceId);
            ActionWrapper actionWrapper = new ActionWrapper();
            cal.setTime(new Timestamp(System.nanoTime()));
            actionWrapper.setActionTime(Timestamp.valueOf(LocalDateTime.now()));
            actionWrapper.setProxyAction("test " + i);
            actionWrapper.setSessionId(sessionWrapper.getSessionId());

            ResponseEntity<Void> callVoid = call("/api/v1/action/create", HttpMethod.POST, actionWrapper, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        ResponseEntity<ActionEntity[]> callActions = call("/api/v1/action/all", HttpMethod.GET, ActionEntity[].class);
        assert callActions.getStatusCode().equals(HttpStatus.OK);
        assert callActions.getBody() != null;

        deleteGroup(groupId);
        deleteService(serviceId);
    }

    @Test
    @Order(111)
    public void getAllActionsWithPaginationTest() {
        loginWithDefaultUserToken();
        String groupId = createGroup("getAllActionsWithPaginationTest");

        ServiceEntity entity = new ServiceEntity();
        entity.setName("test-service");
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);
        String serviceId = createService(entity, groupId);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        for (int i = 0; i < 15; i++) {
            SessionWrapper sessionWrapper = createActionSession(serviceId);
            ActionWrapper actionWrapper = new ActionWrapper();
            cal.setTime(new Timestamp(System.nanoTime()));
            actionWrapper.setActionTime(Timestamp.valueOf(LocalDateTime.now()));
            actionWrapper.setProxyAction("test " + i);
            actionWrapper.setSessionId(sessionWrapper.getSessionId());

            ResponseEntity<Void> callVoid = call("/api/v1/action/create", HttpMethod.POST, actionWrapper, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        ActionPaginationParams actionPaginationParams = new ActionPaginationParams();
        actionPaginationParams.setCurrentPage(0);
        actionPaginationParams.setPerPage(10);
        actionPaginationParams.setSort("created");

        ResponseEntity<ActionResponse> callActions = call("/api/v1/action", HttpMethod.POST, actionPaginationParams, ActionResponse.class);
        assertThat(callActions.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callActions.getBody()).isNotNull();
        assertThat(callActions.getBody().getActionEntities().size()).isEqualTo(10);

        deleteGroup(groupId);
        deleteService(serviceId);
    }

    @Test
    @Order(112)
    public void getActionByUseridTest() {
        loginWithDefaultUserToken();

        String tenantId = createTenant();
        UserEntity user = createUser(tenantId);

        loginWithUserToken(user.getUsername(), "123123123");

        for (int i = 0 ; i < 13 ; i++) {
            ResponseEntity<Void> callVoid = call("/api/v1/action/pda", HttpMethod.POST, "testaction " + i, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        ActionPaginationParams actionPaginationParams = new ActionPaginationParams();
        actionPaginationParams.setCurrentPage(0);
        actionPaginationParams.setPerPage(15);

        ResponseEntity<ActionEntity[]> callActions = call("/api/v1/action/user/" + user.getUserId(), HttpMethod.POST, actionPaginationParams, ActionEntity[].class);
        assertThat(callActions.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callActions.getBody()).length).isEqualTo(13);

        loginWithDefaultUserToken();
        deleteTenant(tenantId);
        deleteUser(user.getUserId());
    }

    @Test
    @Order(113)
    public void getActionByServiceIdTest() {
        loginWithDefaultUserToken();
        String groupId = createGroup("getActionByServiceIdTest");

        ServiceEntity entity = new ServiceEntity();
        entity.setName("test-service");
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);
        String serviceId = createService(entity, groupId);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        SessionWrapper sessionWrapper = createActionSession(serviceId);

        for (int i = 0; i < 16; i++) {
            ActionWrapper actionWrapper = new ActionWrapper();
            cal.setTime(new Timestamp(System.nanoTime()));
            actionWrapper.setActionTime(Timestamp.valueOf(LocalDateTime.now()));
            actionWrapper.setProxyAction("test " + i);
            actionWrapper.setSessionId(sessionWrapper.getSessionId());

            ResponseEntity<Void> callVoid = call("/api/v1/action/create", HttpMethod.POST, actionWrapper, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        ActionPaginationParams actionPaginationParams = new ActionPaginationParams();
        actionPaginationParams.setCurrentPage(0);
        actionPaginationParams.setPerPage(15);

        ResponseEntity<ActionEntity[]> callActions = call("/api/v1/action/service/" + serviceId, HttpMethod.POST, actionPaginationParams, ActionEntity[].class);
        assertThat(callActions.getStatusCode()).isEqualTo(HttpStatus.OK);
        // @ggultekin
        //assertThat(Objects.requireNonNull(callActions.getBody()).length).isEqualTo(15);

        deleteGroup(groupId);
        deleteService(serviceId);
    }

    @Test
    @Order(114)
    public void createActionPDATest() {
        loginWithDefaultUserToken();
        for (int i = 0 ; i < 8 ; i++) {
            ResponseEntity<Void> callVoid = call("/api/v1/action/pda", HttpMethod.POST, "testaction " + i, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }


    public SessionWrapper createActionSession(String serviceId) {

        ResponseEntity<ServiceEntity> callService = call("/api/v1/service/id/" + serviceId, HttpMethod.GET, ServiceEntity.class);
        assert callService.getStatusCode().equals(HttpStatus.OK);
        assertThat(callService.getBody()).isNotNull();
        ServiceEntity service = callService.getBody();

        ResponseEntity<UserEntity> callUser = call("/api/v1/user/who", HttpMethod.GET, UserEntity.class);
        assertThat(callUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callUser.getBody()).isNotNull();
        UserEntity userEntity = callUser.getBody();

        SessionWrapper sessionWrapper = new SessionWrapper("test-service");
        String externalID = UUID.randomUUID().toString();
        sessionWrapper.setUsername("admin");
        sessionWrapper.setStartTime(new Timestamp(System.currentTimeMillis()));
        sessionWrapper.setInventoryId(serviceId);
        sessionWrapper.setExternalSessionId(externalID);
        sessionWrapper.setSessionType("T");
        sessionWrapper.setIpAddress("1.1.1.1");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(new Timestamp(System.currentTimeMillis()));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        LocalDateTime end = LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());
//        sessionWrapper.setServiceEntity(service);
//        sessionWrapper.setUserEntity(userEntity);

        ResponseEntity<Integer> callInteger = call("/api/v1/session/create", HttpMethod.POST, sessionWrapper, Integer.class);
        assertThat(callInteger.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert callInteger.getBody() != null;
        int sessionId = callInteger.getBody();
        sessionWrapper.setSessionId(sessionId);

        return sessionWrapper;
    }
}