package com.h2h.pda.entity;

import javax.persistence.*;


@Entity
@Table(name = "favoriteservice")
public class FavoriteServiceEntity {

    @EmbeddedId
    private FavoriteServicePK id;

    @ManyToOne
    @MapsId("serviceid")
    @JoinColumn(name = "serviceid", insertable = false, updatable = false)
    ServiceEntity service;

    @ManyToOne
    @MapsId("userid")
    @JoinColumn(name = "userid", insertable = false, updatable = false)
    UserEntity user;

    public FavoriteServiceEntity() {
    }

    public FavoriteServiceEntity(FavoriteServicePK id) {
        this.id = id;
    }

    public FavoriteServicePK getId() {
        return id;
    }

    public void setId(FavoriteServicePK id) {
        this.id = id;
    }

    public ServiceEntity getService() {
        return service;
    }

    public FavoriteServiceEntity setService(ServiceEntity service) {
        this.service = service;
        return this;
    }

    public UserEntity getUser() {
        return user;
    }

    public FavoriteServiceEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }
}

