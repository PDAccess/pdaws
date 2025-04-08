package com.h2h.test.unit;

import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.Password;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.service.api.UsersOps;
import com.h2h.test.it.BaseIntegrationTests;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UserOpsTest extends BaseIntegrationTests {
    private static final Logger log = LoggerFactory.getLogger(UserOpsTest.class);

    @Autowired
    UsersOps usersOps;

    @Test
    public void userCreate() {
        loginWithDefaultUserToken();
        Optional<UserEntity> entity = usersOps.byName(DEFAULT_USER);

        UserEntity userEntity = new UserEntity();
        Password password = new Password();
        userEntity.setUsername("test-user");
        userEntity.setRole(UserRole.USER);
        password.setUserPassword("123");
        UserEntity user = usersOps.newUser(userEntity, password);

        assertThat(user).isNotNull();

        Optional<UserEntity> byName = usersOps.byName("test-user");
        assertThat(byName).isPresent();

        byName = usersOps.byName("test-user2");
        assertThat(byName).isNotPresent();

        byName = usersOps.byName("test-user");
        assertThat(byName.get().getRole()).isNotNull();

        usersOps.remove(byName.get().getUserId());
    }
}
