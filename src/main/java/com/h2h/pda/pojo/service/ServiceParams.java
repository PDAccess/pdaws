package com.h2h.pda.pojo.service;

import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.pojo.EntityToDTO;

public class ServiceParams implements EntityToDTO<ServiceParams, ServiceEntity> {

    private String id;
    private String name;
    private String description;
    private String serviceNameLogo;
    private String serviceTypeLogo;
    private String ipAddress;
    private Integer port;
    private ServiceType serviceTypeId;
    private String originId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public ServiceType getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(ServiceType serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    @Override
    public ServiceParams wrap(ServiceEntity entity) {
        setId(entity.getInventoryId());
        setDescription(entity.getDescription());
        setName(entity.getName());
        setServiceNameLogo(entity.getOperatingSystemId().getLogoName());
        setServiceTypeLogo(entity.getServiceTypeId().getTypeName());
        setIpAddress(entity.getIpAddress());
        setPort(entity.getPort());
        setOriginId(entity.getOriginId());
        setServiceTypeId(entity.getServiceTypeId());

        return this;
    }

    @Override
    public ServiceEntity unWrap() {
        return null;
    }
}
