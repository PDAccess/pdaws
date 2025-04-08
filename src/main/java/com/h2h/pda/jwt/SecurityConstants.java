package com.h2h.pda.jwt;

public interface SecurityConstants {
    String SECRET = "026d88e5-466b-7124-b6b6-6197c7b67384";
    //String SECRET = UUID.randomUUID().toString();
    long EXPIRATION_TIME = 864_000_000;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
    String SIGN_UP_URL = "/users/sign-up";
    String VAULT_TOKEN = "vault-token";
    String PDA_AUTH_ID = "authId";
    String USER_ROLE = "uRole";
}