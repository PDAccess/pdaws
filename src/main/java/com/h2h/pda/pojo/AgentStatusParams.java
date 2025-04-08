package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AgentStatusParams {

    @JsonProperty("service_id")
    private String serviceId;

    @JsonProperty("group_id")
    private String groupId;

    @JsonProperty("hostname")
    private String hostname;

    @JsonProperty("statistics_data")
    private String statisticsData;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getStatisticsData() {
        return statisticsData;
    }

    public void setStatisticsData(String statisticsData) {
        this.statisticsData = statisticsData;
    }


}
