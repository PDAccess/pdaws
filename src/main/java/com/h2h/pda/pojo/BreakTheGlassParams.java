package com.h2h.pda.pojo;

import com.h2h.pda.entity.BreakTheGlassEntity;

import java.sql.Timestamp;

public class BreakTheGlassParams implements EntityToDTO<BreakTheGlassParams, BreakTheGlassEntity> {

    private String id;
    private UserParams user;
    private String reason;
    private Timestamp checkedTime;
    private Timestamp checkoutTime;
    private String ipAddress;
    private boolean isShare;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserParams getUser() {
        return user;
    }

    public void setUser(UserParams user) {
        this.user = user;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getCheckedTime() {
        return checkedTime;
    }

    public void setCheckedTime(Timestamp checkedTime) {
        this.checkedTime = checkedTime;
    }

    public Timestamp getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(Timestamp checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isShare() {
        return isShare;
    }

    public void setShare(boolean share) {
        isShare = share;
    }

    @Override
    public BreakTheGlassParams wrap(BreakTheGlassEntity entity) {
        if (entity != null) {
            setId(entity.getBreakId());
            setCheckedTime(entity.getCheckedTime());
            setCheckoutTime(entity.getCheckoutTime());
            setReason(entity.getReason());
            setUser(new UserParams().wrap(entity.getUserEntity()));
            setIpAddress(entity.getIpAddress());
            setShare(entity.isShare());
            return this;
        }
        return null;
    }

    @Override
    public BreakTheGlassEntity unWrap() {
        return null;
    }
}
