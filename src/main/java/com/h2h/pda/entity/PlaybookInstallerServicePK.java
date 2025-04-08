package com.h2h.pda.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PlaybookInstallerServicePK implements Serializable {

    @Column(name = "installer_id")
    private int installerId;

    @Column(name = "service_id")
    private String serviceId;

    public int getInstallerId() {
        return installerId;
    }

    public void setInstallerId(int installerId) {
        this.installerId = installerId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaybookInstallerServicePK that = (PlaybookInstallerServicePK) o;
        return installerId == that.installerId && Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(installerId, serviceId);
    }

    @Override
    public String toString() {
        return "AnsibleInstallerServicePK{" +
                "installerId=" + installerId +
                ", serviceId='" + serviceId + '\'' +
                '}';
    }
}
