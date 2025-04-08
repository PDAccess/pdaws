package com.h2h.test.it;

import com.h2h.pda.pojo.AuthUserPass;
import com.h2h.pda.pojo.InitRequest;
import com.h2h.pda.pojo.SealInfo;
import com.h2h.pda.pojo.vault.SealStatusResponse;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class VaultControllerTest extends BaseIntegrationTests {

    @Test
    @Order(1)
    public void checkVault() {
        ResponseEntity<Boolean> call = call("/api/v1/vault/init", HttpMethod.GET, Boolean.class);

        if (!call.getBody()) {
            SealInfo info = new SealInfo().setSecretThreshold(1).setSecretShares(1);
            ResponseEntity<InitRequest> call1 = call("/api/v1/vault/init", HttpMethod.POST, info, InitRequest.class);

            assertThat(call1.getBody().getKeys().size()).isEqualTo(1);

            AuthUserPass pass = new AuthUserPass().setUsername("admin").setPassword("H2HSecure123").setRootToken(call1.getBody().getRootToken()).setPolicy("inventorys");

            ResponseEntity<Void> call2 = call("/api/v1/vault/authuser", HttpMethod.POST, pass, Void.class);

            assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);

            ResponseEntity<SealStatusResponse> call3 = call("/api/v1/vault/status", HttpMethod.POST, SealStatusResponse.class);
            assertThat(call3.getBody().isSealed()).isEqualTo(false);

        } else {
            ResponseEntity<SealStatusResponse> call1 = call("/api/v1/vault/status", HttpMethod.POST, SealStatusResponse.class);
            assertThat(call1.getBody().isSealed()).isEqualTo(false);
        }
    }

}
