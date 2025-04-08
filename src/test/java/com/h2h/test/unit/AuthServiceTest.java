package com.h2h.test.unit;

import com.h2h.pda.entity.AuthenticationAttemptEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.Password;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.service.api.AuthenticationService;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.UsersOps;
import com.h2h.pda.service.api.VaultOps;
import com.h2h.test.it.BaseIntegrationTests;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthServiceTest extends BaseIntegrationTests {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceTest.class);

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    VaultOps vaultOps;

    @Autowired
    GroupOps groupOps;

    @Autowired
    UsersOps usersOps;

    @Test
    public void createNewCredentials() {
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(UserRole.USER);
        userEntity.setUsername(UUID.randomUUID().toString().substring(0, 16));
        userEntity = usersOps.newUser(userEntity, new Password("test123"));

        loginWithUserToken(userEntity.getUsername(), "test123");

        Page<AuthenticationAttemptEntity> entities = authenticationService.byUserName(userEntity.getUsername(), null, PageRequest.of(0, 100));

        assertThat(entities.getTotalElements()).isEqualTo(0);

        Page<AuthenticationAttemptEntity> attemptEntities = authenticationService.byUserService("12313", null, PageRequest.of(0, 100));

        assertThat(attemptEntities.getTotalElements()).isEqualTo(0);

    }
}
