package com.h2h.pda.pojo.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.h2h.pda.pojo.Credential;

import java.util.List;

public class ServiceCreateParams {
    ServiceEntityWrapper serviceEntity;
    private String ipaddress;
    private int port;
    private String dbname;
    private String path;
    List<Credential> vaults;
    String groupid;
    Credential admin;

    @JsonProperty("connection_user")
    String connectionUser;

    @JsonProperty("sync_method")
    String syncMethod;

    @JsonProperty("sync_data")
    Object syncData;

    public ServiceEntityWrapper getServiceEntity() {
        return serviceEntity;
    }

    public void setServiceEntity(ServiceEntityWrapper serviceEntity) {
        this.serviceEntity = serviceEntity;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public List<Credential> getVaults() {
        return vaults;
    }

    public void setVaults(List<Credential> vaults) {
        this.vaults = vaults;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getConnectionUser() {
        return connectionUser;
    }

    public void setConnectionUser(String connectionUser) {
        this.connectionUser = connectionUser;
    }

    public Credential getAdmin() {
        return admin;
    }

    public void setAdmin(Credential admin) {
        this.admin = admin;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSyncMethod() {
        return syncMethod;
    }

    public void setSyncMethod(String syncMethod) {
        this.syncMethod = syncMethod;
    }


    public Object getSyncData() {
        return syncData;
    }

    public void setSyncData(Object syncData) {
        this.syncData = syncData;
    }
}
