package com.h2h.pda.pojo.service;

import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.pojo.EntityToDTO;

import java.sql.Timestamp;
import java.util.Objects;

public class ServiceEntityWrapper implements EntityToDTO<ServiceEntityWrapper, ServiceEntity> {

    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
    private String inventoryId;
    private String name;
    private String description;
    private ServiceOs operatingSystemId;
    private ServiceType serviceTypeId;
    private String whoCreate;
    private Boolean videoRecord;
    private String whoUpdate;
    private Timestamp lastAccessTime;
    private String originId;
    private String ipAddress;
    private Integer port;
    private String dbName;
    private String path;
    private String ldapDn;
    private String syncMethod;
    private Double mapx;
    private Double mapy;
    private String credantial;
    private boolean hasAgent;
    private String serviceNameLogo;
    private String serviceTypeLogo;
    private String serviceUser;
    private String sessioncount;
    private Timestamp lastSessionStart;
    private Timestamp lastSessionEnd;
    private String serviceType;
    private ServiceCounter serviceCounters;
    private String operatingSystemVersion;
    private boolean isAdmin;

    public ServiceEntityWrapper(ServiceEntity serviceEntity) {
        this.wrap(serviceEntity);
    }

    public ServiceEntityWrapper() {
    }

    public String getSessioncount() {
        return sessioncount;
    }

    public void setSessioncount(String sessioncount) {
        this.sessioncount = sessioncount;
    }

    public String getServiceNameLogo() {
        return serviceNameLogo;
    }

    public void setServiceNameLogo(String serviceNameLogo) {
        this.serviceNameLogo = serviceNameLogo;
    }

    public String getServiceTypeLogo() {
        return serviceTypeLogo;
    }

    public void setServiceTypeLogo(String serviceTypeLogo) {
        this.serviceTypeLogo = serviceTypeLogo;
    }

    public String getServiceUser() {
        return serviceUser;
    }

    public void setServiceUser(String serviceUser) {
        this.serviceUser = serviceUser;
    }

    public Timestamp getLastSessionStart() {
        return lastSessionStart;
    }

    public void setLastSessionStart(Timestamp lastSessionStart) {
        this.lastSessionStart = lastSessionStart;
    }

    public Timestamp getLastSessionEnd() {
        return lastSessionEnd;
    }

    public void setLastSessionEnd(Timestamp lastSessionEnd) {
        this.lastSessionEnd = lastSessionEnd;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceEntityWrapper)) return false;
        if (!super.equals(o)) return false;
        ServiceEntityWrapper that = (ServiceEntityWrapper) o;
        return Objects.equals(serviceNameLogo, that.serviceNameLogo) && Objects.equals(serviceTypeLogo, that.serviceTypeLogo) && Objects.equals(serviceUser, that.serviceUser) && Objects.equals(sessioncount, that.sessioncount) && Objects.equals(lastSessionStart, that.lastSessionStart) && Objects.equals(lastSessionEnd, that.lastSessionEnd) && Objects.equals(serviceType, that.serviceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), serviceNameLogo, serviceTypeLogo, serviceUser, sessioncount, lastSessionStart, lastSessionEnd, serviceType);
    }

    @Override
    public ServiceEntityWrapper wrap(ServiceEntity serviceEntity) {
        if (serviceEntity == null) {
            return null;
        }

        serviceNameLogo = serviceEntity.getOperatingSystemId().getLogoName();
        serviceTypeLogo = serviceEntity.getServiceTypeId().getTypeName();

        setName(serviceEntity.getName());
        setCreatedAt(serviceEntity.getCreatedAt());
        setDeletedAt(serviceEntity.getDeletedAt());
        setInventoryId(serviceEntity.getInventoryId());
        setOperatingSystemId(serviceEntity.getOperatingSystemId());
        setServiceTypeId(serviceEntity.getServiceTypeId());
        setUpdatedAt(serviceEntity.getUpdatedAt());
        setWhoCreate(serviceEntity.getWhoCreate());
        setWhoUpdate(serviceEntity.getWhoUpdate());
        setDescription(serviceEntity.getDescription());
        setMapx(serviceEntity.getMapx());
        setMapy(serviceEntity.getMapy());
        setServiceNameLogo(serviceNameLogo);
        setServiceTypeLogo(serviceTypeLogo);
        setCredantial(serviceEntity.getCredantial());
        setLastAccessTime(serviceEntity.getLastAccessTime());
        setVideoRecord(serviceEntity.getVideoRecord());
        setOriginId(serviceEntity.getOriginId());
        setIpAddress(serviceEntity.getIpAddress());
        setPort(serviceEntity.getPort());
        setPath(serviceEntity.getPath());
        setHasAgent(serviceEntity.isHasAgent());
        setOperatingSystemVersion(serviceEntity.getOperatingSystemVersion());
        return this;
    }

    @Override
    public ServiceEntity unWrap() {
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setCreatedAt(createdAt);
        serviceEntity.setUpdatedAt(updatedAt);
        serviceEntity.setDeletedAt(deletedAt);
        serviceEntity.setInventoryId(inventoryId);
        serviceEntity.setName(name);
        serviceEntity.setDescription(description);
        serviceEntity.setOperatingSystemId(operatingSystemId);
        serviceEntity.setServiceTypeId(serviceTypeId);
        serviceEntity.setWhoCreate(whoCreate);
        serviceEntity.setVideoRecord(videoRecord);
        serviceEntity.setWhoUpdate(whoUpdate);
        serviceEntity.setLastAccessTime(lastAccessTime);
        serviceEntity.setOriginId(originId);
        serviceEntity.setIpAddress(ipAddress);
        serviceEntity.setPort(port);
        serviceEntity.setDbName(dbName);
        serviceEntity.setPath(path);
        serviceEntity.setLdapDn(ldapDn);
        serviceEntity.setSyncMethod(syncMethod);
        serviceEntity.setMapx(mapx);
        serviceEntity.setMapy(mapy);
        serviceEntity.setCredantial(credantial);
        serviceEntity.setHasAgent(hasAgent);
        return serviceEntity;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public ServiceEntityWrapper setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public ServiceEntityWrapper setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public ServiceEntityWrapper setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public ServiceEntityWrapper setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ServiceEntityWrapper setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ServiceEntityWrapper setDescription(String description) {
        this.description = description;
        return this;
    }

    public ServiceOs getOperatingSystemId() {
        return operatingSystemId;
    }

    public ServiceEntityWrapper setOperatingSystemId(ServiceOs operatingSystemId) {
        this.operatingSystemId = operatingSystemId;
        return this;
    }

    public ServiceType getServiceTypeId() {
        return serviceTypeId;
    }

    public ServiceEntityWrapper setServiceTypeId(ServiceType serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
        return this;
    }

    public String getWhoCreate() {
        return whoCreate;
    }

    public ServiceEntityWrapper setWhoCreate(String whoCreate) {
        this.whoCreate = whoCreate;
        return this;
    }

    public Boolean getVideoRecord() {
        return videoRecord;
    }

    public ServiceEntityWrapper setVideoRecord(Boolean videoRecord) {
        this.videoRecord = videoRecord;
        return this;
    }

    public String getWhoUpdate() {
        return whoUpdate;
    }

    public ServiceEntityWrapper setWhoUpdate(String whoUpdate) {
        this.whoUpdate = whoUpdate;
        return this;
    }

    public Timestamp getLastAccessTime() {
        return lastAccessTime;
    }

    public ServiceEntityWrapper setLastAccessTime(Timestamp lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
        return this;
    }

    public String getOriginId() {
        return originId;
    }

    public ServiceEntityWrapper setOriginId(String originId) {
        this.originId = originId;
        return this;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public ServiceEntityWrapper setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public ServiceEntityWrapper setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getDbName() {
        return dbName;
    }

    public ServiceEntityWrapper setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public String getPath() {
        return path;
    }

    public ServiceEntityWrapper setPath(String path) {
        this.path = path;
        return this;
    }

    public String getLdapDn() {
        return ldapDn;
    }

    public ServiceEntityWrapper setLdapDn(String ldapDn) {
        this.ldapDn = ldapDn;
        return this;
    }

    public String getSyncMethod() {
        return syncMethod;
    }

    public ServiceEntityWrapper setSyncMethod(String syncMethod) {
        this.syncMethod = syncMethod;
        return this;
    }

    public Double getMapx() {
        return mapx;
    }

    public ServiceEntityWrapper setMapx(Double mapx) {
        this.mapx = mapx;
        return this;
    }

    public Double getMapy() {
        return mapy;
    }

    public ServiceEntityWrapper setMapy(Double mapy) {
        this.mapy = mapy;
        return this;
    }

    public String getCredantial() {
        return credantial;
    }

    public ServiceEntityWrapper setCredantial(String credantial) {
        this.credantial = credantial;
        return this;
    }

    public boolean isHasAgent() {
        return hasAgent;
    }

    public ServiceEntityWrapper setHasAgent(boolean hasAgent) {
        this.hasAgent = hasAgent;
        return this;
    }

    public ServiceCounter getServiceCounters() {
        return serviceCounters;
    }

    public ServiceEntityWrapper setServiceCounters(ServiceCounter serviceCounters) {
        this.serviceCounters = serviceCounters;
        return this;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getOperatingSystemVersion() {
        return operatingSystemVersion;
    }

    public void setOperatingSystemVersion(String operatingSystemVersion) {
        this.operatingSystemVersion = operatingSystemVersion;
    }
}
