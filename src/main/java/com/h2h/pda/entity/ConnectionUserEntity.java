package com.h2h.pda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "connection_users")
public class ConnectionUserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "connectionUserSequenceGenerator")
    @SequenceGenerator(name = "connectionUserSequenceGenerator", sequenceName = "connection_users_table_sequence", initialValue = 1, allocationSize = 1)
    private int id;

    private String username;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", referencedColumnName = "inventory_id")
    private ServiceEntity serviceEntity;

    @Column(name = "is_admin")
    @Deprecated
    private Boolean isAdmin;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "connectionUser")
    private List<CredentialEntity> credentialEntities;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ServiceEntity getServiceEntity() {
        return serviceEntity;
    }

    public void setServiceEntity(ServiceEntity serviceEntity) {
        this.serviceEntity = serviceEntity;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public List<CredentialEntity> getCredentialEntities() {
        return credentialEntities;
    }

    public void setCredentialEntities(List<CredentialEntity> credentialEntities) {
        this.credentialEntities = credentialEntities;
    }
}
