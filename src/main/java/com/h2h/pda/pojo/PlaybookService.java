package com.h2h.pda.pojo;

import com.h2h.pda.entity.PlaybookInstallerEntity;
import com.h2h.pda.entity.ServiceEntity;

import java.util.List;

// @TODO: Entity Fix
public class PlaybookService {
    private PlaybookInstallerEntity playbookInstallerEntity;
    private List<ServiceEntity> serviceEntities;

    public PlaybookService(PlaybookInstallerEntity playbookInstallerEntity, List<ServiceEntity> serviceEntities) {
        this.playbookInstallerEntity = playbookInstallerEntity;
        this.serviceEntities = serviceEntities;
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
