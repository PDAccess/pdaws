package com.h2h.pda.pojo.policy;

import com.h2h.pda.entity.PolicyRegexEntity;
import com.h2h.pda.entity.PolicyUserEntity;
import com.h2h.pda.entity.SudoPolicyEntity;
import com.h2h.pda.pojo.EntityToDTO;
import com.h2h.pda.pojo.UserDTO;
import com.h2h.pda.pojo.group.GroupsEntityWrapper;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SudoPolicyEntityWrapper implements EntityToDTO<SudoPolicyEntityWrapper, SudoPolicyEntity> {

    private String id;
    private String name;

    private UserDTO whoCreate;
    private GroupsEntityWrapper group;

    private Set<String> policyRegexEntity;

    private Set<String> policyUserEntity;

    private Timestamp createdAt;

    private String behavior;

    private String runAsUser;

    public SudoPolicyEntityWrapper() {
    }

    public String getId() {
        return id;
    }

    public SudoPolicyEntityWrapper setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public SudoPolicyEntityWrapper setName(String name) {
        this.name = name;
        return this;
    }

    public UserDTO getWhoCreate() {
        return whoCreate;
    }

    public SudoPolicyEntityWrapper setWhoCreate(UserDTO whoCreate) {
        this.whoCreate = whoCreate;
        return this;
    }

    public GroupsEntityWrapper getGroup() {
        return group;
    }

    public SudoPolicyEntityWrapper setGroup(GroupsEntityWrapper group) {
        this.group = group;
        return this;
    }

    public Set<String> getPolicyRegexEntity() {
        return policyRegexEntity;
    }

    public SudoPolicyEntityWrapper setPolicyRegexEntity(Set<String> policyRegexEntity) {
        this.policyRegexEntity = policyRegexEntity;
        return this;
    }

    public Set<String> getPolicyUserEntity() {
        return policyUserEntity;
    }

    public SudoPolicyEntityWrapper setPolicyUserEntity(Set<String> policyUserEntity) {
        this.policyUserEntity = policyUserEntity;
        return this;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public SudoPolicyEntityWrapper setCreatedAt(Timestamp createdAt) {
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
    public SudoPolicyEntityWrapper wrap(SudoPolicyEntity entity) {
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
        setRunAsUser(entity.getSudoRunAsUser());
        return this;
    }

    @Override
    public SudoPolicyEntity unWrap() {
        SudoPolicyEntity entity = new SudoPolicyEntity();
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
