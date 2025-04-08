package com.h2h.pda.jwt;

import com.h2h.pda.pojo.user.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class SecurityService {
    private static Logger log = LoggerFactory.getLogger(SecurityService.class);

    public boolean hasAdmin(Authentication authentication) {
        if (authentication.getDetails() instanceof TokenDetails) {
            TokenDetails tokenDetails = (TokenDetails) authentication.getDetails();
            UserRole role = tokenDetails.getRole();
            log.trace("Current User Role {}", role.getName());

            return role == UserRole.ADMIN;
        } else {
            log.warn("Token details is different {}", authentication.getDetails().getClass());
            return false;
        }
    }
}