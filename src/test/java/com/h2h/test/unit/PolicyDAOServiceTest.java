package com.h2h.test.unit;

import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.PolicyEntity;
import com.h2h.pda.entity.SudoPolicyEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.PolicyService;
import com.h2h.pda.service.api.SudoPolicyService;
import com.h2h.pda.service.api.UsersOps;
import com.h2h.test.it.BaseIntegrationTests;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PolicyDAOServiceTest extends BaseIntegrationTests {
    private static final Logger log = LoggerFactory.getLogger(PolicyDAOServiceTest.class);

    @Autowired
    GroupOps groupOps;

    @Autowired
    UsersOps usersOps;

    @Autowired
    SudoPolicyService policyService;

    @Test
    public void effectivePolicies() {

        Optional<UserEntity> entity = usersOps.byName(DEFAULT_USER);

        String newGroup = groupOps.newGroup(new GroupsEntity().setGroupName("test-group"), entity.get());

        int i = groupOps.addUsersTo(newGroup, Collections.singletonList(entity.get().getUserId()));

        assertThat(i).isEqualTo(1);

        SudoPolicyEntity policyEntity = new SudoPolicyEntity();
        policyEntity.setName("deneme");
        policyEntity.setGroup(new GroupsEntity().setGroupId(newGroup));
        policyEntity.setWhoCreate(entity.get());
        policyService.newPolicy(policyEntity);

        List<PolicyEntity> policyEntities = policyService.groupPolicies(newGroup, PolicyService.PolicyType.SUDO);

        assertThat(policyEntities.size()).isEqualTo(1);

        groupOps.deleteById(newGroup);
    }
}
