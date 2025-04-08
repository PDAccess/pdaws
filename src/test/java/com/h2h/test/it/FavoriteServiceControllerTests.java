package com.h2h.test.it;

import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;
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
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FavoriteServiceControllerTests extends BaseIntegrationTests{

    @Test
    @Order(508)
    public void addServiceToFavTest() {
        loginWithDefaultUserToken();

        ResponseEntity<UserEntity> callUser = call("/api/v1/user/who", HttpMethod.GET, UserEntity.class);
        assertThat(callUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callUser.getBody()).isNotNull();
        UserEntity my = callUser.getBody();

        String groupId = createGroup();
        String tenantId = createTenant();
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        ResponseEntity<Void> callVoid = call("/api/v1/fav/mark/" + serviceId, HttpMethod.PUT, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);


        ResponseEntity<Boolean> callBoolean = call("/api/v1/fav/check/"+my.getUserId()+"/"+serviceId, HttpMethod.GET, Boolean.class);
        assertThat(callBoolean.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/service/" + serviceId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        try {
            callVoid = call("/api/v1/fav/mark/" + serviceId, HttpMethod.PUT, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        serviceId = createService(serviceEntity, groupId);

        UserEntity user = createUser(tenantId);

        try {
            callVoid = call("/api/v1/fav/mark/" + serviceId, HttpMethod.PUT, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        deleteUser(user.getUserId());

        try {
            callVoid = call("/api/v1/fav/mark/" + serviceId, HttpMethod.PUT, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        try {
            callVoid = call("/api/v1/fav/mark/test" + "/" + serviceId, HttpMethod.PUT, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        try {
            callVoid = call("/api/v1/fav/mark/" + my.getUserId() + "/test", HttpMethod.PUT, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        deleteService(serviceId);
        deleteGroup(groupId);
        deleteTenant(tenantId);
    }

    @Test
    @Order(509)
    public void unFavServiceTest() {
        loginWithDefaultUserToken();

        ResponseEntity<UserEntity> callUser = call("/api/v1/user/who", HttpMethod.GET, UserEntity.class);

        UserEntity my = callUser.getBody();

        String groupId = createGroup();
        String tenantId = createTenant();
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        assert my != null;
        ResponseEntity<Void> callVoid = call("/api/v1/fav/mark/" + serviceId, HttpMethod.PUT, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);


        ResponseEntity<Boolean> callBoolean = call("/api/v1/fav/check/"+my.getUserId()+"/"+serviceId, HttpMethod.GET, Boolean.class);
        assertThat(callBoolean.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/fav/mark/"+serviceId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callBoolean = call("/api/v1/fav/check/" + my.getUserId() + "/" + serviceId, HttpMethod.GET, Boolean.class);
        assertThat(callBoolean.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


        callVoid = call("/api/v1/service/" + serviceId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        try {
            callVoid = call("/api/v1/fav/mark/" + serviceId, HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        serviceId = createService(serviceEntity, groupId);

        UserEntity user = createUser(tenantId);

        try {
            callVoid = call("/api/v1/fav/mark/" + serviceId, HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        deleteUser(user.getUserId());

        try {
            callVoid = call("/api/v1/fav/mark/" + serviceId, HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        try {
            callVoid = call("/api/v1/fav/mark/test" + "/"+serviceId, HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        try {
            callVoid = call("/api/v1/fav/mark/" + my.getUserId() + "/test", HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        callVoid = call("/api/v1/service/" + serviceId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        deleteGroup(groupId);
        deleteTenant(tenantId);
    }

    @Test
    @Order(510)
    @Disabled
    public void getFavoriteServicesTest() {
        loginWithDefaultUserToken();

        ResponseEntity<UserEntity> callUser = call("/api/v1/user/who", HttpMethod.GET, UserEntity.class);

        UserEntity my = callUser.getBody();

        String groupId = createGroup();
        List<String> serviceList = new ArrayList<>();
        List<String> favServiceList = new ArrayList<>();

        for (int i = 0 ; i < 5 ; i++){
            ServiceEntity serviceEntity = new ServiceEntity();
            serviceEntity.setServiceTypeId(ServiceType.MYSQL);
            serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
            serviceEntity.setName("test"+i);
            serviceList.add(createService(serviceEntity, groupId));
        }
        favServiceList.add(serviceList.get(0));
        favServiceList.add(serviceList.get(1));
        favServiceList.add(serviceList.get(2));

        ResponseEntity<Void> callVoid;

        assert my != null;
        callVoid = call("/api/v1/fav/mark/" + serviceList.get(0), HttpMethod.PUT, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        callVoid = call("/api/v1/fav/mark/" + serviceList.get(1), HttpMethod.PUT, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        callVoid = call("/api/v1/fav/mark/" + serviceList.get(2), HttpMethod.PUT, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ServiceEntityWrapper[]> callServices = call("/api/v1/fav/service", HttpMethod.GET, ServiceEntityWrapper[].class);
        assertThat(callServices.getStatusCode()).isEqualTo(HttpStatus.OK);

        for (ServiceEntityWrapper serviceEntityWrapper : Objects.requireNonNull(callServices.getBody())) {
            assertThat(favServiceList.contains(serviceEntityWrapper.getInventoryId())).isTrue();
        }

        callVoid = call("/api/v1/fav/mark/"+serviceList.get(2), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callServices = call("/api/v1/fav/service", HttpMethod.GET, ServiceEntityWrapper[].class);
        assertThat(callServices.getStatusCode()).isEqualTo(HttpStatus.OK);

        for (ServiceEntityWrapper serviceEntityWrapper : Objects.requireNonNull(callServices.getBody())) {
            if (!favServiceList.contains(serviceEntityWrapper.getInventoryId())) assertThat(serviceEntityWrapper.getInventoryId()).isEqualTo(serviceList.get(2));
        }

        callVoid = call("/api/v1/fav/mark/" + serviceList.get(2), HttpMethod.PUT, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/service/" + serviceList.get(1), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callServices = call("/api/v1/fav/service", HttpMethod.GET, ServiceEntityWrapper[].class);
        assertThat(callServices.getStatusCode()).isEqualTo(HttpStatus.OK);

        for (ServiceEntityWrapper serviceEntityWrapper : Objects.requireNonNull(callServices.getBody())) {
            if (!favServiceList.contains(serviceEntityWrapper.getInventoryId())) assertThat(serviceEntityWrapper.getInventoryId()).isEqualTo(serviceList.get(1));
        }

        callVoid = call("/api/v1/service/" + serviceList.get(0), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/service/" + serviceList.get(2), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/service/" + serviceList.get(3), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/service/" + serviceList.get(4), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        deleteGroup(groupId);
    }

    @Test
    @Order(511)
    public void checkFavServiceTest(){
        loginWithDefaultUserToken();

        ResponseEntity<UserEntity> callUser = call("/api/v1/user/who", HttpMethod.GET, UserEntity.class);

        UserEntity my = callUser.getBody();

        String groupId = createGroup();
        String tenantId = createTenant();
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        assert my != null;
        ResponseEntity<Void> callVoid = call("/api/v1/fav/mark/" + serviceId, HttpMethod.PUT, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);


        ResponseEntity<Boolean> callBoolean = call("/api/v1/fav/check/"+my.getUserId()+"/"+serviceId, HttpMethod.GET, Boolean.class);
        assertThat(callBoolean.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/fav/mark/"+serviceId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callBoolean = call("/api/v1/fav/check/" + my.getUserId() + "/" + serviceId, HttpMethod.GET, Boolean.class);
        assertThat(callBoolean.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


        callVoid = call("/api/v1/service/" + serviceId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        try {
            callVoid = call("/api/v1/fav/check/" + my.getUserId() + "/" + serviceId, HttpMethod.GET, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        serviceId = createService(serviceEntity, groupId);

        UserEntity user = createUser(tenantId);

        try {
            callVoid = call("/api/v1/fav/check/" + user.getUserId() + "/" + serviceId, HttpMethod.GET, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        deleteUser(user.getUserId());

        try {
            callVoid = call("/api/v1/fav/check/" + user.getUserId() + "/" + serviceId, HttpMethod.GET, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        try {
            callVoid = call("/api/v1/fav/check/test" + "/" + serviceId, HttpMethod.GET, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        try {
            callVoid = call("/api/v1/fav/check/" + my.getUserId() + "/test", HttpMethod.GET, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        deleteService(serviceId);
        deleteGroup(groupId);
        deleteTenant(tenantId);
    }
}
