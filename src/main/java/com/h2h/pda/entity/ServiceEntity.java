package com.h2h.pda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.h2h.pda.pojo.service.ServiceOs;
import com.h2h.pda.pojo.service.ServiceType;
import com.h2h.pda.service.api.IService;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "inventory")
public class ServiceEntity extends DeletableBaseEntity implements IService, Serializable {
    @Id
    @JsonProperty("inventory_id")
    @Column(name = "inventory_id")
    private String inventoryId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @JsonProperty("operating_system_id")
    @Column(name = "operating_system_id")
    private ServiceOs operatingSystemId;

    @JsonProperty("operating_system_version")
    @Column(name = "operating_system_version")
    private String operatingSystemVersion;

    @JsonProperty("service_type_id")
    @Column(name = "service_type_id")
    private ServiceType serviceTypeId;

    @JsonProperty("who_create")
    @Column(name = "who_create")
    private String whoCreate;

    @JsonProperty("video_record")
    @Column(name = "video_record")
    @Deprecated
    private Boolean videoRecord;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "serviceid")
    private Set<GroupServiceEntity> memberOf;

    @JsonIgnore
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "service_properties", joinColumns = @JoinColumn(name = "service_id"))
    private Set<ServiceProperty> properties;

    @JsonProperty("who_update")
    private String whoUpdate;

    @Column(name = "last_access_time")
    @Deprecated
    private Timestamp lastAccessTime;

    @Column(name = "origin_id")
    private String originId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "port")
    private Integer port;

    @Column(name = "db_name")
    @Deprecated
    private String dbName;

    @Deprecated
    private String path;

    @JsonIgnore
    @ManyToMany(mappedBy = "serviceEntities")
    @Deprecated
    private Set<PlaybookInstallerEntity> ansibleInstallerEntities;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "serviceEntity")
    private List<ConnectionUserEntity> connectionUserEntities;

    @Column(name = "ldap_dn")
    private String ldapDn;

    @Column(name = "sync_method")
    @Deprecated
    private String syncMethod;

    @Deprecated
    private Double mapx;
    @Deprecated
    private Double mapy;
    @Deprecated
    private String credantial;

    @Column(name = "has_agent")
    private boolean hasAgent;

    public ServiceEntity() {
        // Constructor
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public ServiceEntity setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServiceOs getOperatingSystemId() {
        return operatingSystemId;
    }

    public void setOperatingSystemId(ServiceOs operatingSystemId) {
        this.operatingSystemId = operatingSystemId;
    }

    public String getOperatingSystemVersion() {
        return operatingSystemVersion;
    }

    public void setOperatingSystemVersion(String operatingSystemVersion) {
        this.operatingSystemVersion = operatingSystemVersion;
    }

    public ServiceType getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(ServiceType serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getWhoCreate() {
        return whoCreate;
    }

    public void setWhoCreate(String whoCreate) {
        this.whoCreate = whoCreate;
    }

    public String getWhoUpdate() {
        return whoUpdate;
    }

    public void setWhoUpdate(String whoUpdate) {
        this.whoUpdate = whoUpdate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Deprecated
    public Double getMapx() {
        return mapx;
    }

    @Deprecated
    public void setMapx(Double mapx) {
        this.mapx = mapx;
    }

    @Deprecated
    public Double getMapy() {
        return mapy;
    }

    @Deprecated
    public void setMapy(Double mapy) {
        this.mapy = mapy;
    }

    public String getCredantial() {
        return credantial;
    }

    public void setCredantial(String credantial) {
        this.credantial = credantial;
    }

    public Timestamp getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Timestamp lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    @Deprecated
    public Boolean getVideoRecord() {
        return videoRecord;
    }

    @Deprecated
    public void setVideoRecord(Boolean videoRecord) {
        this.videoRecord = videoRecord;
    }

    public Set<ServiceProperty> getProperties() {
        return properties;
    }

    public void setProperties(Set<ServiceProperty> properties) {
        this.properties = properties;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }


    @Deprecated
    public Set<PlaybookInstallerEntity> getAnsibleInstallerEntities() {
        return ansibleInstallerEntities;
    }

    @Deprecated
    public void setAnsibleInstallerEntities(Set<PlaybookInstallerEntity> ansibleInstallerEntities) {
        this.ansibleInstallerEntities = ansibleInstallerEntities;
    }

    public List<ConnectionUserEntity> getConnectionUserEntities() {
        return connectionUserEntities;
    }

    public void setConnectionUserEntities(List<ConnectionUserEntity> connectionUserEntities) {
        this.connectionUserEntities = connectionUserEntities;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLdapDn() {
        return ldapDn;
    }

    public void setLdapDn(String ldapDn) {
        this.ldapDn = ldapDn;
    }

    public String getSyncMethod() {
        return syncMethod;
    }

    public void setSyncMethod(String syncMethod) {
        this.syncMethod = syncMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceEntity that = (ServiceEntity) o;
        return inventoryId.equals(that.inventoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventoryId);
    }

    public boolean isHasAgent() {
        return hasAgent;
    }

    public void setHasAgent(boolean hasAgent) {
        this.hasAgent = hasAgent;
    }

    public Set<GroupServiceEntity> getMemberOf() {
        return memberOf;
    }

    public ServiceEntity setMemberOf(Set<GroupServiceEntity> memberOf) {
        this.memberOf = memberOf;
        return this;
    }
}

