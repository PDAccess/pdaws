package com.h2h.pda.jwt.providers;

import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.jwt.LoginRequest;
import com.h2h.pda.jwt.LoginTypes;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.service.api.MetricService;
import com.h2h.pda.service.api.UsersOps;
import com.h2h.pda.service.api.VaultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultToken;
import org.springframework.vault.support.VaultTokenResponse;

import java.util.Collections;
import java.util.Optional;

import static com.h2h.pda.pojo.metric.Counters.LOGIN_ATTEMPT_COUNT;

@Component("PDAwsAuthenticationProvider")
public final class PDAWSAuthenticationProvider implements AuthenticationProvider {
    private Logger log = LoggerFactory.getLogger(PDAWSAuthenticationProvider.class);

    @Autowired
    VaultService vaultService;

    @Autowired
    UsersOps usersOps;

    @Autowired
    MetricService metricService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.trace("\n---------- Authentication request ------------\n" +
                "username: {}\n" +
                "password:{}\n" +
                "---------------------------------------------", ((String) authentication.getPrincipal()).toLowerCase(), (String) authentication.getCredentials());


        VaultTemplate template = vaultService.newTemplate();
        String username = ((String) authentication.getPrincipal()).toLowerCase();
        try {
            VaultToken vault = template.doWithVault(restOperations -> {
                LoginRequest entity = new LoginRequest();
                entity.setPassword((String) authentication.getCredentials());

                ResponseEntity<VaultTokenResponse> responseEntity = restOperations.
                        postForEntity(String.format("/auth/userpass/login/%s", username), entity, VaultTokenResponse.class);
                log.info("vault response is: {} {}", responseEntity.getStatusCode(), responseEntity.getStatusCodeValue());

                return responseEntity.getBody().getToken();
            });

            Optional<UserEntity> byName = usersOps.byName(username);

            if (!byName.isPresent()) {
                log.error("User is in vault but not found in db {}", username);
                throw new AuthenticationServiceException("User Not Found");
            }
            UserEntity entity = byName.get();

            User user = new User(username,
                    (String) authentication.getCredentials(),
                    Collections.singletonList(new SimpleGrantedAuthority(UserRole.USER.name())));
            UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(user,
                    authentication.getCredentials(), Collections.singleton(new SimpleGrantedAuthority(LoginTypes.STANDARD.name())));
            TokenDetails td = (TokenDetails) authentication.getDetails();
            td.setToken(vault.getToken());
            td.setRole(entity.getRole());
            result.setDetails(td);

            metricService.getCounter(LOGIN_ATTEMPT_COUNT).increment(this.getClass().getAnnotation(Component.class).value());
            return result;
        } catch (VaultException ve) {
            log.error("Authentication error. Context: {} {}", username, ve.getMessage());
            throw new AuthenticationServiceException("Vault System Error", ve);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
