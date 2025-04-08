package com.h2h.pda.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ansible_installer_service")
public class PlaybookInstallerServiceEntity {

    @EmbeddedId
    private PlaybookInstallerServicePK id;

    public PlaybookInstallerServicePK getId() {
        return id;
    }

    public void setId(PlaybookInstallerServicePK id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AnsibleInstallerServiceEntity{" +
                "id=" + id +
                '}';
    }
}
