package com.h2h.pda.service.api;

import com.h2h.pda.entity.TenantEntity;

import java.util.Optional;

public interface TenantService {
    Optional<TenantEntity> getTenantById(String tenantId);
}
