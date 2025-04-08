package com.h2h.pda.pojo;

import com.h2h.pda.pojo.group.GroupsEntityWrapper;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;

import java.util.List;

public class StatisticsDto {
    private long totalServices;
    private long totalGroups;
    private long totalActions;
    private long totalVault;
    private long totalTerminalServices;
    private long totalDatabaseServices;
    private long totalOnlineSession;
    private long totalPostgresql;
    private long totalMssql;
    private long totalMysql;
    private long totalOracle;
    private long totalSsh;
    private long totalRdp;
    private long totalVnc;
    private long totalTelnet;
    private List<ServiceEntityWrapper> mostActiveServices;
    private List<ServiceEntityWrapper> latestServices;
    private List<GroupsEntityWrapper> latestGroups;

    public StatisticsDto() {
    }

    public List<GroupsEntityWrapper> getLatestGroups() {
        return latestGroups;
    }

    public void setLatestGroups(List<GroupsEntityWrapper> latestGroups) {
        this.latestGroups = latestGroups;
    }

    public long getTotalTelnet() {
        return totalTelnet;
    }

    public void setTotalTelnet(long totalTelnet) {
        this.totalTelnet = totalTelnet;
    }

    public long getTotalMysql() {
        return totalMysql;
    }

    public void setTotalMysql(long totalMysql) {
        this.totalMysql = totalMysql;
    }

    public long getTotalServices() {
        return totalServices;
    }

    public void setTotalServices(long totalServices) {
        this.totalServices = totalServices;
    }

    public long getTotalGroups() {
        return totalGroups;
    }

    public void setTotalGroups(long totalGroups) {
        this.totalGroups = totalGroups;
    }

    public long getTotalActions() {
        return totalActions;
    }

    public void setTotalActions(long totalActions) {
        this.totalActions = totalActions;
    }

    public long getTotalTerminalServices() {
        return totalTerminalServices;
    }

    public void setTotalTerminalServices(long totalTerminalServices) {
        this.totalTerminalServices = totalTerminalServices;
    }

    public long getTotalDatabaseServices() {
        return totalDatabaseServices;
    }

    public void setTotalDatabaseServices(long totalDatabaseServices) {
        this.totalDatabaseServices = totalDatabaseServices;
    }

    public long getTotalOnlineSession() {
        return totalOnlineSession;
    }

    public void setTotalOnlineSession(long totalOnlineSession) {
        this.totalOnlineSession = totalOnlineSession;
    }

    public List<ServiceEntityWrapper> getMostActiveServices() {
        return mostActiveServices;
    }

    public void setMostActiveServices(List<ServiceEntityWrapper> mostActiveServices) {
        this.mostActiveServices = mostActiveServices;
    }

    public List<ServiceEntityWrapper> getLatestServices() {
        return latestServices;
    }

    public void setLatestServices(List<ServiceEntityWrapper> latestServices) {
        this.latestServices = latestServices;
    }

    public long getTotalPostgresql() {
        return totalPostgresql;
    }

    public void setTotalPostgresql(long totalPostgresql) {
        this.totalPostgresql = totalPostgresql;
    }

    public long getTotalMssql() {
        return totalMssql;
    }

    public void setTotalMssql(long totalMssql) {
        this.totalMssql = totalMssql;
    }

    public long getTotalOracle() {
        return totalOracle;
    }

    public void setTotalOracle(long totalOracle) {
        this.totalOracle = totalOracle;
    }

    public long getTotalSsh() {
        return totalSsh;
    }

    public void setTotalSsh(long totalSsh) {
        this.totalSsh = totalSsh;
    }

    public long getTotalRdp() {
        return totalRdp;
    }

    public void setTotalRdp(long totalRdp) {
        this.totalRdp = totalRdp;
    }

    public long getTotalVnc() {
        return totalVnc;
    }

    public void setTotalVnc(long totalVnc) {
        this.totalVnc = totalVnc;
    }

    public long getTotalVault() {
        return totalVault;
    }

    public StatisticsDto setTotalVault(long totalVault) {
        this.totalVault = totalVault;
        return this;
    }
}
