package com.h2h.pda.entity;

import javax.persistence.*;

@Entity
@Table(name = "agent_status")
public class AgentStatusEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agentStatusSequenceGenerator")
    @SequenceGenerator(name = "agentStatusSequenceGenerator", sequenceName = "agent_status_table_sequence", initialValue = 1, allocationSize = 1)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", referencedColumnName = "inventory_id")
    private ServiceEntity service;

    @Column(name = "hostname")
    private String hostname;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "statistics_data")
    private String statisticsData;

    public ServiceEntity getService() {
        return service;
    }

    public AgentStatusEntity setService(ServiceEntity service) {
        this.service = service;
        return this;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getStatisticsData() {
        return statisticsData;
    }

    public void setStatisticsData(String statisticsData) {
        this.statisticsData = statisticsData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
