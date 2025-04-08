package com.h2h.pda.service.impl;

import com.h2h.pda.entity.SystemTokenEntity;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.repository.SystemTokenRepository;
import com.h2h.pda.service.api.VaultOperationCallback;
import com.h2h.pda.service.api.VaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.vault.authentication.SessionManager;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VaultServiceImpl implements VaultService {

    @Value("${vault.endpoint}")
    private String vaultEndpoint;
    @Value("${vault.enabled}")
    private boolean isVaultEnabled;

    SessionManager nullSessionManager() {
        return () -> null;
    }

    @Autowired
    SystemTokenRepository systemTokenRepository;

    @Override
    public VaultTemplate newTemplate() {
        return new VaultTemplate(VaultEndpoint.from(URI.create(vaultEndpoint)),
                new SimpleClientHttpRequestFactory(),
                nullSessionManager());
    }

    @Override
    public VaultTemplate newTemplate(String token) {
        return new VaultTemplate(VaultEndpoint.from(URI.create(vaultEndpoint)), new TokenAuthentication(token));
    }

    @Override
    public boolean isVaultEnabled() {
        return isVaultEnabled;
    }

    @Override
    public <T> T doWithVault(VaultOperationCallback<T> callback) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();
        return newTemplate(details.getToken()).doWithVault(restOperations -> callback.doWithRestOperations(restOperations));
    }

    @Override
    public <T> T doWithVault(String token, VaultOperationCallback<T> callback) {
        VaultTemplate template = token == null ? newTemplate() : newTemplate(token);
        return template.doWithVault(restOperations -> callback.doWithRestOperations(restOperations));
    }

    @Override
    public <T> T doWithVaultUsingRootToken(VaultOperationCallback<T> callback) {
        return newTemplate(currentRootToken()).doWithVault(restOperations -> callback.doWithRestOperations(restOperations));
    }

    @Override
    public <T> T doWithVaultUsingRootTokenAndTemplate(VaultTemplateCallback<T> callback) {
        return callback.doWithTemplate(newTemplate(currentRootToken()));
    }

    @Override
    public <T> VaultResponseSupport<T> read(String path, Class<T> responseType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();
        return newTemplate(details.getToken()).read(path, responseType);
    }

    @Override
    public <T> VaultResponseSupport<T> readUsingRootToken(String path, Class<T> responseType) {
        return newTemplate(currentRootToken()).read(path, responseType);
    }

    @Override
    public VaultResponse write(String path, Object body) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            TokenDetails details = (TokenDetails) authentication.getDetails();
            return newTemplate(details.getToken()).write(path, body);
        } else {
            return newTemplate(currentRootToken()).write(path, body);
        }
    }

    @Override
    public void updateRootToken(String rootToken) {
        Optional<SystemTokenEntity> byName = systemTokenRepository.findByName(VAULT_ROOT_TOKEN);
        SystemTokenEntity tokenEntity = byName.isPresent() ? byName.get() : new SystemTokenEntity();

        tokenEntity.setName(VAULT_ROOT_TOKEN);
        tokenEntity.setToken(rootToken);
        tokenEntity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        tokenEntity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        systemTokenRepository.save(tokenEntity);
    }

    @Override
    public String getRootToken() {
        Optional<SystemTokenEntity> optionalRootToken = systemTokenRepository.findByName(VAULT_ROOT_TOKEN);
        return optionalRootToken.map(SystemTokenEntity::getToken).orElse(null);
    }

    String currentRootToken() {
        Optional<SystemTokenEntity> rootToken = systemTokenRepository.findByName(VAULT_ROOT_TOKEN);
        if (!rootToken.isPresent())
            throw new IllegalStateException("root token not found");

        return rootToken.get().getToken();
    }
}
