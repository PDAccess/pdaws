package com.h2h.pda.service.impl;

import com.h2h.pda.entity.TenantEntity;
import com.h2h.pda.repository.TenantRepository;
import com.h2h.pda.service.api.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    TenantRepository tenantRepository;

    @Override
    public Optional<TenantEntity> getTenantById(String tenantId) {
        return tenantRepository.findById(tenantId);
    }

}
