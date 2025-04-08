package com.h2h.pda.pojo;

public class ActionPaginationParams extends Pagination {
    private DateRange dateRange;

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }
}
