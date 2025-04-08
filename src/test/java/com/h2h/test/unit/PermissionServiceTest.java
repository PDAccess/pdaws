package com.h2h.test.unit;

import com.h2h.pda.entity.CredentialEntity;
import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.PermissionEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.permission.Permissions;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.PermissionService;
import com.h2h.pda.service.api.UsersOps;
import com.h2h.pda.service.api.VaultOps;
import com.h2h.test.it.BaseIntegrationTests;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PermissionServiceTest extends BaseIntegrationTests {
    private static final Logger log = LoggerFactory.getLogger(PermissionServiceTest.class);

    @Autowired
    PermissionService permissionService;

    @Autowired
    VaultOps vaultOps;

    @Autowired
    GroupOps groupOps;

    @Autowired
    UsersOps usersOps;

    @Test
    public void hasPermissionCheck() {

        Optional<UserEntity> entity = usersOps.byName(DEFAULT_USER);

        String groupId = groupOps.newGroup(new GroupsEntity().setGroupName("test-group"), entity.get());

        CredentialEntity credentialEntity = new CredentialEntity();
        credentialEntity.setUsername("deneme");

        String credentialId = vaultOps.newCredential(groupId, entity.get().getUserId(), credentialEntity);

        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setPermissionsSet(Collections.singleton(Permissions.CAN_CONNECT));

        permissionEntity = permissionService.createPermission(credentialId, entity.get().getUserId(), permissionEntity);

        Map<String, Set<Permissions>> strings = permissionService.effectivePermissions(credentialId);

        assertThat(strings).isNotNull();
        assertThat(strings.size()).isEqualTo(1);

        assertThat(permissionService.hasPermission(credentialId, entity.get().getUserId(), Permissions.CAN_CONNECT)).isTrue();
        assertThat(permissionService.hasPermission(credentialId, entity.get().getUserId(), Permissions.CAN_CHANGE_PASSWORD)).isFalse();

        permissionService.delete(permissionEntity.getPermissionId());
        vaultOps.delete(credentialId);
        groupOps.deleteById(groupId);
    }
}
