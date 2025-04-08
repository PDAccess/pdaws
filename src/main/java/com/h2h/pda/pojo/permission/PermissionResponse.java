package com.h2h.pda.pojo.permission;

import java.io.Serializable;

public class PermissionResponse implements Serializable {
    String permissionId;
    String title;
    String description;

    public String getPermissionId() {
        return permissionId;
    }

    public PermissionResponse setPermissionId(String permissionId) {
        this.permissionId = permissionId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public PermissionResponse setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PermissionResponse setDescription(String description) {
        this.description = description;
        return this;
    }
}
