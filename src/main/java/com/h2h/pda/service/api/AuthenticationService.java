package com.h2h.pda.service.api;

import com.h2h.pda.entity.AuthenticationAttemptEntity;
import com.h2h.pda.pojo.auth.LoginType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationEventPublisher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AuthenticationService extends AuthenticationEventPublisher {

    void saveAuthenticationAttempt(HttpServletRequest request, String username, LoginType type, String reason, Boolean isSuccess);

    AuthenticationAttemptEntity saveAuthenticationAttempt(AuthenticationAttemptEntity authenticationAttemptEntity);

    Page<AuthenticationAttemptEntity> byUserName(String username, String filter, PageRequest req);

    Page<AuthenticationAttemptEntity> byUserService(String userId, String filter, PageRequest req);

    Page<AuthenticationAttemptEntity> byService(String serviceId, String filter, PageRequest req);

    Page<AuthenticationAttemptEntity> byServices(List<String> serviceIds, String filter, PageRequest req);

    Page<AuthenticationAttemptEntity> history(String filter, PageRequest req);

    void logout();
}
