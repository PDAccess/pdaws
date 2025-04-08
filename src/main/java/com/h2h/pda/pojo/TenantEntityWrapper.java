package com.h2h.pda.pojo;

import com.h2h.pda.entity.TenantEntity;

import java.sql.Timestamp;
import java.util.List;

public class TenantEntityWrapper {
    private String tenantId;
    private String companyName;
    private String country;
    private Timestamp deletedAt;
    private Timestamp updatedAt;
    private Timestamp createdAt;
    private List<UserDTO> users;

    public TenantEntityWrapper(TenantEntity tenantEntity) {
        setTenantId(tenantEntity.getTenantId());
        setCompanyName(tenantEntity.getCompanyName());
        setCountry(tenantEntity.getCountry());
        setUpdatedAt(tenantEntity.getUpdatedAt());
        setCreatedAt(tenantEntity.getCreatedAt());
        setDeletedAt(tenantEntity.getDeletedAt());
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

    public String getTenantId() {
        return tenantId;
    }

    public TenantEntityWrapper setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public TenantEntityWrapper setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public TenantEntityWrapper setCountry(String country) {
        this.country = country;
        return this;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public TenantEntityWrapper setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public TenantEntityWrapper setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public TenantEntityWrapper setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}
