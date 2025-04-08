package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.h2h.pda.api.vault.ShareController;
import com.h2h.pda.entity.BreakTheGlassShareEntity;

import java.sql.Timestamp;
import java.util.List;

public class BreakTheGlassShareParams implements EntityToDTO<BreakTheGlassShareParams, BreakTheGlassShareEntity> {

    @JsonProperty("id")
    private String id;

    @JsonProperty("credential_id")
    private String credentialId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("share_link")
    private String shareLink;

    @JsonProperty("expired_at")
    private Timestamp expiredAt;

    @JsonProperty("allow_ip")
    private String allowIpAddress;

    @JsonProperty("users")
    private List<UserDTO> users;

    private UserParams user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public Timestamp getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Timestamp expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public UserParams getUser() {
        return user;
    }

    public void setUser(UserParams user) {
        this.user = user;
    }

    public String getAllowIpAddress() {
        return allowIpAddress;
    }

    public void setAllowIpAddress(String allowIpAddress) {
        this.allowIpAddress = allowIpAddress;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

    @Override
    public BreakTheGlassShareParams wrap(BreakTheGlassShareEntity entity) {
        if (entity != null) {
            setId(entity.getId());
            setCredentialId(entity.getCredentialEntity().getCredentialId());
            setDescription(entity.getDescription());
            setShareLink(String.format(ShareController.SHARE_LINK, entity.getId()));
            setExpiredAt(entity.getExpiredAt());
            setUser(new UserParams().wrap(entity.getUserEntity()));
            return this;
        }
        return null;
    }

    @Override
    public BreakTheGlassShareEntity unWrap() {
        return null;
    }
}
