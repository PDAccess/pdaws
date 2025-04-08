package com.h2h.pda.pojo;

import java.util.List;

public class SearchParams2 {
    private List<String> userids;
    private List<String> serviceids;
    private DateRange dateRange;
    private Pagination pagination;

    public List<String> getUserids() {
        return userids;
    }

    public void setUserids(List<String> userids) {
        this.userids = userids;
    }

    public List<String> getServiceids() {
        return serviceids;
    }

    public void setServiceids(List<String> serviceids) {
        this.serviceids = serviceids;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
