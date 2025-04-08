package com.h2h.pda.pojo;

import java.util.List;

public class MaintenanceParams {
    String groupId;
    List<String> userIds;
    DateRange dateRange;

    String username;

    public MaintenanceParams() {
    }

    public MaintenanceParams(String groupId) {
        this.groupId = groupId;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    @Override
    public String toString() {
        return "MaintenanceParams{" +
                "groupId='" + groupId + '\'' +
                ", userIds=" + userIds +
                ", dateRange=" + dateRange +
                ", username='" + username + '\'' +
                '}';
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
