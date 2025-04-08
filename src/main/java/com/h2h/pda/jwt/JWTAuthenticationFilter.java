package com.h2h.pda.jwt;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.jwt.providers.InternalAuthenticationToken;
import com.h2h.pda.jwt.providers.LdapAuthenticationToken;
import com.h2h.pda.pojo.service.ServiceMeta;
import com.h2h.pda.service.api.ActionPdaService;
import com.h2h.pda.service.api.MfaService;
import com.h2h.pda.service.api.SessionService;
import com.h2h.pda.service.api.UsersOps;
import com.h2h.pda.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.vault.VaultException;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.h2h.pda.jwt.SecurityConstants.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter implements AuthenticationSuccessHandler {

    private static Logger log = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    private final ObjectMapper mapper;

    @Autowired
    SessionService sessionService;

    @Autowired
    UsersOps usersOps;

    @Autowired
    MfaService mfaService;

    @Autowired
    ActionPdaService actionPdaService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, SessionService sessionService, UsersOps usersOps, MfaService mfaService, ActionPdaService actionPdaService) {
        setAuthenticationManager(authenticationManager);

        this.sessionService = sessionService;
        this.usersOps = usersOps;
        this.mfaService = mfaService;
        this.actionPdaService = actionPdaService;

        setAuthenticationSuccessHandler(this);

        setAuthenticationFailureHandler((request, response, exception) -> {
            int code = HttpStatus.FORBIDDEN.value();

            Throwable cause = exception.getCause();
            if (cause instanceof VaultException) {
                VaultException v = (VaultException) cause;
                if (v.getCause() instanceof HttpStatusCodeException) {
                    HttpStatusCodeException hscp = (HttpStatusCodeException) v.getCause();
                    code = hscp.getStatusCode().value();
                }
            }

            response.setStatus(code);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().println(exception.getMessage());
            response.getWriter().flush();
        });
        mapper = new ObjectMapper();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) {

        if (!req.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + req.getMethod());
        }

        LoginRequest loginRequest = null;

        try {
            loginRequest = mapper.readValue(req.getInputStream(), LoginRequest.class);
        } catch (JsonParseException jpe) {
            throw new AuthenticationServiceException("Wrong Login Request Parameters", jpe);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Illegal Login Request", e);
        }

        LoginTypes type = LoginTypes.parseLogin(loginRequest.getLoginType());
        String[] split = loginRequest.getUsername().split("@");

        String username = split[0];
        String tenant = split.length > 1 ? split[1] : null;

        AbstractAuthenticationToken auth = null;

        switch (type) {
            case LDAP:
                auth = new LdapAuthenticationToken(
                        username,
                        loginRequest.getPassword());
                break;
            case INTERNAL:
                auth = new InternalAuthenticationToken(
                        username,
                        loginRequest.getPassword(), loginRequest.getService());
                break;
            case STANDARD:
            default:
                auth = new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword());
        }

        TokenDetails details = new TokenDetails();
        details.setRemoteAddress(RequestUtil.remoteAddress(req));
        details.setUserAgent(RequestUtil.userAgent(req));
        details.setAuthType(type);
        details.setServiceId(loginRequest.getService());
        auth.setDetails(details);

        return getAuthenticationManager().authenticate(auth);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth) throws IOException {
        User user = (User) auth.getPrincipal();
        Integer sessionId = saveSession(req, user);
        Optional<UserEntity> userEntity = usersOps.byName(user.getUsername());

        if (!userEntity.isPresent()) {
            res.setStatus(HttpStatus.NO_CONTENT.value());
            res.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
            res.getWriter().write("User not exists");
            return;
        }

        UserEntity byUsername = userEntity.get();
        user.eraseCredentials();

        TokenDetails details = (TokenDetails) auth.getDetails();

        String token = JWT.create()
                .withSubject(user.getUsername())
                .withClaim(VAULT_TOKEN, details.getToken())
                .withClaim(PDA_AUTH_ID, sessionId)
                .withClaim(USER_ROLE, details.getRole().getName())
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plus(EXPIRATION_TIME, ChronoUnit.MILLIS)))
                .sign(HMAC512(SECRET.getBytes()));

        if (Boolean.TRUE.equals(byUsername.getTwofactorauth())) {
            mfaService.storeToken(byUsername.getUsername(), token);
            mfaService.sendMfaCode(byUsername, RequestUtil.remoteAddress(req));
            res.setStatus(HttpStatus.ACCEPTED.value());
            res.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
            res.getWriter().write("Waiting for MFA Code");
        } else {
            res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
            res.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
            res.getWriter().write(token);
            actionPdaService.saveAction("Logined", sessionId);
        }
    }

    private int saveSession(HttpServletRequest request, User user) {
        Integer sessionId = sessionService.start(user.getUsername().toLowerCase(),
                ServiceMeta.PDA, "123", RequestUtil.remoteAddress(request));

        return sessionId;
    }
}