package com.h2h.pda.service.impl;

import com.h2h.pda.config.AsyncQueues;
import com.h2h.pda.entity.*;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.pojo.*;
import com.h2h.pda.pojo.group.GroupRole;
import com.h2h.pda.pojo.mail.EmailData;
import com.h2h.pda.pojo.permission.Permissions;
import com.h2h.pda.pojo.vault.CredentialCounter;
import com.h2h.pda.pojo.vault.CredentialRequestCounter;
import com.h2h.pda.repository.*;
import com.h2h.pda.service.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.jms.Session;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.h2h.pda.config.CacheNames.CREDENTIAL_REQUEST_COUNTER;
import static com.h2h.pda.config.CacheNames.VAULT_COUNTER;
import static com.h2h.pda.pojo.Credential.SECRET_CREDENTIAL;

@Service
public class CredentialManagerImpl implements CredentialManager {

    private static final Logger log = LoggerFactory.getLogger(CredentialManagerImpl.class);
    private static final String VAULT_ERROR = "Vault error: {}";
    private static final String VERIFY_ERROR = "Verify Operation has failed: {}";
    private static final String CM_MS_URL = "%s/%s";

    private String randomString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private int passwordLength = 16;
    private String hostname;

    @Autowired
    SystemSettings systemSettings;

    @Autowired
    AutoCredentialHistoryRepository historyRepository;

    @Autowired
    VaultService vaultService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    SystemTokenRepository systemTokenRepository;

    @Autowired
    CredentialRepository credentialRepository;

    @Autowired
    UsersOps usersOps;

    @Autowired
    BreakTheGlassRepository breakTheGlassRepository;

    @Autowired
    AutoCredentialHistoryRepository autoCredentialHistoryRepository;

    @Autowired
    BreakTheGlassShareRepository breakTheGlassShareRepository;

    @Autowired
    SendEmailService sendEmailService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    GroupOps groupOps;

    @Autowired
    CredentialRequestRepository credentialRequestRepository;

    @Value("${credentialManager.endpoint}")
    private String credentialManagerEndpoint;


    @JmsListener(destination = AsyncQueues.QueueNames.CREDENTIAL_MANAGER_QUEUE, containerFactory = "queueListenerFactory")
    public void changePassword(@Payload CredentialManagerWrapper data,
                               @Headers MessageHeaders headers,
                               Message message, Session session) {

        AutoCredentialsHistoryEntity autoCredentialsHistoryEntity = new AutoCredentialsHistoryEntity();
        autoCredentialsHistoryEntity.setId(UUID.randomUUID().toString());
        autoCredentialsHistoryEntity.setCredentialId(data.getCredentialId());
        autoCredentialsHistoryEntity.setInventoryId(data.getServiceId());
        autoCredentialsHistoryEntity.setStartAt(Timestamp.valueOf(LocalDateTime.now()));

        if (systemSettings.hasTag("vaultautosettings", "default_password_length")) {
            passwordLength = Integer.parseInt(systemSettings.tagValue("vaultautosettings", "default_password_length").get());
        }
        if (systemSettings.hasTag("vaultautosettings", "default_password_chars")) {
            randomString = systemSettings.tagValue("vaultautosettings", "default_password_chars").get();
        }
        if (systemSettings.hasTag("generalSettings", "default_system_host_name")) {
            hostname = systemSettings.tagValue("generalSettings", "default_system_host_name").get();
        }

        String password = "";
        if (StringUtils.hasText(data.getPassword())) {
            password = data.getPassword();
        } else {
            StringBuilder passwordBuilder = new StringBuilder(passwordLength);
            IntStream.range(0, passwordLength)
                    .forEach(i -> passwordBuilder.append(randomString.charAt(new SecureRandom().nextInt(randomString.length()))));
            password = passwordBuilder.toString();
        }

        VaultTemplate template = vaultService.newTemplate(data.getToken());

        try {

            CredentialParams credentialParams = new CredentialParams();

            VerifyRequest request = new VerifyRequest();

            VaultResponseSupport<CredentialParams> vaultResponseSupport =
                    template.read(SECRET_CREDENTIAL + data.getCredentialId(), CredentialParams.class);

            credentialParams = vaultResponseSupport.getData();

            request.setPassword(credentialParams.getPassword());
            request.setUsername(credentialParams.getUsername());
            request.setHostname(data.getIpAddress());
            request.setPort(data.getPort() == null ? 22 : data.getPort());
            request.setNewpassword(password);
            request.setProto(data.getType().getTypeName());

            CredentialManagerResponse verifyResponse = verify(request);
            autoCredentialsHistoryEntity.setResult(verifyResponse.isResult());
            autoCredentialsHistoryEntity.setEndAt(Timestamp.valueOf(LocalDateTime.now()));

            if (verifyResponse.isResult()) {
                CredentialManagerResponse changePasswordResponse = changePassword(request);
                autoCredentialsHistoryEntity.setEndAt(Timestamp.valueOf(LocalDateTime.now()));
                autoCredentialsHistoryEntity.setResult(changePasswordResponse.isResult());

                if (changePasswordResponse.isResult()) {
                    try {
                        credentialParams.setPassword(password);
                        template.write(SECRET_CREDENTIAL + data.getCredentialId(), credentialParams);
                    } catch (VaultException ve) {
                        log.error(VAULT_ERROR, ve.getMessage());
                    }
                }

                autoCredentialsHistoryEntity.setDescription("changepasswd:" + changePasswordResponse.getMessage());
            } else {
                autoCredentialsHistoryEntity.setDescription("verify:" + verifyResponse.getMessage());
            }

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
        }

        CredentialEntity credentialEntity = getCredential(data.getCredentialId());
        if (credentialEntity != null && credentialEntity.getWhoCreate() != null) {
            UserEntity userEntity = credentialEntity.getWhoCreate();
            EmailData emailData = new EmailData();
            emailData.setToMail(userEntity.getEmail());
            emailData.setSubject(String.format("Password Change: %s", credentialEntity.getUsername()));
            String successfullMessage = String.format("Password changed successfully for credential with username %s.", credentialEntity.getUsername());
            String failMessage = String.format("Password changed failed for credential with username %s.", credentialEntity.getUsername());
            String mailMessage = autoCredentialsHistoryEntity.getResult() ? successfullMessage : failMessage;
            emailData.setText(mailMessage);
            emailData.setDescription(autoCredentialsHistoryEntity.getDescription());
            emailData.setLink(String.format("%s/%s", hostname, "/vault/credentials/detail/" + credentialEntity.getCredentialId()));
            sendEmailService.pushEmailRequest(emailData);

            if (data.getWhoTriggered() != null && !data.getWhoTriggered().getUserId().equals(userEntity.getUserId())) {
                EmailData emailData2 = new EmailData();
                emailData2.setToMail(data.getWhoTriggered().getEmail());
                emailData2.setSubject(String.format("Password Change: %s", credentialEntity.getUsername()));
                emailData2.setText(mailMessage);
                emailData2.setDescription(autoCredentialsHistoryEntity.getDescription());
                emailData2.setLink(String.format("%s/%s", hostname, "/vault/credentials/detail/" + credentialEntity.getCredentialId()));
                sendEmailService.pushEmailRequest(emailData);
            }
        }

        historyRepository.save(autoCredentialsHistoryEntity);
    }

    @Override
    public CredentialManagerResponse verify(VerifyRequest vr) {
        try {
            ResponseEntity<CredentialManagerResponse> exchange = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/verify"), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerResponse.class);

            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            } else {
                return new CredentialManagerResponse().setResult(false).setMessage(exchange.getStatusCode().toString()).setRunTime(Timestamp.from(Instant.now()));
            }
        } catch (RestClientException rce) {
            log.warn(VERIFY_ERROR, rce.getMessage());
            return new CredentialManagerResponse().setResult(false).setMessage(rce.getMessage()).setRunTime(Timestamp.from(Instant.now()));
        }
    }

    @Override
    public CredentialManagerResponse changePassword(VerifyRequest vr) {
        try {
            ResponseEntity<CredentialManagerResponse> exchange = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/changepass"), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerResponse.class);

            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            } else {
                return new CredentialManagerResponse().setResult(false).setMessage(exchange.getStatusCode().toString()).setRunTime(Timestamp.from(Instant.now()));
            }
        } catch (RestClientException rce) {
            log.warn(VERIFY_ERROR, rce.getMessage());
            return new CredentialManagerResponse().setResult(false).setMessage(rce.getMessage()).setRunTime(Timestamp.from(Instant.now()));
        }
    }

    @Override
    public void pushChangeRequest(CredentialManagerWrapper request) {
        if (request.getToken() == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                TokenDetails details = (TokenDetails) authentication.getDetails();
                request.setToken(details.getToken());
            } else {
                // Don't access root token directly
                // SystemTokenEntity rootToken = systemTokenRepository.findByName(VAULT_ROOT_TOKEN);
                // request.setToken(rootToken.getToken());
            }
        }

        jmsTemplate.convertAndSend(AsyncQueues.QueueNames.CREDENTIAL_MANAGER_QUEUE, request);
    }

    @Override
    @Transactional
    public CredentialEntity newCredential(CredentialEntity credentialEntity, CredentialParams credentialParams) {
        if (StringUtils.hasLength(credentialParams.getParentCredentialId())) {
            credentialEntity.setParentCredential(getCredential(credentialParams.getParentCredentialId()));
        } else {
            vaultService.write("secret/inventory/credentials/" + credentialParams.getCredentialId(), credentialParams);
        }

        credentialEntity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        credentialEntity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        CredentialEntity save = credentialRepository.save(credentialEntity);
        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setPermissionsSet(new HashSet<>(Arrays.asList(Permissions.values())));

        permissionService.createPermission(save.getCredentialId(), save.getWhoCreate().getUserId(), permissionEntity);

        return save;
    }

    @Override
    public List<CredentialEntity> getCredentials(String groupId, String filter, PageRequest pageRequest) {
        return credentialRepository.findByGroupId(groupId, filter, pageRequest);
    }

    @Override
    public List<CredentialEntity> getAllCredentials(String groupId) {
        return credentialRepository.findAllByGroupId(groupId);
    }

    @Override
    public CredentialEntity getCredential(String credentialId) {
        return credentialRepository.findByIdAndNotDeleted(credentialId).orElse(null);
    }

    @Override
    public List<VaultCredentialParams> getCredentialsByUserId(String userId, String filter, PageRequest pageRequest) {
        return credentialRepository.findByUserId(userId, filter, pageRequest);
    }

    @Override
    public List<VaultCredentialParams> getCredentialsByGroupIds(String userId, List<String> groupIds, String filter, PageRequest pageRequest) {
        return credentialRepository.findByGroupIds(userId, groupIds, filter, pageRequest);
    }

    @Override
    public List<CredentialEntity> getCredentialsByUserIdAndGroupId(String userId, String groupId) {
        return credentialRepository.findByUserIdAndGroupId(userId, groupId);
    }

    @Override
    public CredentialParams breakCredential(String credentialId, String reason, String ipAddress, boolean isShare) {
        CredentialEntity credentialEntity = getCredential(credentialId);
        CredentialEntity credential = getMostParentCredential(credentialEntity);

        BreakTheGlassEntity breakTheGlass = getLastCredentialBreak(credentialId);
        if (credential == null || (credential.isCheckStatus() && (breakTheGlass != null && breakTheGlass.getCheckoutTime() == null))) {
            return null;
        }
        UserEntity userEntity = usersOps.securedUser();
        BreakTheGlassEntity breakTheGlassEntity = new BreakTheGlassEntity();
        breakTheGlassEntity.setBreakId(UUID.randomUUID().toString());
        breakTheGlassEntity.setUserId(userEntity.getUserId());
        breakTheGlassEntity.setUserEntity(userEntity);
        breakTheGlassEntity.setCheckedTime(Timestamp.valueOf(LocalDateTime.now()));
        breakTheGlassEntity.setReason(reason);
        breakTheGlassEntity.setCredentialEntity(credential);
        breakTheGlassEntity.setIpAddress(ipAddress);
        breakTheGlassEntity.setShare(isShare);
        breakTheGlassRepository.save(breakTheGlassEntity);

        try {
            VaultResponseSupport<CredentialParams> vaultResponse = vaultService.read("secret/inventory/credentials/" + credential.getCredentialId(), CredentialParams.class);
            CredentialParams credentialParams = vaultResponse.getData();
            if (credentialParams == null) throw new VaultException("Credentials not found");
            credentialParams.setUsername(credentialEntity.getUsername());
            return vaultResponse.getData();
        } catch (VaultException vaultException) {
            return null;
        }
    }

    @Override
    public CredentialParams breakCredentialUsingRootToken(String credentialId, String reason, UserEntity userEntity, List<UserEntity> userEntities, String ipAddress, boolean isShare) {
        CredentialEntity credentialEntity = getCredential(credentialId);
        CredentialEntity credential = getMostParentCredential(credentialEntity);

        if (credential == null) return null;

        if (userEntities == null || userEntities.size() == 0) {
            userEntities = new ArrayList<>();
            userEntities.add(userEntity);
        }

        for (UserEntity user : userEntities) {
            BreakTheGlassEntity breakTheGlassEntity = new BreakTheGlassEntity();
            breakTheGlassEntity.setBreakId(UUID.randomUUID().toString());
            breakTheGlassEntity.setUserId(user.getUserId());
            breakTheGlassEntity.setUserEntity(user);
            breakTheGlassEntity.setCheckedTime(Timestamp.valueOf(LocalDateTime.now()));
            breakTheGlassEntity.setReason(reason);
            breakTheGlassEntity.setCredentialEntity(credential);
            breakTheGlassEntity.setIpAddress(ipAddress);
            breakTheGlassEntity.setShare(isShare);
            breakTheGlassRepository.save(breakTheGlassEntity);
        }

        try {
            VaultResponseSupport<CredentialParams> vaultResponseSupport = vaultService.readUsingRootToken("secret/inventory/credentials/" + credential.getCredentialId(), CredentialParams.class);
            CredentialParams credentialParams = vaultResponseSupport.getData();
            if (credentialParams == null) throw new VaultException("Credentials not found");
            credentialParams.setUsername(credentialEntity.getUsername());
            return vaultResponseSupport.getData();
        } catch (VaultException vaultException) {
            return null;
        }
    }

    @Override
    public BreakTheGlassShareEntity getSharedLink(String shareId) {
        Optional<BreakTheGlassShareEntity> optionalShareEntity = breakTheGlassShareRepository.findById(shareId);
        return optionalShareEntity.orElse(null);
    }

    @Override
    public BreakTheGlassShareEntity createSharedLink(BreakTheGlassShareEntity shareEntity) {
        shareEntity.setId(UUID.randomUUID().toString());
        shareEntity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        shareEntity.setUserEntity(usersOps.securedUser());
        return breakTheGlassShareRepository.save(shareEntity);
    }

    @Override
    public void revokeSharedLink(String shareId) {
        Optional<BreakTheGlassShareEntity> optionalShareEntity = breakTheGlassShareRepository.findById(shareId);
        optionalShareEntity.ifPresent(shareEntity -> breakTheGlassShareRepository.delete(shareEntity));
    }

    @Override
    public CredentialEntity updateCredential(CredentialEntity credentialEntity) {
        return credentialRepository.save(credentialEntity);
    }

    @Override
    public void deleteCredential(String credentialId) {
        Optional<CredentialEntity> optionalCredentialEntity = credentialRepository.findById(credentialId);
        optionalCredentialEntity.ifPresent(credentialEntity -> credentialRepository.delete(credentialEntity));
    }

    @Override
    public BreakTheGlassEntity getLastCredentialBreak(String credentialId) {
        return breakTheGlassRepository.findTop1ByCredentialEntityCredentialIdOrderByCheckedTimeDesc(credentialId);
    }

    @Override
    public AutoCredentialsHistoryEntity getLastCredentialChange(String credentialId) {
        return autoCredentialHistoryRepository.findTop1ByCredentialIdOrderByEndAtDesc(credentialId);
    }

    @Override
    public List<AutoCredentialsHistoryEntity> getChangeHistory(String credentialId, PageRequest pageRequest) {
        return autoCredentialHistoryRepository.findByCredentialId(credentialId, pageRequest);
    }

    @Override
    public void checkoutCredential(String credentialId) {
        BreakTheGlassEntity breakTheGlassEntity = getLastCredentialBreak(credentialId);
        if (breakTheGlassEntity != null && breakTheGlassEntity.getUserEntity().getUserId().equals(usersOps.securedUser().getUserId())) {
            breakTheGlassEntity.setCheckoutTime(new Timestamp(System.currentTimeMillis()));
            breakTheGlassRepository.save(breakTheGlassEntity);
        }
    }

    @Override
    public Optional<CredentialEntity> getCredentialByNameAndGroup(String credentialName, String groupName) {
        return credentialRepository.findByNameAndGroup(credentialName, groupName);
    }

    @Override
    public VaultResponse updateCredentialData(CredentialParams credentialParams) {
        return vaultService.write("secret/inventory/credentials/" + credentialParams.getCredentialId(), credentialParams);
    }

    @Override
    @Cacheable(cacheNames = VAULT_COUNTER, key = "#userId")
    public CredentialCounter credentialCounters(String userId) {
        List<GroupsEntity> groupsEntities = groupOps.searchBy(userId, GroupRole.ADMIN);
        List<String> groupIds = groupsEntities.stream().map(GroupsEntity::getGroupId).collect(Collectors.toList());
        CredentialCounter vaultCounter = new CredentialCounter();
        vaultCounter.setAll(credentialRepository.countOfCredentials(userId));
        vaultCounter.setYours(credentialRepository.countOfCredentialsForAdmin(userId, groupIds));
        return vaultCounter;
    }

    @Override
    @Cacheable(cacheNames = CREDENTIAL_REQUEST_COUNTER, key = "#credentialId")
    public CredentialRequestCounter credentialRequestCounters(String credentialId) {
        CredentialRequestCounter requestCounter = new CredentialRequestCounter();
        requestCounter.setWaited(credentialRequestRepository.countOfWaitRequest(credentialId));
        requestCounter.setApproved(credentialRequestRepository.countOfApprovedRequest(credentialId));
        requestCounter.setNotApproved(credentialRequestRepository.countOfNotApprovedRequest(credentialId));
        return requestCounter;
    }

    @Override
    public CredentialEntity getMostParentCredential(CredentialEntity credentialEntity) {
        while (credentialEntity != null && credentialEntity.getParentCredential() != null) {
            credentialEntity = credentialEntity.getParentCredential();
        }
        return credentialEntity;
    }

    @Override
    public List<CredentialRequestEntity> getNotRespondedCredentialRequests(String credentialId) {
        return credentialRequestRepository.findNotRespondedRequestsByCredential(credentialId);
    }

    @Override
    public List<CredentialRequestEntity> getRespondedCredentialRequests(String credentialId, boolean isApproved) {
        return credentialRequestRepository.findRespondedRequestsByCredentialAndApproval(credentialId, isApproved);
    }

    @Override
    public boolean isAuthorizedForApproveCredentialRequests(String credentialId, String userId) {
        CredentialEntity credentialEntity = getCredential(credentialId);
        return credentialEntity != null && userId.equals(credentialEntity.getWhoCreate().getUserId());
    }

    @Override
    public CredentialRequestEntity getCredentialRequestById(int requestId) {
        return credentialRequestRepository.findNotRespondedRequestById(requestId);
    }

    @Override
    public CredentialRequestEntity requestCredential(String credentialId) {
        CredentialEntity credentialEntity = getCredential(credentialId);
        if (credentialEntity == null) {
            return null;
        }

        UserEntity requestingUser = usersOps.securedUser();
        CredentialRequestEntity credentialRequestEntity = credentialRequestRepository.findNotRespondedRequestsByCredentialAndUser(credentialId, requestingUser.getUserId());
        if (credentialRequestEntity == null) {
            credentialRequestEntity = new CredentialRequestEntity();
            credentialRequestEntity.setCredentialEntity(credentialEntity);
            credentialRequestEntity.setRequestingUser(requestingUser);
            credentialRequestEntity.setRequestedAt(Timestamp.valueOf(LocalDateTime.now()));
            credentialRequestEntity = credentialRequestRepository.save(credentialRequestEntity);
        }

        return credentialRequestEntity;
    }

    @Override
    public CredentialRequestEntity responseCredentialRequest(int requestId, String ipAddress, boolean isApproved) {
        CredentialRequestEntity credentialRequestEntity = credentialRequestRepository.findNotRespondedRequestById(requestId);
        UserEntity responseUser = usersOps.securedUser();
        if (credentialRequestEntity != null && isAuthorizedForApproveCredentialRequests(credentialRequestEntity.getCredentialEntity().getCredentialId(), responseUser.getUserId())) {
            credentialRequestEntity.setRespondingUser(responseUser);
            credentialRequestEntity.setApproval(isApproved);
            credentialRequestEntity.setRespondedAt(Timestamp.valueOf(LocalDateTime.now()));
            EmailData emailData = new EmailData();
            emailData.setToMail(credentialRequestEntity.getRequestingUser().getEmail());
            emailData.setSubject("PDAccess Credential Request");
            if (isApproved) {
                CredentialParams credentialParams = breakCredential(credentialRequestEntity.getCredentialEntity().getCredentialId(), "Credential Request Approved", ipAddress, false);
                emailData.setText(String.format("PDAccess credential request for %s has been approved:\n\n%s", credentialRequestEntity.getCredentialEntity().getUsername(), credentialParams.toString()));
            } else {
                emailData.setText(String.format("PDAccess credential request for %s has been rejected", credentialRequestEntity.getCredentialEntity().getUsername()));
            }
            sendEmailService.pushEmailRequest(emailData);

            return credentialRequestRepository.save(credentialRequestEntity);
        }

        return null;
    }

    @Override
    public List<CredentialEntity> getManagedCredentials() {
        return credentialRepository.findManagedCredentialsByAccount();
    }

}