package com.h2h.pda.pojo;

public class DeleteMaintenanceParams {

    String groupId;
    DateRange dateRange;

    public DeleteMaintenanceParams(String groupId, DateRange dateRange) {
        this.groupId = groupId;
        this.dateRange = dateRange;
    }

    public DeleteMaintenanceParams() {
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
