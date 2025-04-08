package com.h2h.pda.pojo;

import java.util.List;

public class MaintenanceEditParams {
    String groupId;
    List<String> userIds;
    DateRange oldDateRange;
    DateRange newDateRange;

    String username;

    public MaintenanceEditParams() {
        // Constructor
    }

    public DateRange getOldDateRange() {
        return oldDateRange;
    }

    public void setOldDateRange(DateRange oldDateRange) {
        this.oldDateRange = oldDateRange;
    }

    public DateRange getNewDateRange() {
        return newDateRange;
    }

    public void setNewDateRange(DateRange newDateRange) {
        this.newDateRange = newDateRange;
    }


    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
