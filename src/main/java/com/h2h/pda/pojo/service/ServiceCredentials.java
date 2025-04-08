package com.h2h.pda.pojo.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.h2h.pda.pojo.Credential;

import java.util.List;

public class ServiceCredentials {

    @JsonProperty("admin")
    private Credential adminCredential;

    @JsonProperty("add_users")
    private List<Credential> addedUsers;

    @JsonProperty("edit_users")
    private List<Credential> editedUsers;

    @JsonProperty("delete_users")
    private List<Credential> deletedUsers;

    public Credential getAdminCredential() {
        return adminCredential;
    }

    public void setAdminCredential(Credential adminCredential) {
        this.adminCredential = adminCredential;
    }

    public List<Credential> getAddedUsers() {
        return addedUsers;
    }

    public void setAddedUsers(List<Credential> addedUsers) {
        this.addedUsers = addedUsers;
    }

    public List<Credential> getEditedUsers() {
        return editedUsers;
    }

    public void setEditedUsers(List<Credential> editedUsers) {
        this.editedUsers = editedUsers;
    }

    public List<Credential> getDeletedUsers() {
        return deletedUsers;
    }

    public void setDeletedUsers(List<Credential> deletedUsers) {
        this.deletedUsers = deletedUsers;
    }
}
