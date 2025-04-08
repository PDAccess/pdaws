package com.h2h.pda.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "profile_images")
public class ProfileImageEntity {

    @Id
    @Column(name = "user_id")
    private String id;

    @Column(name = "image")
    private String image;

    @Column(name = "changed_at")
    private String changedAt;

    public ProfileImageEntity() {
    }

    public ProfileImageEntity(String id, String image) {
        this.id = id;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(String changedAt) {
        this.changedAt = changedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}