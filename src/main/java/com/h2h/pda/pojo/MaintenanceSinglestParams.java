package com.h2h.pda.pojo;

public class MaintenanceSinglestParams {
    String groupId;
    String userId;
    DateRange dateRange;

    public MaintenanceSinglestParams(String groupId, String userId, DateRange dateRange) {
        this.groupId = groupId;
        this.userId = userId;
        this.dateRange = dateRange;
    }

    public MaintenanceSinglestParams(String groupId, String userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public MaintenanceSinglestParams(String groupId) {
        this.groupId = groupId;
    }

    public MaintenanceSinglestParams() {
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
