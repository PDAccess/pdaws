package com.h2h.pda.entity;

import javax.persistence.*;

@Entity
@Table(name = "exec_file_filters")
public class ExecFileFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "execFileFilterSequenceGenerator")
    @SequenceGenerator(name = "execFileFilterSequenceGenerator", sequenceName = "exec_file_filters_table_sequence", initialValue = 1, allocationSize = 1)
    private int id;

    private String name;
    private String description;
    private String users;
    private String paths;

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

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
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
