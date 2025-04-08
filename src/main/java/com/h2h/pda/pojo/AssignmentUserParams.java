package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.h2h.pda.entity.UserEntity;

import java.util.List;

public class AssignmentUserParams {

    @JsonProperty("users")
    private List<UserEntity> userEntities;

    public List<UserEntity> getUserEntities() {
        return userEntities;
    }

    public void setUserEntities(List<UserEntity> userEntities) {
        this.userEntities = userEntities;
    }
}
