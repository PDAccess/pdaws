package com.h2h.pda.pojo;

import java.util.List;

public class ActionResponse {
    private List<ActionWrapper> actionEntities;
    private Integer totalRows;

    public ActionResponse() {
    }

    public ActionResponse(List<ActionWrapper> actionEntities, Integer totalRows) {
        this.actionEntities = actionEntities;
        this.totalRows = totalRows;
    }

    public List<ActionWrapper> getActionEntities() {
        return actionEntities;
    }

    public void setActionEntities(List<ActionWrapper> actionEntities) {
        this.actionEntities = actionEntities;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }
}
