package com.h2h.pda.pojo;

import com.h2h.pda.entity.UserEntity;

public class UserParams implements EntityToDTO<UserParams, UserEntity> {

    private String id;
    private String username;
    private String firstName;
    private String lastName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    @Override
    public UserParams wrap(UserEntity entity) {
        setId(entity.getUserId());
        setUsername(entity.getUsername());
        setFirstName(entity.getFirstName());
        setLastName(entity.getLastName());
        return this;
    }

    @Override
    public UserEntity unWrap() {
        return null;
    }
}
