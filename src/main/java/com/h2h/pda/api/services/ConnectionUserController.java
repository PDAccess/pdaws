package com.h2h.pda.api.services;

import com.h2h.pda.entity.*;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.pojo.*;
import com.h2h.pda.pojo.service.ServiceCredentials;
import com.h2h.pda.repository.ConnectionUserRepository;
import com.h2h.pda.repository.SessionRepository;
import com.h2h.pda.repository.TenantRepository;
import com.h2h.pda.service.api.*;
import com.h2h.pda.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.h2h.pda.pojo.Credential.SECRET_INVENTORY;

public class ConnectionUserController extends BaseServiceController {

    private static final Logger log = LoggerFactory.getLogger(ConnectionUserController.class);

    @Value("${credentialManager.endpoint}")
    private String credentialManagerEndpoint;

    @Autowired
    UsersOps usersOps;

    @Autowired
    @Deprecated
    SessionRepository sessionRepository;

    @Autowired
    @Deprecated
    TenantRepository tenantRepository;


    @Autowired
    @Deprecated
    ConnectionUserRepository connectionUserRepo;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CredentialManager credentialManager;

    @Autowired
    VaultService vaultService;

    @Autowired
    SystemSettings systemSettings;

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    GroupOps groupOps;

    @GetMapping(path = "account/{account_id}")
    public ResponseEntity<ConnectionUserWrapper> getConnectionUserEntity(@PathVariable("account_id") int accountId) {
        Optional<ConnectionUserEntity> optionalConnectionUserEntity = connectionUserRepo.findById(accountId);
        return optionalConnectionUserEntity.map(connectionUserEntity -> ResponseEntity.ok(new ConnectionUserWrapper().wrap(connectionUserEntity))).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @GetMapping(path = "service/account/{service_id}")
    public ResponseEntity<LocalAccount[]> getLocalAccounts(@PathVariable("service_id") String serviceId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        Optional<ServiceEntity> optionalServiceEntity = serviceOps.byId(serviceId);
        if (!optionalServiceEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<ConnectionUserEntity> connectionUserEntities = connectionUserRepo.findByServiceIdAndRole(serviceId, true);
        if (connectionUserEntities.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ConnectionUserEntity connectionUserEntity = connectionUserEntities.get(0);

        VaultTemplate template = vaultService.newTemplate(details.getToken());

        ServiceEntity service = optionalServiceEntity.get();

        VerifyRequest vr = new VerifyRequest();

        try {
            VaultResponseSupport<Credential> vaultResponseSupport =
                    template.read(SECRET_INVENTORY + serviceId + "/" + connectionUserEntity.getId(), Credential.class);
            Credential credentials = vaultResponseSupport.getData();
            if (credentials == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            vr.setUsername(credentials.getUsername());
            vr.setPassword(credentials.getPassword());
            vr.setHostname(service.getIpAddress());
            vr.setPort(service.getPort());

            vr.setProto(service.getServiceTypeId().getTypeName());

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<CredentialManagerAccountResponse> response = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/account"), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerAccountResponse.class);
        CredentialManagerAccountResponse accountResponse = response.getBody();
        if (response.getStatusCode() != HttpStatus.OK || accountResponse == null || !accountResponse.isResult()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(accountResponse.getAccounts(), HttpStatus.OK);

    }

    // TODO: Entity Fix
    @GetMapping(path = "service/account/assign/{connection_id}")
    public ResponseEntity<List<UserEntity>> getAssignmentUsers(@PathVariable("connection_id") Integer connectionId) {

        Optional<ConnectionUserEntity> optionalConnectionUserEntity = connectionUserRepo.findById(connectionId);
        if (!optionalConnectionUserEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ConnectionUserEntity connectionUserEntity = optionalConnectionUserEntity.get();

        List<UserEntity> userEntities = new ArrayList<>();

        return new ResponseEntity<>(userEntities, HttpStatus.OK);

    }

    @PostMapping(path = "service/account/assign/{connection_id}")
    public ResponseEntity<Void> setConnectionUsers(@PathVariable("connection_id") Integer connectionId, @RequestBody AssignmentUserParams params) {

        Optional<ConnectionUserEntity> optionalConnectionUserEntity = connectionUserRepo.findById(connectionId);
        if (!optionalConnectionUserEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ConnectionUserEntity connectionUserEntity = optionalConnectionUserEntity.get();
        ServiceEntity service = connectionUserEntity.getServiceEntity();


        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/credentials/{serviceId}")
    public ResponseEntity<BreakData> getServiceCredentials(@PathVariable String serviceId) {
        UserEntity user = usersOps.securedUser();

        Optional<ServiceEntity> optionalServiceEntity = serviceOps.byId(serviceId);
        if (!optionalServiceEntity.isPresent()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ServiceEntity service = optionalServiceEntity.get();
        ConnectionUserEntity connectionUserEntity = new ConnectionUserEntity();

        if (connectionUserEntity == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        try {
            VaultResponseSupport<Credential> vaultResponseSupport =
                    vaultService.read(SECRET_INVENTORY + serviceId + "/" + connectionUserEntity.getId(), Credential.class);
            Credential credentials = vaultResponseSupport.getData();
            if (credentials == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            BreakData breakData = new BreakData();
            breakData.setDbname(service.getDbName());
            breakData.setIpaddress(service.getIpAddress());
            breakData.setPort(service.getPort());
            breakData.setUsername(credentials.getUsername());
            breakData.setPassword(credentials.getPassword());
            breakData.setKey(credentials.getKey());
            breakData.setPassphrase(credentials.getPassphrase());
            breakData.setPpKey(credentials.getPpKey());

            return new ResponseEntity<>(breakData, HttpStatus.OK);

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/change-credentials/{service_id}")
    public ResponseEntity<Void> setServiceCredentials(@RequestBody ServiceCredentials credentials, @PathVariable("service_id") String serviceId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        Optional<ServiceEntity> optionalServiceEntity = serviceOps.byId(serviceId);
        if (!optionalServiceEntity.isPresent()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        ServiceEntity service = optionalServiceEntity.get();

        VaultTemplate template = vaultService.newTemplate(details.getToken());

        //Add users
        for (Credential credential : credentials.getAddedUsers()) {
            ConnectionUserEntity connectionUserEntity = new ConnectionUserEntity();
            connectionUserEntity.setUsername(credential.getUsername());
            connectionUserEntity.setServiceEntity(service);
            connectionUserEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            connectionUserEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            connectionUserEntity = connectionUserRepo.save(connectionUserEntity);

            try {
                template.write(SECRET_INVENTORY + serviceId + "/" + connectionUserEntity.getId(), (Object) credential);
            } catch (VaultException ve) {
                log.error(VAULT_ERROR, ve.getMessage());
            }
        }

        //Edit users
        for (Credential credential : credentials.getEditedUsers()) {

            Optional<ConnectionUserEntity> optionalEntity = connectionUserRepo.findById(credential.getId());
            if (optionalEntity.isPresent()) {
                try {
                    template.write(SECRET_INVENTORY + serviceId + "/" + credential.getId(), (Object) credential);
                } catch (VaultException ve) {
                    log.error(VAULT_ERROR, ve.getMessage());
                }
            }
        }

        //Deleted users
        for (Credential credential : credentials.getDeletedUsers()) {
            Optional<ConnectionUserEntity> optionalEntity = connectionUserRepo.findById(credential.getId());
            if (optionalEntity.isPresent()) {
                ConnectionUserEntity connectionUserEntity = optionalEntity.get();
                connectionUserRepo.delete(connectionUserEntity);

                try {
                    template.delete(SECRET_INVENTORY + serviceId + "/" + credential.getId());
                } catch (VaultException ve) {
                    log.error(VAULT_ERROR, ve.getMessage());
                }
            }
        }

        //Admin user
        Credential adminCredential = credentials.getAdminCredential();
        if (adminCredential != null) {
            Optional<ConnectionUserEntity> optionalEntity = connectionUserRepo.findById(adminCredential.getId());
            if (optionalEntity.isPresent() && optionalEntity.get().getAdmin()) {
                try {
                    template.write(SECRET_INVENTORY + serviceId + "/" + adminCredential.getId(), (Object) adminCredential);
                } catch (VaultException ve) {
                    log.error(VAULT_ERROR, ve.getMessage());
                }
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/connection-users/{service_id}")
    public ResponseEntity<List<ConnectionUserWrapper>> getConnectionUsers(@PathVariable("service_id") String serviceId) {

        List<ConnectionUserEntity> users = connectionUserRepo.findByServiceId(serviceId);
        List<ConnectionUserWrapper> connectionUsers = new ArrayList<>();
        for (ConnectionUserEntity user : users) {
            ConnectionUserWrapper wrapper = new ConnectionUserWrapper();
            wrapper.setId(user.getId());
            wrapper.setAdmin(user.getAdmin());
            wrapper.setUsername(user.getUsername());
            wrapper.setDeletable(true);
            wrapper.setCreatedAt(user.getCreatedAt());
            wrapper.setUpdatedAt(user.getUpdatedAt());
            wrapper.setCredentials(user.getCredentialEntities());
            connectionUsers.add(wrapper);
        }
        return new ResponseEntity<>(connectionUsers, HttpStatus.OK);
    }

    @PostMapping(path = "/connection-users/{service_id}")
    public ResponseEntity<String> addConnectionUser(@RequestParam String username, @PathVariable("service_id") String serviceId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        List<ConnectionUserEntity> connectionUserEntities = connectionUserRepo.findByUsernameAndService(username, serviceId);
        if (!connectionUserEntities.isEmpty()) {
            return ResponseEntity.badRequest().body("Connection Users List is Empty");
        }

        Optional<ServiceEntity> optionalServiceEntity = serviceOps.byId(serviceId);
        if (!optionalServiceEntity.isPresent()) {
            return ResponseEntity.badRequest().body("Service not found");
        }

        ServiceEntity service = optionalServiceEntity.get();

        ConnectionUserEntity connectionUserEntity = new ConnectionUserEntity();
        connectionUserEntity.setUsername(username);
        connectionUserEntity.setServiceEntity(service);
        connectionUserEntity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        connectionUserEntity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        connectionUserEntity = connectionUserRepo.save(connectionUserEntity);

        int passwordLength = 16;
        final StringBuilder randomString = new StringBuilder("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");

        if (systemSettings.hasTag("default_password_length")) {
            passwordLength = Integer.parseInt(systemSettings.tagValue("default_password_length").get());
        }

        if (systemSettings.hasTag("default_password_chars")) {
            randomString.delete(0, randomString.length());
            randomString.append(systemSettings.tagValue("default_password_chars").get());
        }

        StringBuilder password = new StringBuilder(passwordLength);
        IntStream.range(0, passwordLength).forEach(i ->
                password.append(randomString.charAt(new SecureRandom().nextInt(randomString.length()))));


        Credential credential = new Credential();
        credential.setId(connectionUserEntity.getId());
        credential.setUsername(username);
        credential.setPassword(password.toString());

        VaultTemplate template = vaultService.newTemplate(details.getToken());

        try {
            template.write(SECRET_INVENTORY + service.getInventoryId() + "/" + connectionUserEntity.getId(), (Object) credential);
        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        connectionUserEntities = connectionUserRepo.findByServiceIdAndRole(service.getInventoryId(), true);
        if (connectionUserEntities.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ConnectionUserEntity adminUser = connectionUserEntities.get(0);

        VerifyRequest vr = new VerifyRequest();

        try {
            VaultResponseSupport<Credential> vaultResponseSupport =
                    template.read(SECRET_INVENTORY + service.getInventoryId() + "/" + adminUser.getId(), Credential.class);
            Credential credentials = vaultResponseSupport.getData();
            if (credentials == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            vr.setUsername(credentials.getUsername());
            vr.setPassword(credentials.getPassword());
            vr.setHostname(service.getIpAddress());
            vr.setPort(service.getPort());
            vr.setNewpassword(password.toString());

            vr.setProto(service.getServiceTypeId().getMeta().getPropertyString());

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<CredentialManagerAccountResponse> managerAccountResponse = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/account"), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerAccountResponse.class);
        CredentialManagerAccountResponse accountResponse = managerAccountResponse.getBody();
        if (managerAccountResponse.getStatusCode() != HttpStatus.OK || accountResponse == null || !accountResponse.isResult()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LocalAccount[] accounts = accountResponse.getAccounts();

        boolean hasAccount = false;
        for (LocalAccount account : accounts) {
            if (account.getUsername().equals(username)) {
                hasAccount = true;
                break;
            }
        }

        ResponseEntity<CredentialManagerResponse> response = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, hasAccount ? "/changepass/" + connectionUserEntity.getUsername() : "/account/" + connectionUserEntity.getUsername()), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerResponse.class);
        CredentialManagerResponse managerResponse = response.getBody();
        if (response.getStatusCode() != HttpStatus.OK || managerResponse == null || !managerResponse.isResult()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(path = "/connection-users/{account_id}")
    public ResponseEntity<Void> deleteConnectionUser(@PathVariable("account_id") Integer accountId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        Optional<ConnectionUserEntity> optionalConnectionUserEntity = connectionUserRepo.findById(accountId);
        if (!optionalConnectionUserEntity.isPresent() || optionalConnectionUserEntity.get().getServiceEntity() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ConnectionUserEntity connectionUserEntity = optionalConnectionUserEntity.get();

        Optional<ServiceEntity> optionalServiceEntity = serviceOps.byId(connectionUserEntity.getServiceEntity().getInventoryId());
        if (!optionalServiceEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ServiceEntity service = optionalServiceEntity.get();

        VaultTemplate template = vaultService.newTemplate(details.getToken());

        /*
        try {
            template.delete(SECRET_INVENTORY + service.getInventoryId() + "/" + connectionUserEntity.getId());
        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
         */

        connectionUserRepo.delete(connectionUserEntity);

        /*
        List<ConnectionUserEntity> connectionUserEntities = connectionUserRepo.findByServiceIdAndRole(service.getInventoryId(), true);
        if (connectionUserEntities.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ConnectionUserEntity adminUser = connectionUserEntities.get(0);

        VerifyRequest vr = new VerifyRequest();

        try {
            VaultResponseSupport<Credential> vaultResponseSupport =
                    template.read(SECRET_INVENTORY + service.getInventoryId() + "/" + adminUser.getId(), Credential.class);
            Credential credentials = vaultResponseSupport.getData();
            if (credentials == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            vr.setUsername(credentials.getUsername());
            vr.setPassword(credentials.getPassword());
            vr.setHostname(service.getIpAddress());
            vr.setPort(service.getPort());

            vr.setProto(service.getServiceTypeId().getTypeName());

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<CredentialManagerResponse> response = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/account/" + connectionUserEntity.getUsername()), HttpMethod.DELETE, new HttpEntity<>(vr), CredentialManagerResponse.class);
        CredentialManagerResponse managerResponse = response.getBody();
        if (response.getStatusCode() != HttpStatus.OK || managerResponse == null || !managerResponse.isResult()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        */

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/connection-users/break")
    public ResponseEntity<BreakData> breakConnectionUser(@RequestParam("account_id") Integer accountId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        Optional<ConnectionUserEntity> optionalConnectionUserEntity = connectionUserRepo.findById(accountId);
        if (!optionalConnectionUserEntity.isPresent() || optionalConnectionUserEntity.get().getServiceEntity() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ConnectionUserEntity connectionUserEntity = optionalConnectionUserEntity.get();

        ServiceEntity service = connectionUserEntity.getServiceEntity();

        VaultTemplate template = vaultService.newTemplate(details.getToken());

        BreakData breakData = new BreakData();

        try {
            VaultResponseSupport<Credential> vaultResponseSupport =
                    template.read(SECRET_INVENTORY + service.getInventoryId() + "/" + connectionUserEntity.getId(), Credential.class);
            Credential credentials = vaultResponseSupport.getData();
            if (credentials == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            breakData.setDbname(service.getDbName());
            breakData.setIpaddress(service.getIpAddress());
            breakData.setPort(service.getPort());
            breakData.setUsername(credentials.getUsername());
            breakData.setPassword(credentials.getPassword());
            breakData.setKey(credentials.getKey());
            breakData.setPassphrase(credentials.getPassphrase());
            breakData.setPpKey(credentials.getPpKey());

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(breakData, HttpStatus.OK);
    }

    @PostMapping("/account/manage")
    public ResponseEntity<Void> manageAccount(@RequestBody ManageAccountParams manageAccountParams, HttpServletRequest request) {
        Optional<ConnectionUserEntity> optionalConnectionUserEntity = connectionUserRepository.findById(manageAccountParams.getAccountId());
        CredentialEntity credentialEntity = credentialManager.getCredential(manageAccountParams.getCredentialId());
        if (!optionalConnectionUserEntity.isPresent() || credentialEntity == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        ConnectionUserEntity connectionUserEntity = optionalConnectionUserEntity.get();
        GroupsEntity groupsEntity = credentialEntity.getGroup();

        CredentialEntity newCredentialEntity = new CredentialEntity();
        newCredentialEntity.setCredentialId(UUID.randomUUID().toString());
        newCredentialEntity.setWhoCreate(usersOps.securedUser());
        newCredentialEntity.setConnectionUser(connectionUserEntity);
        newCredentialEntity.setUsername(credentialEntity.getUsername());
        newCredentialEntity.setGroup(credentialEntity.getGroup());

        CredentialParams newCredentialParams = new CredentialParams();
        newCredentialParams.setCredentialId(newCredentialEntity.getCredentialId());
        newCredentialParams.setUsername(newCredentialEntity.getUsername());
        newCredentialParams.setGroupId(groupsEntity.getGroupId());

        CredentialParams credentialParams = credentialManager.breakCredential(credentialEntity.getCredentialId(), "Account Manage", RequestUtil.remoteAddress(request), false);
        if (credentialParams != null) {
            newCredentialParams.setPassword(credentialParams.getPassword());
            newCredentialParams.setKey(credentialParams.getKey());
            newCredentialParams.setAccount(credentialParams.getAccount());
            newCredentialParams.setKeyValues(credentialParams.getKeyValues());
            newCredentialParams.setServices(credentialParams.getServices());
        }

        credentialManager.newCredential(newCredentialEntity, newCredentialParams);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
