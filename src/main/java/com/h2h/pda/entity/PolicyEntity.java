package com.h2h.pda.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "policy")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "policy_type",
        discriminatorType = DiscriminatorType.INTEGER)
public class PolicyEntity extends BaseEntity {

    @Id
    private String id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "who_create", referencedColumnName = "user_id")
    @Fetch(FetchMode.JOIN)
    private UserEntity whoCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupid", referencedColumnName = "groupid")
    @Fetch(FetchMode.JOIN)
    private GroupsEntity group;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "policyregex", joinColumns = @JoinColumn(name = "policyid"))
    private Set<PolicyRegexEntity> policyRegexEntity;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "policyuser", joinColumns = @JoinColumn(name = "policyid"))
    private Set<PolicyUserEntity> policyUserEntity;

    public PolicyEntity() {
        // Constructor
    }

    public String getId() {
        return id;
    }

    public PolicyEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PolicyEntity setName(String name) {
        this.name = name;
        return this;
    }

    public UserEntity getWhoCreate() {
        return whoCreate;
    }

    public PolicyEntity setWhoCreate(UserEntity whoCreate) {
        this.whoCreate = whoCreate;
        return this;
    }

    public Set<PolicyRegexEntity> getPolicyRegexEntity() {
        return policyRegexEntity;
    }

    public PolicyEntity setPolicyRegexEntity(Set<PolicyRegexEntity> policyRegexEntity) {
        this.policyRegexEntity = policyRegexEntity;
        return this;
    }

    public Set<PolicyUserEntity> getPolicyUserEntity() {
        return policyUserEntity;
    }

    public PolicyEntity setPolicyUserEntity(Set<PolicyUserEntity> policyUserEntity) {
        this.policyUserEntity = policyUserEntity;
        return this;
    }

    public GroupsEntity getGroup() {
        return group;
    }

    public PolicyEntity setGroup(GroupsEntity group) {
        this.group = group;
        return this;
    }

    public boolean hasUser(String userId) {
        return policyUserEntity.contains(new PolicyUserEntity().setUserId(userId));
    }
}
