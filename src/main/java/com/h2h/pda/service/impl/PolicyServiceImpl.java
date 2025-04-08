package com.h2h.pda.service.impl;

import com.h2h.pda.entity.*;
import com.h2h.pda.repository.PolicyRepository;
import com.h2h.pda.service.api.PolicyService;
import com.h2h.pda.service.api.UsersOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

public abstract class PolicyServiceImpl<T extends PolicyEntity> implements PolicyService<T> {

    @Autowired
    PolicyRepository policyRepository;

    @Autowired
    UsersOps usersOps;

    @Override
    public List<T> userEffectivePolicyOn(String userId, String serviceId) {
        List<PolicyEntity> entities = policyRepository.policyUserFind(userId, Pageable.unpaged());

        return Collections.emptyList();
    }

    @Override
    public List<PolicyEntity> groupPolicies(String groupId, PolicyType type) {
        List<PolicyEntity> policyList = (List<PolicyEntity>) policyRepository.groupPolicies(groupId);
        List<PolicyEntity> sendPolList = new ArrayList<>();
        for (PolicyEntity item : policyList) {
            if ((PolicyType.PROXY == type && item instanceof ProxyPolicyEntity) || (PolicyType.SUDO == type && item instanceof SudoPolicyEntity)) {
                for (PolicyUserEntity policyUserEntity : item.getPolicyUserEntity()) {
                    Optional<UserEntity> entity = usersOps.byId(policyUserEntity.getUserId());
                    entity.ifPresent(userEntity -> policyUserEntity.setUserId(userEntity.getUsername()));
                }

                sendPolList.add(item);
            }
        }

        return sendPolList;
    }

    @Override
    public List<PolicyEntity> servicePolicies(String groupId, PolicyType type) {
        return null;
    }

    @Override
    public List<String> regexSet(String groupId) {
        return null;
    }

    @Override
    public String newPolicy(T policyEntity) {
        policyEntity.setId(UUID.randomUUID().toString());
        policyEntity.setCreatedAt(Timestamp.from(Instant.now()));
        policyEntity.setUpdatedAt(Timestamp.from(Instant.now()));
        PolicyEntity save = policyRepository.save(policyEntity);
        return save.getId();
    }

    @Override
    public String updatePolicy(T policyEntity) {
        policyEntity.setUpdatedAt(Timestamp.from(Instant.now()));
        PolicyEntity save = policyRepository.save(policyEntity);
        return save.getId();
    }

    @Override
    public void removePolicy(String id) {
        policyRepository.deleteById(id);
    }

    @Override
    public Optional<T> findPolicy(String id) {
        return (Optional<T>) policyRepository.findById(id);
    }
}
