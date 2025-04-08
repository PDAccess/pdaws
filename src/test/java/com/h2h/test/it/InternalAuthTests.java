package com.h2h.test.it;

import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.SettingParam;
import com.h2h.pda.pojo.group.GroupRole;
import com.h2h.pda.pojo.service.ServiceOs;
import com.h2h.pda.pojo.service.ServiceType;
import com.h2h.pda.pojo.system.SystemSettingTags;
import com.h2h.pda.service.api.GroupOps;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class InternalAuthTests extends BaseIntegrationTests {

    @Autowired
    GroupOps groupOps;

    @Test
    @Order(1000)
    public void internalAuthByNoAdminAccessSettings() {
        loginWithDefaultUserToken();
        String tenantId = createTenant();

        String groupId = createGroup("testtt");

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        List<SettingParam> settingParams = new ArrayList<>();
        settingParams.add(new SettingParam(SystemSettingTags.NO_LOGIN_TO_DEVICE_FROM_ADMIN_USERS, "false"));

        ResponseEntity<Void> call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserEntity loginUser = createAdminUser(tenantId);

        groupOps.addServicesTo(groupId, Collections.singletonList(serviceId));
        groupOps.addUsersTo(groupId, Collections.singletonList(loginUser.getUserId()), GroupRole.ADMIN);

        Map<String, String> map = new HashMap<>();
        map.put("username", loginUser.getUsername());
        map.put("password", "123123123");
        map.put("service", serviceId);
        map.put("loginType", "internal");

        ResponseEntity<Void> loginResponse = call("/login", HttpMethod.POST, map, Void.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        deleteService(serviceId);
        deleteGroup(groupId);
        hardDeleteUser(loginUser.getUserId());
    }

    @Test
    @Order(1001)
    public void internalAuthByEnabledAdminAccessSettings() {
        loginWithDefaultUserToken();
        String tenantId = createTenant();

        String groupId = createGroup("testtt");

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        List<SettingParam> settingParams = new ArrayList<>();
        settingParams.add(new SettingParam(SystemSettingTags.NO_LOGIN_TO_DEVICE_FROM_ADMIN_USERS, "true"));

        ResponseEntity<Void> call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserEntity loginUser = createAdminUser(tenantId);

        groupOps.addServicesTo(groupId, Collections.singletonList(serviceId));
        groupOps.addUsersTo(groupId, Collections.singletonList(loginUser.getUserId()), GroupRole.ADMIN);

        Map<String, String> map = new HashMap<>();
        map.put("username", loginUser.getUsername());
        map.put("password", "123123123");
        map.put("service", serviceId);
        map.put("loginType", "internal");

        try {
            ResponseEntity<Void> loginResponse = call("/login", HttpMethod.POST, map, Void.class);
            assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        deleteService(serviceId);
        deleteGroup(groupId);
        hardDeleteUser(loginUser.getUserId());
    }

}
