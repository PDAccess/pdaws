package com.h2h.pda.api.vault;

import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.AuthUserPass;
import com.h2h.pda.pojo.InitRequest;
import com.h2h.pda.pojo.SealInfo;
import com.h2h.pda.pojo.SealRequest;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.pojo.vault.SealStatusResponse;
import com.h2h.pda.pojo.vault.TokenResponse;
import com.h2h.pda.pojo.vault.VaultInit;
import com.h2h.pda.service.api.UsersOps;
import com.h2h.pda.service.api.VaultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.vault.VaultException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/vault")
public class VaultController {

    private final Logger log = LoggerFactory.getLogger(VaultController.class);
    private static final String X_VAULT_TOKEN = "X-Vault-Token";

    private static final String VAULT_KV = "{\"path\":\"secret\", \"type\":\"kv\"}";
    private static final String VAULT_POLICY = "path \"secret/inventory/*\" {\n" +
            "  capabilities = [\"create\", \"read\", \"update\", \"delete\", \"list\"]\n" +
            "}\n" +
            "path \"auth/userpass/users/*\" {\n" +
            "  capabilities = [\"create\", \"read\", \"update\", \"delete\", \"list\"]\n" +
            "}\n" +
            "path \"auth/adminpass/users/*\" {\n" +
            "  capabilities = [\"create\", \"read\", \"update\", \"delete\", \"list\"]\n" +
            "}\n" +
            "path \"auth/ldap/config\" {\n" +
            "  capabilities = [\"create\", \"read\", \"update\", \"delete\", \"list\"]\n" +
            "}\n" +
            "path \"sys/auth\" {\n" +
            "  capabilities = [\"read\", \"list\"]\n" +
            "}\n" +
            "path \"auth/ldap/groups/*\" {\n" +
            "  capabilities = [\"create\", \"read\", \"update\", \"delete\", \"list\"]\n" +
            "}\n" +
            "path \"auth/admin/config\" {\n" +
            "  capabilities = [\"create\", \"read\", \"update\", \"delete\", \"list\"]\n" +
            "}\n" +
            "path \"auth/admin/groups/*\" {\n" +
            "  capabilities = [\"create\", \"read\", \"update\", \"delete\", \"list\"]\n" +
            "}";

    @Autowired
    VaultService vaultService;

    @Autowired
    UsersOps usersOps;

    @GetMapping(path = "/init")
    public ResponseEntity<Boolean> initGetVault() {
        log.info("check vault status");

        try {
            VaultInit resp = vaultService.doWithVault(null, restOperations -> {
                ResponseEntity<VaultInit> responseEntity = restOperations.getForEntity("/sys/init",
                        VaultInit.class);

                return responseEntity.getBody();
            });

            return new ResponseEntity<>(resp.isInitialized(), HttpStatus.OK);
        } catch (VaultException ve) {
            log.error("Vault error:", ve);
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping(path = "/init")
    public ResponseEntity<InitRequest> initVault(@RequestBody SealInfo sealInfo) {

        Map<String, Integer> map = new HashMap<>();
        map.put("secret_shares", sealInfo.getSecretShares());
        map.put("secret_threshold", sealInfo.getSecretThreshold());

        HttpEntity<Map<String, Integer>> requestUpdate = new HttpEntity<>(map);

        try {
            TokenResponse tokenResponse = vaultService.doWithVault(null, restOperations -> {
                ResponseEntity<TokenResponse> responseEntity = restOperations.exchange("/sys/init", HttpMethod.PUT,
                        requestUpdate, TokenResponse.class);
                return responseEntity.getBody();
            });

            String rootToken = tokenResponse.getRootToken();
            InitRequest initRequest = new InitRequest(rootToken, tokenResponse.getKeys());

            unSeal(initRequest, map.get("secret_threshold"));

            vaultService.updateRootToken(rootToken);

            return new ResponseEntity<>(initRequest, HttpStatus.OK);
        } catch (VaultException ve) {
            log.error("Vault error:", ve);
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public void unSeal(InitRequest initRequest, int threshold) {

        for (int i = 0; i < threshold; i++) {
            String seal = initRequest.getKeys().get(i);
            SealRequest sealRequest = new SealRequest();
            sealRequest.setKey(seal);

            try {
                vaultService.doWithVault(null, restOperations -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("key", sealRequest.getKey());
                    HttpEntity<Map<String, String>> requestUpdate = new HttpEntity<>(map);
                    ResponseEntity<SealStatusResponse> exchange = restOperations.exchange("/sys/unseal", HttpMethod.PUT,
                            requestUpdate, SealStatusResponse.class);

                    return exchange.getBody();
                });
            } catch (VaultException ve) {
                SealStatusResponse response = status().getBody();
                response.setErrors(Arrays.asList(ve.getMessage()));
            }
        }
    }

    @PostMapping(path = "auth-user")
    public ResponseEntity<Void> authUser(@RequestBody AuthUserPass authUserPass) {

        /* Use Root Token */
        HttpHeaders httpHeader = new HttpHeaders();
        httpHeader.set(X_VAULT_TOKEN, authUserPass.getRootToken());
        HttpEntity entity = new HttpEntity(httpHeader);
        vaultService.doWithVault(null, restOperations -> {
            ResponseEntity<String> responseEntity = restOperations.exchange("/auth/token/lookup-self", HttpMethod.GET, entity, String.class);
            return responseEntity.getBody();
        });

        /* Userpass Authentication */
        Map<String, String> userpassMap = new HashMap<>();
        userpassMap.put("type", "userpass");
        httpHeader.set(X_VAULT_TOKEN, authUserPass.getRootToken());
        HttpEntity<Map<String, String>> userpassEntity = new HttpEntity<>(userpassMap, httpHeader);
        vaultService.doWithVault(null, restOperations -> {
            ResponseEntity<String> responseEntity = restOperations.exchange("/sys/auth/userpass", HttpMethod.POST, userpassEntity, String.class);
            return responseEntity.getBody();
        });

        /* Adminpass Authentication */
        Map<String, String> adminPassMap = new HashMap<>();
        adminPassMap.put("type", "userpass");
        httpHeader.set(X_VAULT_TOKEN, authUserPass.getRootToken());
        HttpEntity<Map<String, String>> adminPassEntity = new HttpEntity<>(adminPassMap, httpHeader);
        vaultService.doWithVault(null, restOperations -> {
            ResponseEntity<String> responseEntity = restOperations.exchange("/sys/auth/adminpass", HttpMethod.POST, adminPassEntity, String.class);
            return responseEntity.getBody();
        });

        /* Add User */
        Map<String, String> createUserMap = new HashMap<>();
        createUserMap.put("password", authUserPass.getPassword());
        createUserMap.put("policies", authUserPass.getPolicy());
        httpHeader.set(X_VAULT_TOKEN, authUserPass.getRootToken());
        HttpEntity<Map<String, String>> createUserEntity = new HttpEntity<>(createUserMap, httpHeader);
        vaultService.doWithVault(null, restOperations -> {
            ResponseEntity<String> responseEntity = restOperations.exchange(UsersOps.AUTH_ADMINPASS_USERS + authUserPass.getUsername(), HttpMethod.POST, createUserEntity, String.class);
            return responseEntity.getBody();
        });

        UserEntity userEntity = new UserEntity();

        userEntity.setUsername(authUserPass.getUsername());
        userEntity.setFirstName(authUserPass.getUsername());
        userEntity.setLastName(authUserPass.getUsername());
        userEntity.setRole(UserRole.ADMIN);
        userEntity.setRememberToken("123");
        userEntity.setExternal(false);
        usersOps.newUser(userEntity);

//        Map<String, String> createUserMap3 = new HashMap<>();
//        createUserMap3.put("data", VAULT_KV);
        HttpEntity<String> createUserEntity3 = new HttpEntity<>(VAULT_KV, httpHeader);
        httpHeader.set(X_VAULT_TOKEN, authUserPass.getRootToken());
        vaultService.doWithVault(null, restOperations -> {
            ResponseEntity<String> responseEntity = restOperations.exchange("/sys/mounts/secret", HttpMethod.POST, createUserEntity3, String.class);
            return responseEntity.getBody();
        });

        Map<String, String> createUserMap2 = new HashMap<>();
        createUserMap2.put("rules", VAULT_POLICY);
        HttpEntity<Map<String, String>> createUserEntity2 = new HttpEntity<>(createUserMap2, httpHeader);
        httpHeader.set(X_VAULT_TOKEN, authUserPass.getRootToken());
        vaultService.doWithVault(null, restOperations -> {
            ResponseEntity<String> responseEntity = restOperations.exchange("/sys/policy/" + authUserPass.getPolicy(), HttpMethod.PUT, createUserEntity2, String.class);
            return responseEntity.getBody();
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/status")
    public ResponseEntity<SealStatusResponse> statusGeneral() {
        try {
            SealStatusResponse resp = vaultService.doWithVault(null, restOperations -> {
                ResponseEntity<SealStatusResponse> responseEntity = restOperations.getForEntity("/sys/seal-status"
                        , SealStatusResponse.class);
                return responseEntity.getBody();
            });

            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (VaultException vaultException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @GetMapping(path = "/status")
    public ResponseEntity<SealStatusResponse> status() {
        return statusGeneral();
    }

    @PostMapping(path = "/unlock")
    public ResponseEntity<SealStatusResponse> unlock(@RequestBody SealRequest seal) {

        try {
            SealStatusResponse resp = vaultService.doWithVault(null, restOperations -> {
                Map<String, String> map = new HashMap<>();
                map.put("key", seal.getKey());
                HttpEntity<Map<String, String>> requestUpdate = new HttpEntity<>(map);
                ResponseEntity<SealStatusResponse> exchange = restOperations.exchange("/sys/unseal", HttpMethod.PUT,
                        requestUpdate, SealStatusResponse.class);

                return exchange.getBody();
            });

            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (VaultException ve) {
            SealStatusResponse response = status().getBody();
            response.setErrors(Arrays.asList(ve.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}