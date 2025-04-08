package com.h2h.pda.pojo;

import com.h2h.pda.entity.SnippetEntity;

// @TODO: Entity Fix
public class SnippetUsername {
    private SnippetEntity snippetEntity;
    private String username;
    private String firstName;
    private String lastName;

    public SnippetUsername(SnippetEntity snippetEntity, String username) {
        this.snippetEntity = snippetEntity;
        this.username = username;
    }

    public SnippetUsername() {
    }

    public SnippetUsername(SnippetEntity snippetEntity, String username, String firstName, String lastName) {
        this.snippetEntity = snippetEntity;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public SnippetEntity getSnippetEntity() {
        return snippetEntity;
    }

    public void setSnippetEntity(SnippetEntity snippetEntity) {
        this.snippetEntity = snippetEntity;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
