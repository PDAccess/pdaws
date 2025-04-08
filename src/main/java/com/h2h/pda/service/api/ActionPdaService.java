package com.h2h.pda.service.api;

import com.h2h.pda.pojo.ActionPdaData;

public interface ActionPdaService {
    void saveAction(ActionPdaData data);
    void saveAction(String actionPayload);
    void saveAction(String actionPayload, int sessionId);
}
