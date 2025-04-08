package com.h2h.test.message;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.pojo.user.UserRole;
import org.flywaydb.core.internal.resolver.ChecksumCalculator;
import org.flywaydb.core.internal.resource.filesystem.FileSystemResource;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.Arrays;

import static com.h2h.pda.jwt.SecurityConstants.*;
import static com.h2h.pda.jwt.SecurityConstants.USER_ROLE;

public class Checksum {

    @Test
    void calculateChecksum() {

        String filename[] = {"/Users/h2e/pda/pdaws/src/main/resources/db/migration/V1.1__pdadump.sql", "/Users/h2e/pda/pdaws/src/main/resources/db/migration/V1.3__vault.sql",
                "/Users/h2e/pda/pdaws/src/main/resources/db/migration/V10.2__group_properties_table_add.sql"};

        Arrays.stream(filename).forEach(f -> {
            FileSystemResource r = new FileSystemResource(null, f, Charset.forName("UTF-8"));
            int cs = ChecksumCalculator.calculate(r);
            System.out.printf("file: %s checksum: %s \n", f, cs);
        });
    }

    String token = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRlZmF1bHQiLCJ0eXAiOiJKV1QifQ.eyJzdWIiOiJwZGFjY2Vzc2FkbWluIiwidXNlcl9pZCI6ImI5OTMyNTkxLWQ0OGUtNGMwMS1hZDVjLWM5N2IzMTkzMmYzMiIsIlBEQV9TRVNTSU9OX0lEIjoxMjMsInZhdWx0LXRva2VuIjoibm9uZSIsInVSb2xlIjoidXNlciIsInJlYWxtIjoiZGVmYXVsdCIsImV4cCI6MTcxMTMyMDY2MSwiaWF0IjoxNzExMzAyNjYxfQ.";

    @Test
    void testToken() {
        DecodedJWT jwt = JWT.decode(token);
        String user = jwt.getSubject();
        Claim claim = jwt.getClaim(VAULT_TOKEN);
        Claim claim1 = jwt.getClaim(PDA_AUTH_ID);
        Claim claim2 = jwt.getClaim(USER_ROLE);
        TokenDetails tokenDetails = new TokenDetails();
        tokenDetails.setToken(claim.asString());
        tokenDetails.setAuthId(claim1.asString());
        tokenDetails.setRole(UserRole.of(claim2.asString()));
    }
}