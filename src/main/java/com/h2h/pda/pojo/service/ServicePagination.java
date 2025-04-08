package com.h2h.pda.pojo.service;

import com.h2h.pda.pojo.Pagination;

public class ServicePagination extends Pagination {
    Boolean activeAgent;

    public ServicePagination() {
    }

    public ServicePagination(int i, int maxValue, String s) {
        super(i, maxValue, s);
    }

    public Boolean getActiveAgent() {
        return activeAgent;
    }

    public ServicePagination setActiveAgent(Boolean activeAgent) {
        this.activeAgent = activeAgent;
        return this;
    }
}
