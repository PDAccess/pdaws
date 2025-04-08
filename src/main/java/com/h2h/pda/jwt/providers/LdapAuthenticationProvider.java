package com.h2h.pda.jwt.providers;

import com.h2h.pda.config.LdapTemplateWrapper;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.jwt.LoginRequest;
import com.h2h.pda.jwt.LoginTypes;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.map.UserContextMapper;
import com.h2h.pda.pojo.ldap.LdapCommonAttributes;
import com.h2h.pda.pojo.ldap.LdapUser;
import com.h2h.pda.pojo.ldap.LdapUserAttributes;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.repository.SystemTokenRepository;
import com.h2h.pda.service.api.MetricService;
import com.h2h.pda.service.api.UsersOps;
import com.h2h.pda.service.api.VaultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.query.SearchScope;
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
import java.util.List;
import java.util.Optional;

import static com.h2h.pda.pojo.metric.Counters.LOGIN_ATTEMPT_COUNT;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Component("LDAPAuthenticationProvider")
public final class LdapAuthenticationProvider implements AuthenticationProvider {
    private Logger log = LoggerFactory.getLogger(LdapAuthenticationProvider.class);

    @Autowired
    VaultService vaultService;

    @Autowired
    MetricService metricService;

    @Autowired
    UsersOps usersOps;

    @Autowired
    LdapTemplateWrapper ldapTemplateWrapper;

    @Autowired
    LdapUserAttributes ldapUserAttributes;

    @Autowired
    LdapCommonAttributes ldapCommonAttributes;

    @Autowired
    SystemTokenRepository systemTokenRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.trace("\n---------- Authentication request ------------\n" +
                "username: {}\n" +
                "password:{}\n" +
                "---------------------------------------------", ((String) authentication.getPrincipal()).toLowerCase(), (String) authentication.getCredentials());

        VaultTemplate template = vaultService.newTemplate();
        String username = ((String) authentication.getPrincipal()).toLowerCase();
        VaultException vaultException = null;

        try {
            VaultToken vault = template.doWithVault(restOperations -> {
                LoginRequest entity = new LoginRequest();
                entity.setPassword((String) authentication.getCredentials());

                ResponseEntity<VaultTokenResponse> responseEntity;
                responseEntity = restOperations.
                        postForEntity(String.format("/auth/ldap/login/%s", username), entity, VaultTokenResponse.class);
                log.info("vault response for normal users is: {} {}", responseEntity.getStatusCode(), responseEntity.getStatusCodeValue());


                return responseEntity.getBody().getToken();
            });

            User user = new User(username,
                    (String) authentication.getCredentials(),
                    Collections.singletonList(new SimpleGrantedAuthority(UserRole.USER.name())));
            UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(user,
                    authentication.getCredentials(), Collections.singleton(new SimpleGrantedAuthority(LoginTypes.LDAP.name())));

            TokenDetails td = (TokenDetails) authentication.getDetails();
            td.setToken(vault.getToken());
            td.setRole(UserRole.USER);
            result.setDetails(td);

            metricService.getCounter(LOGIN_ATTEMPT_COUNT).increment(this.getClass().getAnnotation(Component.class).value());
            Optional<UserEntity> byName = usersOps.byName(username);
            if (!byName.isPresent()) {
                saveExternalUser(result, UserRole.USER);
            }

            return result;
        } catch (VaultException ve) {
            log.error("Authentication error. Context: {} {}", username, ve.getMessage());
            vaultException = ve;
        }

        try {
            VaultToken vault = template. doWithVault(restOperations -> {
                LoginRequest entity = new LoginRequest();
                entity.setPassword((String) authentication.getCredentials());

                ResponseEntity<VaultTokenResponse> responseEntity = restOperations.
                        postForEntity(String.format("/auth/admin/login/%s", username), entity, VaultTokenResponse.class);
                log.info("vault response for admin users is: {} {}", responseEntity.getStatusCode(), responseEntity.getStatusCodeValue());

                return responseEntity.getBody().getToken();
            });

            User user = new User(username,
                    (String) authentication.getCredentials(),
                    Collections.singletonList(new SimpleGrantedAuthority(UserRole.ADMIN.name())));
            UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(user,
                    authentication.getCredentials());

            TokenDetails td = (TokenDetails) authentication.getDetails();
            td.setToken(vault.getToken());
            td.setRole(UserRole.ADMIN);
            result.setDetails(td);

            metricService.getCounter(LOGIN_ATTEMPT_COUNT).increment(this.getClass().getAnnotation(Component.class).value() + "-admin");
            Optional<UserEntity> byName = usersOps.byName(username);
            if (!byName.isPresent()) {
                saveExternalUser(result, UserRole.ADMIN);
            }

            return result;
        } catch (VaultException ve) {
            log.error("Authentication error for admin group: {} {}", username, ve.getMessage());
            ve.initCause(vaultException);
            vaultException = ve;
        }

        throw new AuthenticationServiceException("Authentication Failed", vaultException != null ? vaultException : null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return LdapAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private UserEntity saveExternalUser(Authentication auth, UserRole role) {
        User principal = (User) auth.getPrincipal();
        UserEntity user = new UserEntity();

        user.setUsername(principal.getUsername());
        user.setFirstName(principal.getUsername());
        user.setExternal(true);
        user.setRole(role);
        user.setTwofactorauth(false);

        updateLdapUserData(user);

        return usersOps.newUser(user);
    }

    private void updateLdapUserData(UserEntity user) {
        try {
            List<LdapUser> ldapUsers = ldapTemplateWrapper.getLdapTemplate().search(query().searchScope(SearchScope.SUBTREE).where(ldapCommonAttributes.getObjectClass()).is(ldapUserAttributes.getObjectClass()).and(ldapUserAttributes.getUsername()).is(user.getUsername()),
                    new UserContextMapper(ldapUserAttributes));
            if (!ldapUsers.isEmpty()) {
                LdapUser ldapUser = ldapUsers.get(0);
                user.setFirstName(ldapUser.getFirstName());
                user.setLastName(ldapUser.getLastName());
                user.setEmail(ldapUser.getMail());
                user.setPhone(ldapUser.getTelephoneNumber());
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }
}
