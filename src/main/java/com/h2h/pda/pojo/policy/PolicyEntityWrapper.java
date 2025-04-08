package com.h2h.pda.pojo.policy;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.EntityToDTO;
import com.h2h.pda.pojo.UserDTO;
import com.h2h.pda.pojo.group.GroupsEntityWrapper;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PolicyEntityWrapper implements EntityToDTO<PolicyEntityWrapper, PolicyEntity> {

    private String id;
    private String name;

    private UserDTO whoCreate;
    private GroupsEntityWrapper group;

    private Set<String> policyRegexEntity;

    private Set<String> policyUserEntity;

    private Timestamp createdAt;

    private String behavior;

    private String runAsUser;

    public PolicyEntityWrapper() {
    }

    public String getId() {
        return id;
    }

    public PolicyEntityWrapper setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PolicyEntityWrapper setName(String name) {
        this.name = name;
        return this;
    }

    public UserDTO getWhoCreate() {
        return whoCreate;
    }

    public PolicyEntityWrapper setWhoCreate(UserDTO whoCreate) {
        this.whoCreate = whoCreate;
        return this;
    }

    public GroupsEntityWrapper getGroup() {
        return group;
    }

    public PolicyEntityWrapper setGroup(GroupsEntityWrapper group) {
        this.group = group;
        return this;
    }

    public Set<String> getPolicyRegexEntity() {
        return policyRegexEntity;
    }

    public PolicyEntityWrapper setPolicyRegexEntity(Set<String> policyRegexEntity) {
        this.policyRegexEntity = policyRegexEntity;
        return this;
    }

    public Set<String> getPolicyUserEntity() {
        return policyUserEntity;
    }

    public PolicyEntityWrapper setPolicyUserEntity(Set<String> policyUserEntity) {
        this.policyUserEntity = policyUserEntity;
        return this;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public PolicyEntityWrapper setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    @Override
    public PolicyEntityWrapper wrap(PolicyEntity entity) {
        Objects.requireNonNull(entity);
        setId(entity.getId());
        setName(entity.getName());
        if (entity.getGroup() != null)
            setGroup(new GroupsEntityWrapper().wrap(entity.getGroup()));
        setWhoCreate(new UserDTO().wrap(entity.getWhoCreate()));

        if (entity.getPolicyRegexEntity() != null)
            setPolicyRegexEntity(entity.getPolicyRegexEntity().stream().map(PolicyRegexEntity::getRegex).collect(Collectors.toSet()));
        if (entity.getPolicyUserEntity() != null)
            setPolicyUserEntity(entity.getPolicyUserEntity().stream().map(PolicyUserEntity::getUserId).collect(Collectors.toSet()));

        setCreatedAt(entity.getCreatedAt());
        if (entity instanceof ProxyPolicyEntity) {
            ProxyPolicyEntity proxyPolicyEntity = (ProxyPolicyEntity) entity;
            if (proxyPolicyEntity.getBehavior() != null) setBehavior(proxyPolicyEntity.getBehavior().name());
        } else if (entity instanceof SudoPolicyEntity) {
            SudoPolicyEntity sudoPolicyEntity = (SudoPolicyEntity) entity;
            if (sudoPolicyEntity.getBehavior() != null) setBehavior(sudoPolicyEntity.getBehavior().name());
        }
        return this;
    }

    @Override
    public PolicyEntity unWrap() {
        PolicyEntity entity = new PolicyEntity();
        entity.setId(getId());
        entity.setName(getName());
        entity.setGroup(getGroup().unWrap());
        entity.setWhoCreate(getWhoCreate().unWrap());
        entity.setPolicyRegexEntity(getPolicyRegexEntity().stream().map(PolicyRegexEntity::new).collect(Collectors.toSet()));
        entity.setPolicyUserEntity(getPolicyUserEntity().stream().map(PolicyUserEntity::new).collect(Collectors.toSet()));
        entity.setCreatedAt(getCreatedAt());
        return entity;
    }

    public String getRunAsUser() {
        return runAsUser;
    }

    public void setRunAsUser(String runAsUser) {
        this.runAsUser = runAsUser;
    }
}
