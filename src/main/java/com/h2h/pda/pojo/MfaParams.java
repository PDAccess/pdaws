package com.h2h.pda.pojo;

import com.h2h.pda.entity.UserEntity;

public class MfaParams {

    private UserDTO userEntity;
    private String mfaCode;
    private String ipAddress;

    public MfaParams() {}

    public MfaParams(UserEntity userEntity, String mfaCode, String ipAddress) {
        this.userEntity = new UserDTO(userEntity);
        this.mfaCode = mfaCode;
        this.ipAddress = ipAddress;
    }

    public UserDTO getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserDTO userEntity) {
        this.userEntity = userEntity;
    }

    public String getMfaCode() {
        return mfaCode;
    }

    public void setMfaCode(String mfaCode) {
        this.mfaCode = mfaCode;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
