package com.h2h.pda.entity;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class WhoCreateEntity extends DeletableBaseEntity {
    @ManyToOne
    @JoinColumn(name = "who_create")
    private UserEntity whoCreate;

    public UserEntity getWhoCreate() {
        return whoCreate;
    }

    public WhoCreateEntity setWhoCreate(UserEntity whoCreate) {
        this.whoCreate = whoCreate;
        return this;
    }
}
