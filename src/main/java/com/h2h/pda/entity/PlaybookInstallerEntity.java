package com.h2h.pda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "ansible_installers")
public class PlaybookInstallerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ansibleInstallerSequenceGenerator")
    @SequenceGenerator(name = "ansibleInstallerSequenceGenerator", sequenceName = "ansible_installers_table_sequence", initialValue = 1, allocationSize = 1)
    @Column(name = "id")
    private int id;

    private String name;
    private String description;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ansible_installer_service",
            joinColumns = @JoinColumn(name = "installer_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<ServiceEntity> serviceEntities;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "installerEntity")
    @Fetch(FetchMode.JOIN)
    private Set<PlaybookHistoryEntity> historyEntities;

    @Column(name = "yml_content")
    private String ymlContent;

    private Boolean status;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "process_id")
    private Integer processId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "is_private")
    private Boolean isPrivate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Set<ServiceEntity> getServiceEntities() {
        return serviceEntities;
    }

    public void setServiceEntities(Set<ServiceEntity> serviceEntities) {
        this.serviceEntities = serviceEntities;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public String getYmlContent() {
        return ymlContent;
    }

    public void setYmlContent(String ymlContent) {
        this.ymlContent = ymlContent;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public Set<PlaybookHistoryEntity> getHistoryEntities() {
        return historyEntities;
    }

    public void setHistoryEntities(Set<PlaybookHistoryEntity> historyEntities) {
        this.historyEntities = historyEntities;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    @Override
    public String toString() {
        return "AnsibleInstallerEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", historyEntities=" + historyEntities +
                ", ymlContent='" + ymlContent + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", processId=" + processId +
                ", userId='" + userId + '\'' +
                ", isPrivate=" + isPrivate +
                '}';
    }
}
