package com.h2h.pda.api.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.pojo.user.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.h2h.pda.jwt.SecurityConstants.*;

@RestController
@RequestMapping("/api/v1/token")
public class TokenValidationController {

    private static Logger log = LoggerFactory.getLogger(TokenValidationController.class);

    @PostMapping(path = "/verify")
    public ResponseEntity<UserToken> verifyToken(HttpServletRequest request) {
        try {
            String requestToken = request.getHeader(HEADER_STRING);
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .build()
                    .verify(requestToken.replace(TOKEN_PREFIX, ""));

            String user = jwt.getSubject();
            String token = jwt.getClaim(VAULT_TOKEN).asString();
            UserToken userToken = new UserToken(user, token);

            return ResponseEntity.ok(userToken);
        } catch (JWTVerificationException ve) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping(path = "/valid")
    public ResponseEntity<UserToken> isTokenValid(HttpServletRequest request) {
        UserToken userToken = null;
        String token = request.getHeader(HEADER_STRING);
        TokenDetails tokenDetails = new TokenDetails();
        if (token != null) {
            String user = null;
            try {
                DecodedJWT jwt = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                        .build()
                        .verify(token.replace(TOKEN_PREFIX, ""));
                user = jwt.getSubject();
                Claim claim = jwt.getClaim(VAULT_TOKEN);
                Claim claim1 = jwt.getClaim(PDA_AUTH_ID);
                Claim claim2 = jwt.getClaim(USER_ROLE);
                tokenDetails.setToken(claim.asString());
                tokenDetails.setAuthId(claim1.asString());
                tokenDetails.setRole(UserRole.of(claim2.asString()));

                Date expDate = jwt.getExpiresAt();
                Timestamp now = new Timestamp(System.currentTimeMillis());
                if (now.after(expDate)) {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }

                String selfToken = JWT.create()
                        .withSubject(user)
                        .withClaim(VAULT_TOKEN, claim.asString())
                        .withClaim(PDA_AUTH_ID, claim1.asString())
                        .withClaim(USER_ROLE, claim2.asString())
                        .withExpiresAt(expDate)
                        .sign(HMAC512(SECRET.getBytes()));

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                usernamePasswordAuthenticationToken.setDetails(tokenDetails);

                if (!selfToken.equals(token.replace(TOKEN_PREFIX, "")))
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

                if (log.isDebugEnabled()) {
                    log.debug("token {}", claim.asString());
                }

                userToken = new UserToken(user, claim.asString());

            } catch (Exception e) {
                log.error("Token Validation", e);
            }
        }
        return ResponseEntity.ok(userToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> newToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        TokenDetails tokenDetails = new TokenDetails();
        if (token != null) {
            String user = null;
            try {
                DecodedJWT jwt = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                        .build()
                        .verify(token.replace(TOKEN_PREFIX, ""));
                user = jwt.getSubject();
                Claim claim = jwt.getClaim(VAULT_TOKEN);
                Claim claim1 = jwt.getClaim(PDA_AUTH_ID);
                Claim claim2 = jwt.getClaim(USER_ROLE);
                tokenDetails.setToken(claim.asString());
                tokenDetails.setAuthId(claim1.asString());
                tokenDetails.setRole(UserRole.of(claim2.asString()));


                Date expDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);


                JWT.create()
                        .withSubject(user)
                        .withClaim(VAULT_TOKEN, claim.asString())
                        .withClaim(PDA_AUTH_ID, claim1.asString())
                        .withClaim(USER_ROLE, claim2.asString())
                        .withExpiresAt(expDate)
                        .sign(HMAC512(SECRET.getBytes()));


                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                log.error("Token Validation", e);
                log.warn("Request Path is {} token is {}", request.getRequestURI(), token);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
