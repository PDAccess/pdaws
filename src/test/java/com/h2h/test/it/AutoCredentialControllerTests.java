package com.h2h.test.it;

import com.h2h.pda.entity.AutoCredantialSettingsEntity;
import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.AutoCredentialSettingsWrapper;
import com.h2h.pda.pojo.service.ServiceOs;
import com.h2h.pda.pojo.service.ServiceType;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AutoCredentialControllerTests extends BaseIntegrationTests {

    @Test
    @Order(505)
    @Disabled
    public void addCredentialTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup();
        String tenantId = createTenant();
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        AutoCredentialSettingsWrapper autoCredentialparams = new AutoCredentialSettingsWrapper();
        autoCredentialparams.setAutoCredentialTime(4800);
        autoCredentialparams.setAutoCredentialTimeType("hour");

        ResponseEntity<Void> callVoid = call("/api/v1/auto/credantial/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<AutoCredantialSettingsEntity> callSetting = call("/api/v1/auto/credantial/" + serviceId, HttpMethod.GET, AutoCredantialSettingsEntity.class);
        assertThat(callSetting.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callSetting.getBody()).isNotNull();

        AutoCredantialSettingsEntity autoCredantial = callSetting.getBody();
        assertThat(autoCredantial.getAutoCredantialTime()).isEqualTo(3600);
        assertThat(autoCredantial.getAutoCredantialTimeType()).isEqualTo(autoCredentialparams.getAutoCredentialTimeType());


        callVoid = call("/api/v1/auto/credential/test", HttpMethod.POST, autoCredentialparams, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        UserEntity userEntity = createUser(tenantId);
        loginWithUserToken(userEntity.getUsername(), "123123123");

        try {
            callVoid = call("/api/v1/auto/credential/test", HttpMethod.POST, autoCredentialparams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        loginWithDefaultUserToken();
        deleteUser(userEntity.getUserId());

        autoCredentialparams.setAutoCredentialTime(3599);
        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        autoCredentialparams.setAutoCredentialTime(3600);
        autoCredentialparams.setAutoCredentialTimeType("test");
        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        autoCredentialparams.setAutoCredentialTimeType("");
        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        autoCredentialparams.setAutoCredentialTimeType(null);
        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        autoCredentialparams.setAutoCredentialTimeType("day");
        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        autoCredentialparams.setAutoCredentialTimeType("week");
        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        autoCredentialparams.setAutoCredentialTimeType("month");
        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        autoCredentialparams.setAutoCredentialTimeType("year");
        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        autoCredentialparams.setAutoCredentialTime(86400);
        autoCredentialparams.setAutoCredentialTimeType("week");
        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        autoCredentialparams.setAutoCredentialTimeType("month");
        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        autoCredentialparams.setAutoCredentialTimeType("year");
        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        autoCredentialparams.setAutoCredentialTime(604800);
        autoCredentialparams.setAutoCredentialTimeType("month");
        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        autoCredentialparams.setAutoCredentialTimeType("year");
        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        autoCredentialparams.setAutoCredentialTime(2592000);
        autoCredentialparams.setAutoCredentialTimeType("year");
        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        autoCredentialparams.setAutoCredentialTime(31536000);

        callVoid = call("/api/v1/service/" + serviceId, HttpMethod.DELETE, Void.class);
        AssertionsForClassTypes.assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        deleteTenant(tenantId);
        deleteGroup(groupId);
    }

    @Test
    @Order(506)
    @Disabled
    public void deleteCredential() {
        loginWithDefaultUserToken();

        String groupId = createGroup();
        String tenantId = createTenant();
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        ResponseEntity<Void> callVoid;

        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        AutoCredentialSettingsWrapper autoCredentialparams = new AutoCredentialSettingsWrapper();
        autoCredentialparams.setAutoCredentialTime(4800);
        autoCredentialparams.setAutoCredentialTimeType("hour");

        callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserEntity userEntity = createUser(tenantId);
        loginWithUserToken(userEntity.getUsername(), "123123123");

        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        loginWithDefaultUserToken();

        deleteUser(userEntity.getUserId());

        callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        try {
            ResponseEntity<AutoCredantialSettingsEntity> callSetting = call("/api/v1/auto/credential/" + serviceId, HttpMethod.GET, AutoCredantialSettingsEntity.class);
            assertThat(callSetting.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        try {
            callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        try {
            callVoid = call("/api/v1/auto/credential/test", HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        callVoid = call("/api/v1/service/" + serviceId, HttpMethod.DELETE, Void.class);
        AssertionsForClassTypes.assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        deleteTenant(tenantId);
        deleteGroup(groupId);
    }

    @Test
    @Order(507)
    @Disabled
    public void faultCredential() {
        loginWithDefaultUserToken();
        ResponseEntity<AutoCredantialSettingsEntity> callSetting;

        String groupId = createGroup();
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        try {
            callSetting = call("/api/v1/auto/credential/test", HttpMethod.GET, AutoCredantialSettingsEntity.class);
            assertThat(callSetting.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }


        AutoCredentialSettingsWrapper autoCredentialparams = new AutoCredentialSettingsWrapper();
        autoCredentialparams.setAutoCredentialTime(4800);
        autoCredentialparams.setAutoCredentialTimeType("hour");

        ResponseEntity<Void> callVoid = call("/api/v1/auto/credential/" + serviceId, HttpMethod.POST, autoCredentialparams, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callSetting = call("/api/v1/auto/credential/" + serviceId, HttpMethod.GET, AutoCredantialSettingsEntity.class);
        assertThat(callSetting.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/service/" + serviceId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        try {
            callSetting = call("/api/v1/auto/credential/" + serviceId, HttpMethod.GET, AutoCredantialSettingsEntity.class);
            assertThat(callSetting.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
