package com.h2h.pda.pojo.vault;

import com.h2h.pda.entity.CredentialRequestEntity;
import com.h2h.pda.pojo.CredentialDetails;
import com.h2h.pda.pojo.EntityToDTO;
import com.h2h.pda.pojo.UserDTO;

import java.sql.Timestamp;

public class CredentialRequestParams implements EntityToDTO<CredentialRequestParams, CredentialRequestEntity> {

    private int id;
    private CredentialDetails credentials;
    private UserDTO requestingUser;
    private UserDTO respondingUser;
    private boolean isApproval;
    private Timestamp requestedAt;
    private Timestamp respondedAt;

    @Override
    public CredentialRequestParams wrap(CredentialRequestEntity entity) {
        this.setId(entity.getId());
        this.setCredentials(new CredentialDetails().wrap(entity.getCredentialEntity()));
        this.setRequestingUser(new UserDTO().wrap(entity.getRequestingUser()));
        this.setRespondingUser(new UserDTO().wrap(entity.getRespondingUser()));
        this.setRequestedAt(entity.getRequestedAt());
        this.setRespondedAt(entity.getRespondedAt());
        this.setApproval(entity.isApproval());
        return this;
    }

    @Override
    public CredentialRequestEntity unWrap() {
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public UserDTO getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(UserDTO requestingUser) {
        this.requestingUser = requestingUser;
    }

    public UserDTO getRespondingUser() {
        return respondingUser;
    }

    public void setRespondingUser(UserDTO respondingUser) {
        this.respondingUser = respondingUser;
    }

    public boolean isApproval() {
        return isApproval;
    }

    public void setApproval(boolean approval) {
        isApproval = approval;
    }

    public Timestamp getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Timestamp requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Timestamp getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(Timestamp respondedAt) {
        this.respondedAt = respondedAt;
    }

    public CredentialDetails getCredentials() {
        return credentials;
    }

    public void setCredentials(CredentialDetails credentials) {
        this.credentials = credentials;
    }
}
