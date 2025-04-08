package com.h2h.test.it;

import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.group.GroupRole;
import com.h2h.pda.pojo.group.GroupUserCreateParams;
import com.h2h.pda.pojo.service.ServiceOs;
import com.h2h.pda.pojo.service.ServiceType;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class InternalControllerTests extends BaseIntegrationTests {

    @Test
    @Order(700)
    public void checkServiceTest() {
        loginWithDefaultUserToken();

        String notExistServiceId = UUID.randomUUID().toString();
        String notExistUsername = UUID.randomUUID().toString();

        ResponseEntity<Void> call;

        call = call(String.format("/api/v1/internal/service/check/%s/%s", notExistServiceId, notExistUsername), HttpMethod.GET, null, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        String tenantId = createTenant();
        UserEntity user = createUser(tenantId);
        String userId = user.getUserId();
        String username = user.getUsername();

        call = call(String.format("/api/v1/internal/service/check/%s/%s", notExistServiceId, username), HttpMethod.GET, null, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        String groupId = createGroup();

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        call = call(String.format("/api/v1/internal/service/check/%s/%s", serviceId, notExistUsername), HttpMethod.GET, null, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        call = call(String.format("/api/v1/internal/service/check/%s/%s", serviceId, username), HttpMethod.GET, null, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        GroupUserCreateParams params = new GroupUserCreateParams();
        params.setUserlist(Collections.singletonList(userId));
        params.setRole(GroupRole.USER.name());
        ResponseEntity<Void> callVoid = call("/api/v1/group/user/" + groupId, HttpMethod.POST, params, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        call = call(String.format("/api/v1/internal/service/check/%s/%s", serviceId, username), HttpMethod.GET, null, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        deleteUser(userId);
        deleteService(serviceId);
        deleteGroup(groupId);
    }

}
