package com.h2h.test.unit;

import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.service.ServiceCounter;
import com.h2h.pda.pojo.service.ServiceOs;
import com.h2h.pda.pojo.service.ServiceType;
import com.h2h.pda.pojo.service.UserServiceCounter;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.ServiceOps;
import com.h2h.pda.service.api.UsersOps;
import com.h2h.test.it.BaseIntegrationTests;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceOpsTest extends BaseIntegrationTests {
    private static final Logger log = LoggerFactory.getLogger(ServiceOpsTest.class);

    @Autowired
    GroupOps groupOps;

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    UsersOps usersOps;

    @Test
    public void serviceMembership() {
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(UserRole.USER);
        userEntity.setUsername(UUID.randomUUID().toString());
        userEntity = usersOps.newUser(userEntity);

        //Optional<UserEntity> defaultUser = usersOps.byName(DEFAULT_USER);

        String groupId = groupOps.newGroup(new GroupsEntity().setGroupName(UUID.randomUUID().toString()), userEntity);

        int countOfUsers = groupOps.addUsersTo(groupId, Collections.singletonList(userEntity.getUserId()));

        assertThat(countOfUsers).isEqualTo(1);

        List<UserEntity> groupUsers = groupOps.effectiveUsers(groupId);

        assertThat(groupUsers).isNotNull();
        assertThat(groupUsers.size()).isEqualTo(1);

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setDescription("test-service-desc");
        serviceEntity.setName("test-service");
        serviceEntity.setOperatingSystemId(ServiceOs.CENTOS);
        serviceEntity.setServiceTypeId(ServiceType.SSH);
        String orUpdate = serviceOps.createOrUpdate(serviceEntity);

        int serviceCount = groupOps.addServicesTo(groupId, Collections.singletonList(orUpdate));
        assertThat(serviceCount).isEqualTo(1);

        List<ServiceEntity> entities = serviceOps.collectEffectiveService(userEntity.getUserId());

        assertThat(entities.size()).isEqualTo(1);

        List<UserEntity> userEntities = serviceOps.collectEffectiveUsers(orUpdate);

        assertThat(userEntities.size()).isEqualTo(1);

        List<ServiceEntity> serviceEntities = groupOps.effectiveServices(groupId);

        assertThat(serviceEntities.size()).isEqualTo(1);

        int removeServicesFrom = groupOps.removeServicesFrom(groupId, Collections.singletonList(orUpdate));

        assertThat(removeServicesFrom).isEqualTo(1);

        serviceEntities = groupOps.effectiveServices(groupId);

        assertThat(serviceEntities.size()).isEqualTo(0);

        int removeUsersFrom = groupOps.removeUsersFrom(groupId, Collections.singletonList(userEntity.getUserId()));

        assertThat(removeUsersFrom).isEqualTo(1);

        List<UserEntity> userEntities2 = serviceOps.collectEffectiveUsers(orUpdate);

        assertThat(userEntities2.size()).isEqualTo(0);

        groupOps.deleteById(groupId);
        serviceOps.delete(orUpdate);
    }

    @Test
    public void userServiceList() {
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(UserRole.USER);
        userEntity.setUsername(UUID.randomUUID().toString());
        userEntity = usersOps.newUser(userEntity);

        String groupId = groupOps.newGroup(new GroupsEntity().setGroupName(UUID.randomUUID().toString()), userEntity);

        int countOfUsers = groupOps.addUsersTo(groupId, Collections.singletonList(userEntity.getUserId()));

        assertThat(countOfUsers).isEqualTo(1);

        List<UserEntity> groupUsers = groupOps.effectiveUsers(groupId);

        assertThat(groupUsers).isNotNull();
        assertThat(groupUsers.size()).isEqualTo(1);

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setDescription("test-service-desc");
        serviceEntity.setName(UUID.randomUUID().toString());
        serviceEntity.setIpAddress("1.2.3.4");
        serviceEntity.setOperatingSystemId(ServiceOs.CENTOS);
        serviceEntity.setServiceTypeId(ServiceType.SSH);
        String orUpdate = serviceOps.createOrUpdate(serviceEntity);

        int serviceCount = groupOps.addServicesTo(groupId, Collections.singletonList(orUpdate));
        assertThat(serviceCount).isEqualTo(1);

        List<ServiceEntity> search = serviceOps.search(userEntity.getUserId(), null, null, PageRequest.of(0, 10));

        assertThat(search.size()).isEqualTo(1);

        search = serviceOps.search(userEntity.getUserId(), "test", null, PageRequest.of(0, 10));

        assertThat(search.size()).isEqualTo(1);

        search = serviceOps.search(userEntity.getUserId(), "desc", null, PageRequest.of(0, 10));

        assertThat(search.size()).isEqualTo(1);

        search = serviceOps.search(userEntity.getUserId(), "hede", null, PageRequest.of(0, 10));

        assertThat(search.size()).isEqualTo(0);

        search = serviceOps.search(userEntity.getUserId(), "2.3", "2.3", PageRequest.of(0, 10));

        assertThat(search.size()).isEqualTo(1);

        search = serviceOps.search(userEntity.getUserId(), "2.7", "2.7", PageRequest.of(0, 10));

        assertThat(search.size()).isEqualTo(0);

        search = serviceOps.search(userEntity.getUserId() + "x", "2.3", "2.3", PageRequest.of(0, 10));

        assertThat(search.size()).isEqualTo(0);

        serviceOps.delete(orUpdate);
        groupOps.deleteById(groupId);
    }

    @Test
    public void serviceCounters() {
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(UserRole.USER);
        userEntity.setUsername(UUID.randomUUID().toString());
        userEntity = usersOps.newUser(userEntity);

        String groupId = groupOps.newGroup(new GroupsEntity().setGroupName(UUID.randomUUID().toString()), userEntity);
        String groupId2 = groupOps.newGroup(new GroupsEntity().setGroupName(UUID.randomUUID().toString()), userEntity);

        int countOfUsers = groupOps.addUsersTo(groupId, Collections.singletonList(userEntity.getUserId()));

        assertThat(countOfUsers).isEqualTo(1);

        List<UserEntity> groupUsers = groupOps.effectiveUsers(groupId);

        assertThat(groupUsers).isNotNull();
        assertThat(groupUsers.size()).isEqualTo(1);

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setDescription("test-service-desc");
        serviceEntity.setName("test-service");
        serviceEntity.setIpAddress("1.2.3.4");
        serviceEntity.setOperatingSystemId(ServiceOs.CENTOS);
        serviceEntity.setServiceTypeId(ServiceType.SSH);
        String orUpdate = serviceOps.createOrUpdate(serviceEntity);

        int serviceCount = groupOps.addServicesTo(groupId, Collections.singletonList(orUpdate));
        serviceCount = groupOps.addServicesTo(groupId2, Collections.singletonList(orUpdate));
        assertThat(serviceCount).isEqualTo(1);

        ServiceCounter counters = serviceOps.counters(orUpdate);

        assertThat(counters.getGroups()).isEqualTo(2);
        assertThat(counters.getMembers()).isEqualTo(2);

        UserServiceCounter userCounters = serviceOps.userCounters(userEntity.getUserId());

        assertThat(userCounters.getYours()).isEqualTo(1);

        serviceOps.delete(orUpdate);
        groupOps.deleteById(groupId);
        groupOps.deleteById(groupId2);
    }
}
