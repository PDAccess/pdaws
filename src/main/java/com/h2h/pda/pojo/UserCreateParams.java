package com.h2h.pda.pojo;

import com.h2h.pda.entity.UserEntity;

import java.util.List;

public class UserCreateParams {
    private UserDTO userEntity;
    private Password password;
    private List<String> ipAddress;

    public UserCreateParams() {
    }

    public UserCreateParams(UserEntity userEntity, Password password) {
        this.userEntity = new UserDTO(userEntity);
        this.password = password;
    }

    public UserCreateParams(UserEntity userEntity, Password password, List<String> ipAddress) {
        this.userEntity = new UserDTO(userEntity);
        this.password = password;
        this.ipAddress = ipAddress;
    }

    public UserDTO getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserDTO userEntity) {
        this.userEntity = userEntity;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public List<String> getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(List<String> ipAddress) {
        this.ipAddress = ipAddress;
    }
}
