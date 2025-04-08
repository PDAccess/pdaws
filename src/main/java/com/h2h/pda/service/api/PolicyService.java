package com.h2h.pda.service.api;

import com.h2h.pda.entity.PolicyEntity;

import java.util.List;
import java.util.Optional;

public interface PolicyService<T extends PolicyEntity> {

    enum PolicyType {
        PROXY, SUDO;
    }

    Optional<T> findPolicy(String id);

    List<T> userEffectivePolicyOn(String userId, String serviceId);

    List<PolicyEntity> groupPolicies(String groupId, PolicyType type);

    List<PolicyEntity> servicePolicies(String groupId, PolicyType type);

    List<String> regexSet(String groupId);

    String newPolicy(T t);

    String updatePolicy(T t);

    void removePolicy(String id);
}
