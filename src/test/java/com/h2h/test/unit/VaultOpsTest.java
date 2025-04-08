package com.h2h.test.unit;

import com.h2h.pda.entity.CredentialEntity;
import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.PermissionService;
import com.h2h.pda.service.api.UsersOps;
import com.h2h.pda.service.api.VaultOps;
import com.h2h.test.it.BaseIntegrationTests;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class VaultOpsTest extends BaseIntegrationTests {
    private static final Logger log = LoggerFactory.getLogger(VaultOpsTest.class);

    @Autowired
    PermissionService permissionService;

    @Autowired
    VaultOps vaultOps;

    @Autowired
    GroupOps groupOps;

    @Autowired
    UsersOps usersOps;

    @Test
    public void createNewCredentials() {

        Optional<UserEntity> entity = usersOps.byName(DEFAULT_USER);

        String groupId = groupOps.newGroup(new GroupsEntity().setGroupName("test-group"), entity.get());

        CredentialEntity credentialEntity = new CredentialEntity();
        credentialEntity.setUsername("deneme");

        String credentialId = vaultOps.newCredential(groupId, entity.get().getUserId(), credentialEntity);

        List<CredentialEntity> credentialEntities = vaultOps.byGroup(groupId);

        assertThat(credentialEntities.size()).isEqualTo(1);

        groupOps.deleteById(groupId);
        vaultOps.delete(credentialId);
    }
}
