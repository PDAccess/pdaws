package com.h2h.pda.service.impl;

import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.service.api.TokenGenerator;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class TokenGeneratorImpl implements TokenGenerator {
    @Override
    public long totp2(Integer code) throws NoSuchAlgorithmException, InvalidKeyException {
        return totp2(ByteBuffer.allocate(4).putInt(code).array());
    }

    @Override
    public String totp(String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secret);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }


    @Override
    public long totp2(byte[] secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Base32 base32 = new Base32();

        byte[] decode = base32.decode(new String(secret));
        return hotp(decode, toBytes(System.currentTimeMillis() / 1000 / 30));
    }

    private byte[] toBytes(long value) {
        ByteBuffer result = ByteBuffer.allocate(8);
        byte mask = (byte) 0xFF;
        short[] shifts = {56, 48, 40, 32, 24, 16, 8, 0};

        for (short shift : shifts) {
            result = result.put((byte) ((value >> shift) & mask));
        }

        return result.array();
    }

    @Override
    public long hotp(byte[] secret, byte[] counter) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signKey = new SecretKeySpec(secret, "HmacSHA1");

        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(counter);

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(buffer.array());

        int offset = hash[19] & 0xF;
        long truncatedHash = hash[offset] & 0x7F;

        for (int the = 1; the < 4; the++) {
            // Perform the shift left 8 bits
            truncatedHash <<= 8;
            truncatedHash |= hash[offset + the] & 0xFF;
        }
        long l = truncatedHash %= 1000000;
        return l;
    }

    @Override
    public String createJwt(TokenDetails tokenDetails) {
        // TODO: jwt token create logic
//      String sign = JWT.create()
//                .withSubject(user.getUsername())
//                .withClaim(VAULT_TOKEN, details.getToken())
//                .withClaim(PDA_SESSION_ID, sessionId)
//                .withClaim(USER_ROLE, details.getRole().getName())
//                .withIssuedAt(Date.from(Instant.now()))
//                .withExpiresAt(Date.from(Instant.now().plus(EXPIRATION_TIME, ChronoUnit.MILLIS)))
//                .sign(HMAC512(SECRET.getBytes()));
//
//        return sign;
        return null;
    }

    @Override
    public TokenDetails decodeJwt(String token) {
        // TODO: jwt token decode logic
        return null;
    }
}
