package com.h2h.pda.pojo;

import com.h2h.pda.entity.PlaybookInstallerEntity;
import com.h2h.pda.entity.ServiceEntity;

import java.util.List;

// @TODO: Entity Fix
public class PlaybookUsername {
    private PlaybookInstallerEntity playbookInstallerEntity;
    private List<ServiceEntity> serviceEntities;
    private String username;
    private String firstName;
    private String lastName;

    public PlaybookUsername(PlaybookInstallerEntity playbookInstallerEntity, String username) {
        this.playbookInstallerEntity = playbookInstallerEntity;
        this.username = username;
    }

    public PlaybookUsername() {
    }

    public PlaybookUsername(PlaybookInstallerEntity playbookInstallerEntity, String username, String firstName, String lastName) {
        this.playbookInstallerEntity = playbookInstallerEntity;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public PlaybookUsername(PlaybookInstallerEntity playbookInstallerEntity, List<ServiceEntity> serviceEntities, String username, String firstName, String lastName) {
        this.playbookInstallerEntity = playbookInstallerEntity;
        this.serviceEntities = serviceEntities;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PlaybookInstallerEntity getAnsibleInstallerEntity() {
        return playbookInstallerEntity;
    }

    public void setAnsibleInstallerEntity(PlaybookInstallerEntity playbookInstallerEntity) {
        this.playbookInstallerEntity = playbookInstallerEntity;
    }

    public List<ServiceEntity> getServiceEntities() {
        return serviceEntities;
    }

    public void setServiceEntities(List<ServiceEntity> serviceEntities) {
        this.serviceEntities = serviceEntities;
    }
}
