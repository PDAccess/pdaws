package com.h2h.pda.jwt.providers;

import com.h2h.pda.entity.AuthenticationAttemptEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.jwt.LoginRequest;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.pojo.auth.LoginType;
import com.h2h.pda.pojo.system.SystemSettingTags;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.service.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultToken;
import org.springframework.vault.support.VaultTokenResponse;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static com.h2h.pda.pojo.metric.Counters.*;

@Component("InternalAuthenticationProvider")
public final class InternalAuthenticationProvider implements AuthenticationProvider {
    private Logger log = LoggerFactory.getLogger(InternalAuthenticationProvider.class);

    @Autowired
    VaultService vaultService;

    @Autowired
    MetricService metricService;

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    UsersOps usersOps;

    @Autowired
    SystemSettings systemSettings;

    @Autowired
    AuthenticationService authenticationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        InternalAuthenticationToken iat = (InternalAuthenticationToken) authentication;

        log.trace("\n---------- Authentication request ------------\n" +
                "username: {}\n" +
                "password:{}\n" +
                "---------------------------------------------", ((String) authentication.getPrincipal()).toLowerCase(), authentication.getCredentials());


        VaultTemplate template = vaultService.newTemplate();
        String username = ((String) authentication.getPrincipal()).toLowerCase();
        VaultToken vault = null;
        Exception lastException = null;
        metricService.getCounter(LOGIN_ATTEMPT_COUNT).increment(this.getClass().getAnnotation(Component.class).value());

        Optional<UserEntity> optionalUserEntity = usersOps.byName(username);
        if (!optionalUserEntity.isPresent()) {
            throw new AuthenticationServiceException("Authentication Failed", lastException);
        }

        UserEntity userEntity = optionalUserEntity.get();
        boolean hasServiceMembership = serviceOps.isMembership(iat.getServiceId(), userEntity.getUserId());
        boolean canConnectToDevice = userEntity.getRole().equals(UserRole.USER) || (userEntity.getRole().equals(UserRole.ADMIN) && !systemSettings.checkTagValue(SystemSettingTags.NO_LOGIN_TO_DEVICE_FROM_ADMIN_USERS, "true"));
        if (hasServiceMembership && canConnectToDevice) {
            UserEntity ue = optionalUserEntity.get();

            try {
                if (ue.isExternal()) {
                    if (ue.getRole() == UserRole.USER) {
                        vault = template.doWithVault(restOperations -> {
                            LoginRequest entity = new LoginRequest();
                            entity.setPassword((String) authentication.getCredentials());

                            ResponseEntity<VaultTokenResponse> responseEntity = restOperations.
                                    postForEntity(String.format("/auth/ldap/login/%s", username), entity, VaultTokenResponse.class);
                            log.info("vault response for normal users is: {} {}", responseEntity.getStatusCode(), responseEntity.getStatusCodeValue());

                            return responseEntity.getBody().getToken();
                        });
                    } else {
                        vault = template.doWithVault(restOperations -> {
                            LoginRequest entity = new LoginRequest();
                            entity.setPassword((String) authentication.getCredentials());

                            ResponseEntity<VaultTokenResponse> responseEntity = restOperations.
                                    postForEntity(String.format("/auth/admin/login/%s", username), entity, VaultTokenResponse.class);
                            log.info("vault response for admin users is: {} {}", responseEntity.getStatusCode(), responseEntity.getStatusCodeValue());

                            return responseEntity.getBody().getToken();
                        });
                    }
                } else {
                    if (ue.getRole() == UserRole.USER) {
                        vault = template.doWithVault(restOperations -> {
                            LoginRequest entity = new LoginRequest();
                            entity.setPassword((String) authentication.getCredentials());

                            ResponseEntity<VaultTokenResponse> responseEntity = restOperations.
                                    postForEntity(String.format("/auth/userpass/login/%s", username), entity, VaultTokenResponse.class);
                            log.info("vault response is: {} {}", responseEntity.getStatusCode(), responseEntity.getStatusCodeValue());

                            return responseEntity.getBody().getToken();
                        });
                    } else {
                        vault = template.doWithVault(restOperations -> {
                            LoginRequest entity = new LoginRequest();
                            entity.setPassword((String) authentication.getCredentials());

                            ResponseEntity<VaultTokenResponse> responseEntity = restOperations.
                                    postForEntity(String.format("/auth/adminpass/login/%s", username), entity, VaultTokenResponse.class);
                            log.info("vault response is: {} {}", responseEntity.getStatusCode(), responseEntity.getStatusCodeValue());

                            return responseEntity.getBody().getToken();
                        });
                    }
                }
            } catch (VaultException ve) {
                lastException = ve;
                metricService.getCounter(LOGIN_FAIL_COUNT).increment(this.getClass().getAnnotation(Component.class).value());
            }

            if (vault != null) {
                metricService.getCounter(LOGIN_SUCCESS_COUNT).increment(this.getClass().getAnnotation(Component.class).value());
                User user = new User(username,
                        (String) authentication.getCredentials(),
                        Collections.singletonList(new SimpleGrantedAuthority(UserRole.USER.name())));
                InternalAuthenticationToken result = new InternalAuthenticationToken(user,
                        authentication.getCredentials(), iat.getServiceId());

                TokenDetails td = (TokenDetails) authentication.getDetails();
                td.setToken(vault.getToken());
                td.setRole(optionalUserEntity.get().getRole());
                result.setDetails(td);

                return result;
            }
        }

        throw new AuthenticationServiceException("Authentication Failed", lastException);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return InternalAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
