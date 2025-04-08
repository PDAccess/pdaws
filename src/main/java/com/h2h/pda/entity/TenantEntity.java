package com.h2h.pda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tenant")
public class TenantEntity extends DeletableBaseEntity {
    public static final String DEFAULT_TENANT = "123";

    @Id
    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "company_name")
    private String companyName;
    private String country;

    public TenantEntity() {
        // Constructor
    }

    public String getTenantId() {
        return tenantId;
    }

    public TenantEntity setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
