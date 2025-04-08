package com.h2h.pda.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "maintenance")
public class MaintenanceEntity {

    @Id
    private String uniqueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "groupid")
    private GroupsEntity groupsEntity;

    private String userId;

    private Timestamp startDate;

    private Timestamp endDate;

    public MaintenanceEntity() {
    }

    public MaintenanceEntity(String uniqueId, String groupId, String userId) {
        this.uniqueId = uniqueId;
        this.groupsEntity = new GroupsEntity().setGroupId(groupId);
        this.userId = userId;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public MaintenanceEntity setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public GroupsEntity getGroupsEntity() {
        return groupsEntity;
    }

    public MaintenanceEntity setGroupsEntity(GroupsEntity groupsEntity) {
        this.groupsEntity = groupsEntity;
        return this;
    }
}
