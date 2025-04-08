package com.h2h.pda.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Table;
import java.util.Objects;

@Table(name = "policyuser")
@Embeddable
public class PolicyUserEntity {

    @JsonProperty("userid")
    @Column(name = "userid")
    private String userId;

    public PolicyUserEntity() {
    }

    public PolicyUserEntity(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public PolicyUserEntity setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolicyUserEntity that = (PolicyUserEntity) o;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}