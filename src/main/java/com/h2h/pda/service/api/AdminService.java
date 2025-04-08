package com.h2h.pda.service.api;

import com.h2h.pda.entity.BroadcastMessageEntity;
import com.h2h.pda.pojo.StatisticsDto;

import java.util.List;

public interface AdminService {

    // Broadcast Messages
    BroadcastMessageEntity newBroadcastMessage(BroadcastMessageEntity messageEntity);

    List<BroadcastMessageEntity> activeMessages();

    List<BroadcastMessageEntity> allMessages();

    void deleteMessage(String id);

    // statistics
    StatisticsDto statistics();

    //
}
