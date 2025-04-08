package com.h2h.pda.entity;

import com.h2h.pda.pojo.group.GroupMembership;
import com.h2h.pda.pojo.group.GroupRole;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "groupuser")
public class GroupUserEntity {

    @EmbeddedId
    private GroupUserPK id;

    @ManyToOne
    @MapsId("groupid")
    @JoinColumn(name = "groupid", insertable = false, updatable = false)
    GroupsEntity group;

    @ManyToOne
    @MapsId("userid")
    @JoinColumn(name = "userid", insertable = false, updatable = false)
    UserEntity user;

    private String whoCreate;
    private String notification;
    @Column(name = "expiredate")
    private Timestamp expireDate;
    private Timestamp createdAt;

    @Column(name = "membership_type")
    @Enumerated(EnumType.STRING)
    private GroupMembership membershipType;

    @Column(name = "membership_role")
    @Enumerated(EnumType.STRING)
    private GroupRole membershipRole;

    public GroupUserEntity() {
    }

    public GroupUserEntity(GroupUserPK id, String whoCreate, Timestamp expiredate, Timestamp createdAt) {
        this.id = id;
        this.whoCreate = whoCreate;
        this.expireDate = expiredate;
        this.createdAt = createdAt;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public GroupUserPK getId() {
        return id;
    }

    public GroupUserEntity setId(GroupUserPK id) {
        this.id = id;
        return this;
    }

    public String getWhoCreate() {
        return whoCreate;
    }

    public void setWhoCreate(String whoCreate) {
        this.whoCreate = whoCreate;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public GroupUserEntity setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
        return this;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public GroupUserEntity setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public GroupMembership getMembershipType() {
        return membershipType;
    }

    public GroupUserEntity setMembershipType(GroupMembership membershipType) {
        this.membershipType = membershipType;
        return this;
    }

    public GroupsEntity getGroup() {
        return group;
    }

    public GroupUserEntity setGroup(GroupsEntity group) {
        this.group = group;
        return this;
    }

    public UserEntity getUser() {
        return user;
    }

    public GroupUserEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public GroupRole getMembershipRole() {
        return membershipRole;
    }

    public GroupUserEntity setMembershipRole(GroupRole membershipRole) {
        this.membershipRole = membershipRole;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupUserEntity that = (GroupUserEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}