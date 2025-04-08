package com.h2h.pda.pojo.group;

import com.h2h.pda.pojo.service.ServiceEntityWrapper;

import java.util.List;

public class GroupServices {
    private List<ServiceEntityWrapper> services;
    private int totalRows;
    private int totalService;

    public GroupServices() {
    }

    public GroupServices(List<ServiceEntityWrapper> services, int totalRows, int totalService) {
        this.services = services;
        this.totalRows = totalRows;
        this.totalService = totalService;
    }

    public List<ServiceEntityWrapper> getServices() {
        return services;
    }

    public void setServices(List<ServiceEntityWrapper> services) {
        this.services = services;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getTotalService() {
        return totalService;
    }

    public void setTotalService(int totalService) {
        this.totalService = totalService;
    }
}
