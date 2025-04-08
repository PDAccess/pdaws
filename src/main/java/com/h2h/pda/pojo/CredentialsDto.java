package com.h2h.pda.pojo;

import com.h2h.pda.pojo.service.ServiceEntityWrapper;

import java.sql.Timestamp;

public class CredentialsDto {
    private int id;
    private String username;
    private ServiceEntityWrapper service;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean isAdmin;

    public CredentialsDto(int id, String username, ServiceEntityWrapper service, Timestamp createdAt, Timestamp updatedAt, Boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.service = service;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isAdmin = isAdmin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ServiceEntityWrapper getService() {
        return service;
    }

    public void setService(ServiceEntityWrapper service) {
        this.service = service;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
