package com.h2h.pda.service.api;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = IService.class)
public interface IService {
    String getInventoryId();

    String getName();

    String getDescription();

    String getIpAddress();
}
