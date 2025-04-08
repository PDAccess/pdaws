package com.h2h.pda.entity;

import javax.persistence.*;

@Entity
@Table(name = "exec_trace_filters")
public class ExecTraceFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "execTraceFilterSequenceGenerator")
    @SequenceGenerator(name = "execTraceFilterSequenceGenerator", sequenceName = "exec_trace_filters_table_sequence", initialValue = 1, allocationSize = 1)
    private int id;

    private String name;
    private String description;
    private String users;
    private String regexes;

    @Column(name = "service_id")
    @Deprecated
    private String serviceId;

    @Column(name = "group_id")
    private String groupId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getRegexes() {
        return regexes;
    }

    public void setRegexes(String regexes) {
        this.regexes = regexes;
    }

    @Deprecated
    public String getServiceId() {
        return serviceId;
    }

    @Deprecated
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
