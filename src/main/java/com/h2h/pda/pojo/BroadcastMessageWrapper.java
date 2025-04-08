package com.h2h.pda.pojo;

import com.h2h.pda.entity.BroadcastMessageEntity;

public class BroadcastMessageWrapper implements EntityToDTO<BroadcastMessageWrapper, BroadcastMessageEntity> {

    private String messageid;
    private String message;
    private String fontColor;
    private String backgroundColor;
    private String fontsize;
    private String startDate;
    private String endDate;
    private Boolean isDeleted;
    private DateRange dateRange;

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getFontsize() {
        return fontsize;
    }

    public void setFontsize(String fontsize) {
        this.fontsize = fontsize;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public BroadcastMessageWrapper wrap(BroadcastMessageEntity m) {
        setMessageid(m.getMessageId());
        setMessage(m.getMessage());
        setBackgroundColor(m.getBackgroundColor());
        setFontsize(m.getFontSize());
        setFontColor(m.getFontColor());
        setIsDeleted(m.getIsDeleted());
        return this;
    }

    @Override
    public BroadcastMessageEntity unWrap() {
        return null;
    }
}
