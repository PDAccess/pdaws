package com.h2h.pda.service.api;

import org.springframework.lang.Nullable;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;

public interface VaultService {
    String VAULT_ROOT_TOKEN = "vault_root_token";
    String VAULT_ERROR = "Vault error: {}";

    interface VaultTemplateCallback<T> {
        @Nullable
        T doWithTemplate(VaultTemplate vaultTemplate);
    }

    VaultTemplate newTemplate();

    VaultTemplate newTemplate(String token);

    boolean isVaultEnabled();

    void updateRootToken(String rootToken);

    String getRootToken();

    <T> T doWithVault(VaultOperationCallback<T> callback);

    <T> T doWithVault(String token, VaultOperationCallback<T> callback);

    <T> T doWithVaultUsingRootToken(VaultOperationCallback<T> callback);

    <T> T doWithVaultUsingRootTokenAndTemplate(VaultTemplateCallback<T> callback);

    <T> VaultResponseSupport<T> read(String path, Class<T> responseType);

    <T> VaultResponseSupport<T> readUsingRootToken(String path, Class<T> responseType);

    VaultResponse write(String path, @Nullable Object body);

}