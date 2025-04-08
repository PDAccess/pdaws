package com.h2h.test.it;

import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.pojo.group.GroupServices;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;
import com.h2h.pda.pojo.service.ServiceOs;
import com.h2h.pda.pojo.service.ServiceType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupServiceControllerTests extends BaseIntegrationTests {
    static final String URL = "/api/v1/group/service/";
    static final String SERVICE_URL = "/api/v1/service/";

    @Test
    @Order(550)
    @Disabled
    public void groupServiceTests() {
        loginWithDefaultUserToken();
        UserEntity user = getLoggedInUser();
        assertThat(user).isNotNull();
        String groupId = createGroup();
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.of(randomNumber()));
        serviceEntity.setOperatingSystemId(ServiceOs.of(randomNumber()));
        serviceEntity.setName("test");
        createService(serviceEntity, groupId);

        ResponseEntity<ServiceEntityWrapper[]> serviceListCall = call(SERVICE_URL + "all", HttpMethod.GET, ServiceEntityWrapper[].class);
        assertThat(serviceListCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ServiceEntityWrapper> services = Arrays.asList(Objects.requireNonNull(serviceListCall.getBody()));
        assertThat(services.isEmpty()).isNotEqualTo(true);

        List<String> serviceIds = new ArrayList<>();

        for (ServiceEntityWrapper ser : services) {
            serviceIds.add(ser.getInventoryId());
        }

        ResponseEntity<Void> addGroupServicesCall = call(URL + groupId, HttpMethod.POST, serviceIds, Void.class);
        assertThat(addGroupServicesCall.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ServiceEntityWrapper[]> getGroupServices = call(URL+groupId,HttpMethod.GET,ServiceEntityWrapper[].class);
        assertThat(getGroupServices.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ServiceEntityWrapper[]> getGroupUserServices = call(URL+groupId+"/"+user.getUserId(),HttpMethod.GET,ServiceEntityWrapper[].class);
        assertThat(getGroupUserServices.getStatusCode()).isEqualTo(HttpStatus.OK);

        deleteServices(serviceIds);
    }

    @Test
    @Order(551)
    @Disabled
    public void groupServicesWithPaginationTest() {
        loginWithDefaultUserToken();
        String groupId = createGroup();

        List<String> serviceList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            ServiceEntity entity = new ServiceEntity();
            entity.setName("test-service " + i);
            entity.setOperatingSystemId(ServiceOs.UBUNTU);
            entity.setServiceTypeId(ServiceType.SSH);
            serviceList.add(createService(entity, groupId));
        }
        Pagination pagination = new Pagination();
        pagination.setCurrentPage(0);
        pagination.setPerPage(10);

        ResponseEntity<GroupServices> callGroupServices = call("/api/v1/group/service/" + groupId, HttpMethod.POST, pagination, GroupServices.class);
        assertThat(callGroupServices.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callGroupServices.getBody()).isNotNull();
        assertThat(callGroupServices.getBody().getTotalRows()).isEqualTo(5);
        assertThat(callGroupServices.getBody().getTotalService()).isEqualTo(5);

        deleteService(serviceList.get(4));

        callGroupServices = call("/api/v1/group/service/" + groupId, HttpMethod.POST, pagination, GroupServices.class);
        assertThat(callGroupServices.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callGroupServices.getBody()).isNotNull();
        assertThat(callGroupServices.getBody().getTotalService()).isEqualTo(4);

        serviceList.remove(4);
        deleteServices(serviceList);
        deleteGroup(groupId);
    }

    @Test
    @Order(552)
    public void groupServiceAddTest() {
        loginWithDefaultUserToken();
        String groupId = createGroup();

        List<String> serviceIds = new ArrayList<>();

        ServiceEntity entity = new ServiceEntity();
        entity.setName("test-service");
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);
        serviceIds.add(createService(entity, groupId));

        String otherGroupId = createGroup();

        ResponseEntity<Integer> callAddServiceToGroup = call("/api/v1/group/service/" + otherGroupId, HttpMethod.PUT, serviceIds, Integer.class);
        assertThat(callAddServiceToGroup.getStatusCode()).isEqualTo(HttpStatus.OK);

        ServiceEntity entity2 = new ServiceEntity();
        entity2.setName("test-service 2");
        entity2.setOperatingSystemId(ServiceOs.UBUNTU);
        entity2.setServiceTypeId(ServiceType.SSH);
        serviceIds.add(createService(entity2, groupId));

        ResponseEntity<Integer> callAddService2ToGroup = call("/api/v1/group/service/" + otherGroupId, HttpMethod.PUT, serviceIds, Integer.class);
        assertThat(callAddService2ToGroup.getStatusCode()).isEqualTo(HttpStatus.OK);

        deleteServices(serviceIds);
        deleteGroup(groupId);
        deleteGroup(otherGroupId);
    }

    @Test
    @Order(553)
    public void groupServiceDeleteTest() {
        loginWithDefaultUserToken();
        String groupId = createGroup();

        List<String> serviceIds = new ArrayList<>();

        ServiceEntity entity = new ServiceEntity();
        entity.setName("test-service");
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);
        serviceIds.add(createService(entity, groupId));

        ServiceEntity entity2 = new ServiceEntity();
        entity2.setName("test-service 2");
        entity2.setOperatingSystemId(ServiceOs.UBUNTU);
        entity2.setServiceTypeId(ServiceType.SSH);
        serviceIds.add(createService(entity2, groupId));

        for (String serviceId:serviceIds) {
            ResponseEntity<Void> callDeleteServiceFromGroup = call("/api/v1/group/service/" + groupId + "/" + serviceId, HttpMethod.DELETE, Void.class);
            assertThat(callDeleteServiceFromGroup.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        deleteServices(serviceIds);
        deleteGroup(groupId);
    }

    @Test
    @Order(554)
    public void groupServicesDeleteTest() {
        loginWithDefaultUserToken();
        String groupId = createGroup();

        List<String> serviceIds = new ArrayList<>();

        ServiceEntity entity = new ServiceEntity();
        entity.setName("test-service");
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);
        serviceIds.add(createService(entity, groupId));

        ServiceEntity entity2 = new ServiceEntity();
        entity2.setName("test-service 2");
        entity2.setOperatingSystemId(ServiceOs.UBUNTU);
        entity2.setServiceTypeId(ServiceType.SSH);
        serviceIds.add(createService(entity2, groupId));

        ResponseEntity<Void> callDeleteServiceFromGroup = call("/api/v1/group/service/delete/" + groupId, HttpMethod.POST, serviceIds, Void.class);
        assertThat(callDeleteServiceFromGroup.getStatusCode()).isEqualTo(HttpStatus.OK);

        deleteServices(serviceIds);
        deleteGroup(groupId);
    }

}
