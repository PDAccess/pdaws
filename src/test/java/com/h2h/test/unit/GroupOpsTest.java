package com.h2h.test.unit;

import com.h2h.pda.entity.GroupProperty;
import com.h2h.pda.entity.GroupUserEntity;
import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.group.*;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.UsersOps;
import com.h2h.test.it.BaseIntegrationTests;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupOpsTest extends BaseIntegrationTests {
    private static final Logger log = LoggerFactory.getLogger(GroupOpsTest.class);

    @Autowired
    GroupOps groupOps;

    @Autowired
    UsersOps usersOps;

    @Test
    public void groupMembership() {
        Optional<UserEntity> entity = usersOps.byName(DEFAULT_USER);

        String s = groupOps.newGroup(new GroupsEntity().setGroupName("test-group"), entity.get());

        int i = groupOps.addUsersTo(s, Collections.singletonList(entity.get().getUserId()));

        assertThat(i).isEqualTo(1);

        List<UserEntity> entities = groupOps.effectiveUsers(s);

        assertThat(entities).isNotNull();
        assertThat(entities.size()).isEqualTo(1);

        groupOps.deleteById(s);
    }

    @Test
    public void searchGroup() {
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(UserRole.USER);
        userEntity.setUsername(UUID.randomUUID().toString());
        userEntity = usersOps.newUser(userEntity);

        UserEntity userEntity2 = new UserEntity();
        userEntity2.setRole(UserRole.USER);
        userEntity2.setUsername(UUID.randomUUID().toString());
        userEntity2 = usersOps.newUser(userEntity2);

        String s = groupOps.newGroup(new GroupsEntity().setGroupName(UUID.randomUUID().toString()), userEntity);

        int i = groupOps.addUsersTo(s, Collections.singletonList(userEntity2.getUserId()));

        assertThat(i).isEqualTo(2);

        List<UserEntity> entities = groupOps.effectiveUsers(s);

        assertThat(entities).isNotNull();
        assertThat(entities.size()).isEqualTo(2);

        List<GroupsEntity> groupsEntities = groupOps.searchBy(userEntity.getUserId(), PageRequest.of(0, 10, Sort.by("groupName")), GroupCategory.NORMAL);

        assertThat(groupsEntities.size()).isEqualTo(1);

        groupOps.deleteById(s);
    }

    @Test
    public void countGroups() {
        Optional<UserEntity> entity = usersOps.byName(DEFAULT_USER);
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(UserRole.USER);
        userEntity.setUsername(UUID.randomUUID().toString());
        userEntity = usersOps.newUser(userEntity);

        String s = groupOps.newGroup(new GroupsEntity().setGroupName("test-group"), entity.get());

        int i = groupOps.addUsersTo(s, Collections.singletonList(userEntity.getUserId()), GroupRole.ADMIN);

        assertThat(i).isEqualTo(2);

        List<UserEntity> entities = groupOps.effectiveUsers(s);

        assertThat(entities).isNotNull();
        assertThat(entities.size()).isEqualTo(2);

        GroupCounter counters = groupOps.counters(s, GroupRole.ADMIN);

        assertThat(counters.getMembers()).isEqualTo(2);
        assertThat(counters.getService()).isEqualTo(0);

        groupOps.deleteById(s);
    }

    @Test
    public void countUserGroups() {
        Optional<UserEntity> entity = usersOps.byName(DEFAULT_USER);
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(UserRole.USER);
        userEntity.setUsername(UUID.randomUUID().toString());
        userEntity = usersOps.newUser(userEntity);

        String s1 = groupOps.newGroup(new GroupsEntity().setGroupName("test-group"), entity.get());
        String s2 = groupOps.newGroup(new GroupsEntity().setGroupName("test-group2"), entity.get());

        int i = groupOps.addUsersTo(s1, Collections.singletonList(userEntity.getUserId()), GroupRole.USER);
        assertThat(i).isEqualTo(2);
        i = groupOps.addUsersTo(s2, Collections.singletonList(userEntity.getUserId()), GroupRole.ADMIN);
        assertThat(i).isEqualTo(2);

        UserGroupCounter userGroupCounter = groupOps.userCounters(userEntity.getUserId());

        assertThat(userGroupCounter.getYours()).isEqualTo(1);
        assertThat(userGroupCounter.getJoined()).isEqualTo(1);

        groupOps.deleteById(s1);
        groupOps.deleteById(s2);
    }

    @Test
    public void effectiveMembers() {
        Optional<UserEntity> entity = usersOps.byName(DEFAULT_USER);
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(UserRole.USER);
        userEntity.setUsername(UUID.randomUUID().toString());
        userEntity = usersOps.newUser(userEntity);

        String s1 = groupOps.newGroup(new GroupsEntity().setGroupName("test-group"), entity.get());
        String s2 = groupOps.newGroup(new GroupsEntity().setGroupName("test-group2"), entity.get());

        Optional<GroupsEntity> groupsEntity = groupOps.byId(s1);
        GroupsEntity groupsEntity1 = groupsEntity.get();
        groupsEntity1.getProperties().add(new GroupProperty(GroupProperties.IDLE_TIMEOUT, "1"));
        groupOps.update(groupsEntity1);

        int i = groupOps.addUsersTo(s1, Collections.singletonList(userEntity.getUserId()), GroupRole.USER);
        assertThat(i).isEqualTo(2);
        i = groupOps.addUsersTo(s2, Collections.singletonList(userEntity.getUserId()), GroupRole.ADMIN);
        assertThat(i).isEqualTo(2);

        Iterable<GroupUserEntity> userGroupCounter = groupOps.effectiveMembers(s1);
        assertThat(userGroupCounter.iterator().hasNext()).isTrue();

        groupOps.deleteById(s1);
        groupOps.deleteById(s2);
    }
}
