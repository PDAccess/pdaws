package com.h2h.pda.service.api;

import com.h2h.pda.entity.AlarmEntity;
import com.h2h.pda.entity.AlarmHistoryEntity;
import com.h2h.pda.pojo.ActionPayload;
import com.h2h.pda.pojo.Pagination;

import java.util.List;
import java.util.Optional;

public interface AlarmService {
    void pushAction(ActionPayload actionPayload);

    Optional<AlarmEntity> byId(Integer alarmId);

    List<AlarmEntity> byServiceId(String serviceId);

    List<AlarmEntity> byGroupId(String groupId);

    long getAlarmCount();

    List<AlarmHistoryEntity> getAlarmHistories(Pagination pagination);
}
