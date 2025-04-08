package com.h2h.pda.service.api;

import com.h2h.pda.jwt.TokenDetails;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface TokenGenerator {
    long totp2(Integer code) throws NoSuchAlgorithmException, InvalidKeyException;

    String totp(String secret) throws NoSuchAlgorithmException, InvalidKeyException;

    long totp2(byte[] secret) throws NoSuchAlgorithmException, InvalidKeyException;

    long hotp(byte[] secret, byte[] counter) throws NoSuchAlgorithmException, InvalidKeyException;

    String createJwt(TokenDetails tokenDetails);

    TokenDetails decodeJwt(String token);
}
