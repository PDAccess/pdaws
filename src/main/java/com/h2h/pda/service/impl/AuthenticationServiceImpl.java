package com.h2h.pda.service.impl;

import com.h2h.pda.entity.AuthenticationAttemptEntity;
import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.jwt.providers.InternalAuthenticationToken;
import com.h2h.pda.pojo.auth.LoginType;
import com.h2h.pda.repository.AuthenticationAttemptRepository;
import com.h2h.pda.service.api.AuthenticationService;
import com.h2h.pda.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Autowired
    AuthenticationAttemptRepository authenticationAttemptRepository;

    @Override
    public void saveAuthenticationAttempt(HttpServletRequest request, String username, LoginType type, String reason, Boolean isSuccess) {

        AuthenticationAttemptEntity authenticationAttemptEntity = new AuthenticationAttemptEntity();
        authenticationAttemptEntity.setUsername(username);
        authenticationAttemptEntity.setLoginType(type);
        authenticationAttemptEntity.setIpAddress(RequestUtil.remoteAddress(request));
        authenticationAttemptEntity.setUserAgent(RequestUtil.userAgent(request));
        authenticationAttemptEntity.setReason(reason);
        authenticationAttemptEntity.setSuccess(isSuccess);
        authenticationAttemptEntity.setAttemptedAt(Timestamp.from(Instant.now()));
        authenticationAttemptRepository.save(authenticationAttemptEntity);
    }

    @Override
    public AuthenticationAttemptEntity saveAuthenticationAttempt(AuthenticationAttemptEntity authenticationAttemptEntity) {
        return authenticationAttemptRepository.save(authenticationAttemptEntity);
    }

    @Override
    public Page<AuthenticationAttemptEntity> byUserName(String username, String filter, PageRequest req) {
        Specification<AuthenticationAttemptEntity> where = Specification.where(AuthenticationAttemptRepository.QueryFilter.findByAuthByUser(username));
        if (StringUtils.hasText(filter)) {
            where.or(AuthenticationAttemptRepository.QueryFilter.findByAuthFilterByHost(filter))
                    .or(AuthenticationAttemptRepository.QueryFilter.findByAuthFilterByUserAgent(filter));
        }

        return authenticationAttemptRepository.findAll(where, req);
    }

    @Override
    public Page<AuthenticationAttemptEntity> byUserService(String userId, String filter, PageRequest req) {
        Specification<AuthenticationAttemptEntity> where = Specification.where(AuthenticationAttemptRepository.QueryFilter.serviceFilterByMember(userId));
        if (StringUtils.hasText(filter)) {
            where.or(AuthenticationAttemptRepository.QueryFilter.findByAuthFilterByHost(filter))
                    .or(AuthenticationAttemptRepository.QueryFilter.findByAuthFilterByUser(filter))
                    .or(AuthenticationAttemptRepository.QueryFilter.findByAuthFilterByUserAgent(filter));
        }

        return authenticationAttemptRepository.findAll(where, req);
    }

    @Override
    public Page<AuthenticationAttemptEntity> byService(String serviceId, String filter, PageRequest req) {
        Specification<AuthenticationAttemptEntity> where = Specification.where(AuthenticationAttemptRepository.QueryFilter.serviceFilter(serviceId));
        if (StringUtils.hasText(filter)) {
            where.or(AuthenticationAttemptRepository.QueryFilter.findByAuthFilterByHost(filter))
                    .or(AuthenticationAttemptRepository.QueryFilter.findByAuthFilterByUser(filter))
                    .or(AuthenticationAttemptRepository.QueryFilter.findByAuthFilterByUserAgent(filter));
        }

        return authenticationAttemptRepository.findAll(where, req);
    }

    @Override
    public Page<AuthenticationAttemptEntity> byServices(List<String> serviceIds, String filter, PageRequest req) {
        Specification<AuthenticationAttemptEntity> where = Specification.where(AuthenticationAttemptRepository.QueryFilter.servicesFilter(serviceIds));
        if (StringUtils.hasText(filter)) {
            where.or(AuthenticationAttemptRepository.QueryFilter.findByAuthFilterByHost(filter))
                    .or(AuthenticationAttemptRepository.QueryFilter.findByAuthFilterByUser(filter))
                    .or(AuthenticationAttemptRepository.QueryFilter.findByAuthFilterByUserAgent(filter));
        }

        return authenticationAttemptRepository.findAll(where, req);
    }

    @Override
    public Page<AuthenticationAttemptEntity> history(String filter, PageRequest req) {
        Specification<AuthenticationAttemptEntity> where = Specification.where(AuthenticationAttemptRepository.QueryFilter.findByNullService());
        if (StringUtils.hasText(filter)) {
            where = Specification.where(AuthenticationAttemptRepository.QueryFilter.findByAuthFilterByHost(filter))
                    .or(AuthenticationAttemptRepository.QueryFilter.findByAuthFilterByUser(filter))
                    .or(AuthenticationAttemptRepository.QueryFilter.findByAuthFilterByUserAgent(filter));
        }

        return authenticationAttemptRepository.findAll(where, req);
    }

    @Override
    public void logout() {
        // TODO: Added session end logic;
    }

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        log.info("Authentication success {}", authentication.getPrincipal());
        TokenDetails tokenDetails = (TokenDetails) authentication.getDetails();

        AuthenticationAttemptEntity authenticationAttemptEntity = new AuthenticationAttemptEntity();
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            authenticationAttemptEntity.setUsername(user.getUsername());
        } else {
            authenticationAttemptEntity.setUsername((String) authentication.getPrincipal());
        }
        authenticationAttemptEntity.setLoginType(LoginType.NORMAL);
        authenticationAttemptEntity.setIpAddress(tokenDetails.getRemoteAddress());
        authenticationAttemptEntity.setUserAgent(tokenDetails.getUserAgent());
        authenticationAttemptEntity.setServiceId(tokenDetails.getServiceId());
        authenticationAttemptEntity.setReason(tokenDetails.getAuthType() + ":" + "Authentication Success");
        authenticationAttemptEntity.setSuccess(true);
        if (authentication instanceof InternalAuthenticationToken) {
            InternalAuthenticationToken iat = (InternalAuthenticationToken) authentication;
            if (iat.getServiceId() != null)
                authenticationAttemptEntity.setService(new ServiceEntity().setInventoryId(iat.getServiceId()));
        }
        authenticationAttemptEntity.setAttemptedAt(Timestamp.from(Instant.now()));
        authenticationAttemptRepository.save(authenticationAttemptEntity);
    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        log.info("Authentication failed {} {}", exception.getMessage(), authentication.getPrincipal());
        TokenDetails tokenDetails = (TokenDetails) authentication.getDetails();

        AuthenticationAttemptEntity authenticationAttemptEntity = new AuthenticationAttemptEntity();
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            authenticationAttemptEntity.setUsername(user.getUsername());
        } else {
            authenticationAttemptEntity.setUsername((String) authentication.getPrincipal());
        }
        authenticationAttemptEntity.setLoginType(LoginType.NORMAL);
        authenticationAttemptEntity.setIpAddress(tokenDetails.getRemoteAddress());
        authenticationAttemptEntity.setUserAgent(tokenDetails.getUserAgent());
        authenticationAttemptEntity.setServiceId(tokenDetails.getServiceId());
        authenticationAttemptEntity.setReason(tokenDetails.getAuthType() + ":" + exception.getMessage());
        authenticationAttemptEntity.setSuccess(false);
        if (authentication instanceof InternalAuthenticationToken) {
            InternalAuthenticationToken iat = (InternalAuthenticationToken) authentication;
            if (iat.getServiceId() != null)
                authenticationAttemptEntity.setService(new ServiceEntity().setInventoryId(iat.getServiceId()));
        }
        authenticationAttemptEntity.setAttemptedAt(Timestamp.from(Instant.now()));
        authenticationAttemptRepository.save(authenticationAttemptEntity);
    }
}
