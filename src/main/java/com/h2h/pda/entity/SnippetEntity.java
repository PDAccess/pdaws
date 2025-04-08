package com.h2h.pda.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "snippets")
public class SnippetEntity extends DeletableBaseEntity {

    @Id
    @Column(name = "snippet_id")
    private String snippetId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "operating_system_id")
    private int operatingSystemId;

    @Column(name = "service_type_id")
    private int serviceTypeId;

    @Column(name = "description")
    private String description;

    @Column(name = "title")
    private String title;

    @Column(name = "info")
    private String info;



    public SnippetEntity() {
    }

    public SnippetEntity(String userId, String snippetId) {
        this.userId = userId;
        this.snippetId = snippetId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSnippetId() {
        return snippetId;
    }

    public void setSnippetId(String snippetId) {
        this.snippetId = snippetId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getOperatingSystemId() {
        return operatingSystemId;
    }

    public void setOperatingSystemId(int operatingSystemId) {
        this.operatingSystemId = operatingSystemId;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }
}
