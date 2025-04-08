package com.h2h.pda.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FavoriteServicePK implements Serializable {
    @Column(name = "userid")
    private String userId;
    @Column(name = "serviceid")
    private String serviceId;

    public FavoriteServicePK() {
    }

    public FavoriteServicePK(String userId, String serviceId) {
        this.userId = userId;
        this.serviceId = serviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        FavoriteServicePK that = (FavoriteServicePK) o;
        return Objects.equals(userId, that.userId) && Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, serviceId);
    }
}
