package com.h2h.pda.service.api;

import com.h2h.pda.entity.MfaEntity;
import com.h2h.pda.entity.UserEntity;

import java.util.Optional;

public interface MfaService {
    String QUEUE = "mfa_send_queue";

    void sendMfaCode(UserEntity userEntity, String ipAddress);

    boolean checkMfaCode(String username, String mfaCode);

    String generateMfaCode(String username);

    void storeToken(String username, String token);

    Optional<MfaEntity> getMfaStatusByUsername(String username);
}
