package com.h2h.test.it;

import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.jwt.LoginRequest;
import com.h2h.pda.pojo.*;
import com.h2h.pda.pojo.auth.AuthenticationAttemptEntityWrapper;
import com.h2h.pda.pojo.service.ServiceCreateParams;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;
import com.h2h.pda.pojo.service.ServiceOs;
import com.h2h.pda.pojo.service.ServiceType;
import com.h2h.test.util.PageHelper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SessionControllerTest extends BaseIntegrationTests {

    @Test
    @Order(10)
    @Disabled
    public void pdaLogin() throws Exception {
        LoginRequest entity = new LoginRequest();
        entity.setUsername(DEFAULT_USER);
        entity.setPassword(DEFAULT_USER_PASSWORD);

        String postForObject = this.restTemplate.postForObject("/login",
                entity, String.class);

        assertThat(postForObject).isNotNull();
    }

    @Test
    @Order(11)
    @Disabled
    public void authLogs() throws Exception {
        loginWithDefaultUserToken();

        Thread.sleep(1000);
        ResponseEntity<PageHelper<AuthenticationAttemptEntityWrapper>> call = call("/api/v1/auths/live", HttpMethod.POST, new Pagination(0, 15, "createddesc"), ParameterizedTypeReference.forType(PageHelper.class));

        assertThat(call.getBody().getTotalElements()).isGreaterThan(0);
    }

    @Test
    @Order(12)
    @Disabled
    public void listServices() throws Exception {
        loginWithDefaultUserToken();

        String groupId = createGroup();

        ServiceCreateParams params = new ServiceCreateParams();
        params.setIpaddress("1.1.1.1");
        params.setPort(22);
        Credential inventory = new Credential();
        inventory.setUsername("admin");

        params.setVaults(Collections.singletonList(inventory));
        ServiceEntity entity = new ServiceEntity();
        entity.setName("test-service");
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);
        params.setServiceEntity(new ServiceEntityWrapper(entity));
        params.setAdmin(inventory);
        params.setGroupid(groupId);

        ResponseEntity<String> call = call("/api/v1/service/service", HttpMethod.POST, params, String.class);

        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        String serviceId = call.getBody();

        SessionWrapper wrapper = new SessionWrapper("test");

        String externalID = UUID.randomUUID().toString();
        wrapper.setUsername("admin");
        wrapper.setStartTime(new Timestamp(System.currentTimeMillis()));
        wrapper.setInventoryId(call.getBody());
        wrapper.setExternalSessionId(externalID);
        wrapper.setSessionType("T");
        wrapper.setIpAddress("1.1.1.1");

        ResponseEntity<Integer> call2 = call("/api/v1/session/create", HttpMethod.POST, wrapper, Integer.class);

        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);
        wrapper.setSessionId(call2.getBody());

        ResponseEntity<Integer> call3 = call("/api/v1/session/logout", HttpMethod.PUT, wrapper, Integer.class);

        assertThat(call3.getStatusCode()).isEqualTo(HttpStatus.OK);

        SearchParams2 params2 = new SearchParams2();
        params2.setPagination(new Pagination(1, 10, "createddesc"));

        ResponseEntity<SessionFilterResponse> object = call("/api/v1/session/filter", HttpMethod.POST, params2, SessionFilterResponse.class);

        assertThat(object.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(object.getBody().getSessionCount()).isGreaterThan(0);

        ResponseEntity<Void> serviceDelete = call("/api/v1/service/" + serviceId, HttpMethod.DELETE, Void.class);
        assertThat(serviceDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}