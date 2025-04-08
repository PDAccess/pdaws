package com.h2h.pda.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.h2h.pda.pojo.user.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

import static com.h2h.pda.jwt.SecurityConstants.*;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private Logger log = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);


        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token == null) {
            token = request.getParameter(HEADER_STRING);
            if (token != null) {
                try {
                    byte[] encode = Base64.getDecoder().decode(token);
                    token = new String(encode);
                } catch (IllegalArgumentException iae) {
                    token = null;
                }
            }
        }


        TokenDetails tokenDetails = new TokenDetails();
        if (token != null) {
            String user = null;
            try {
                DecodedJWT jwt = JWT.decode(token.replace(TOKEN_PREFIX, ""));
                user = jwt.getSubject();
                Claim claim = jwt.getClaim(VAULT_TOKEN);
                Claim claim1 = jwt.getClaim(PDA_AUTH_ID);
                Claim claim2 = jwt.getClaim(USER_ROLE);
                tokenDetails.setToken(claim.asString());
                tokenDetails.setAuthId(claim1.asString());
                tokenDetails.setRole(UserRole.of(claim2.asString()));

            } catch (JWTVerificationException jve) {
                log.warn("Invalid session {} {}", token, jve.getMessage());
                return null;
            } catch (Exception e) {
                log.error("Token Validation", e);
                log.warn("Request Path is {} token is {}", request.getRequestURI(), token);
                return null;
            }

            if (user != null) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                usernamePasswordAuthenticationToken.setDetails(tokenDetails);
                return  usernamePasswordAuthenticationToken;
            }
            log.warn("user is empty");

            return null;
        }
        log.error("empty token");
        return null;
    }
}


