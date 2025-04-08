package com.h2h.pda.pojo;

import com.h2h.pda.entity.ServiceEntity;

import java.util.Set;

// @TODO: Entity Fix
public class PlaybookInstallerWrapper {

    private String name;
    private String description;
    private Set<ServiceEntity> services;
    private String ymlContent;
    private String userId;
    private Boolean isPrivate;

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

    public Set<ServiceEntity> getServices() {
        return services;
    }

    public void setServices(Set<ServiceEntity> services) {
        this.services = services;
    }

    public String getYmlContent() {
        return ymlContent;
    }

    public void setYmlContent(String ymlContent) {
        this.ymlContent = ymlContent;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    @Override
    public String toString() {
        return "AnsibleInstallerWrapper{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", services=" + services +
                ", ymlContent='" + ymlContent + '\'' +
                ", userId='" + userId + '\'' +
                ", is_private=" + isPrivate +
                '}';
    }
}
