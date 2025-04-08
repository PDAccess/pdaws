package com.h2h.pda.api.services;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.Credential;
import com.h2h.pda.pojo.CredentialParams;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;
import com.h2h.pda.repository.ConnectionUserRepository;
import com.h2h.pda.repository.SessionRepository;
import com.h2h.pda.service.api.CredentialManager;
import com.h2h.pda.service.api.ServiceOps;
import com.h2h.pda.service.api.UsersOps;
import com.h2h.pda.service.api.VaultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseServiceController {

    private static final Logger log = LoggerFactory.getLogger(BaseServiceController.class);

    static final String VAULT_ERROR = "Vault error: {}";
    static final String CREATED_AT = "createdAt";
    static final String OPERATING_SYS_ID = "operatingSystemId";
    static final String SERVICE_TYPE_ID = "serviceTypeId";
    static final String SECURITY_KEY = "Q7ujw9N4BxZuNudLjsrXQ6EvX79iUBi7";
    static final String KEY_SERVER_URL = "http://ppk-converter/keyconvert.sh";
    static final String ADMIN_ID = "123";
    static final String CM_MS_URL = "%s/%s";
    static final String CREATE = "create";
    static final String OS_DESC = "os-desc";
    static final String OP_SERVICE = "opservice";
    static final String OP_SERVICE_DESC = "opservice-desc";
    static final String NAME_DESC = "name-desc";
    static final String NAME = "name";

    @Autowired
    ConnectionUserRepository connectionUserRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    VaultService vaultService;

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    CredentialManager credentialManager;

    @Autowired
    UsersOps usersOps;

    ResponseEntity<List<ServiceEntityWrapper>> getWrapperList(List<ServiceEntity> serviceEntities) {
        List<ServiceEntityWrapper> serviceEntityWrappers = new ArrayList<>();
        for (ServiceEntity serviceEntity : serviceEntities) {
            getServiceEntityWrapper(serviceEntityWrappers, serviceEntity);
        }

        return new ResponseEntity<>(serviceEntityWrappers, HttpStatus.OK);
    }

    void getServiceEntityWrapper(List<ServiceEntityWrapper> serviceEntityWrappers, ServiceEntity serviceEntity) {
        if (serviceEntity == null) {
            return;
        }
        ServiceEntityWrapper serviceEntityWrapper = new ServiceEntityWrapper(serviceEntity);
        Optional<UserEntity> entity = usersOps.byId(serviceEntity.getWhoCreate());
        entity.ifPresent(userEntity -> serviceEntityWrapper.setServiceUser(userEntity.getUsername()));
        List<SessionEntity> sessionList = sessionRepository.findByLastSession(serviceEntity.getInventoryId());
        if (!sessionList.isEmpty()) {
            SessionEntity session = sessionList.get(0);
            serviceEntityWrapper.setLastSessionStart(session.getStartTime());
            serviceEntityWrapper.setLastSessionEnd(session.getEndTime());
        }
        serviceEntityWrappers.add(serviceEntityWrapper);
    }

    void saveCredential(String serviceId, Credential credential, String connectionUser, ServiceEntity serviceEntity, GroupsEntity groupsEntity, Boolean isAdmin) {
        // mode this logic to Cmanager
//        ConnectionUserEntity connectionUserEntity = new ConnectionUserEntity();
//        connectionUserEntity.setUsername(credential.getUsername());
//        connectionUserEntity.setServiceEntity(serviceEntity);
//        connectionUserEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
//        connectionUserEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
//        connectionUserEntity.setAdmin(isAdmin);
//        connectionUserEntity = connectionUserRepository.save(connectionUserEntity);
//
//        CredentialEntity credentialEntity = new CredentialEntity();
//        credentialEntity.setWhoCreate(usersOps.securedUser());
//        credentialEntity.setGroup(groupsEntity);
//        credentialEntity.setConnectionUser(connectionUserEntity);
//        credentialEntity.setUsername(connectionUserEntity.getUsername());
//        credentialEntity.setCredentialId(UUID.randomUUID().toString());
//
//        CredentialParams credentialParams = new CredentialParams();
//        credentialParams.setCredentialId(credentialEntity.getCredentialId());
//        credentialParams.setUsername(credential.getUsername());
//        credentialParams.setPassword(credential.getPassword());
//        credentialParams.setKey(credential.getKey());
//        credentialParams.setGroupId(groupsEntity.getGroupId());
//
//        credentialManager.newCredential(credentialEntity, credentialParams);
    }

    ResponseEntity<Void> recordChange(String serviceId, boolean status) {
        Optional<ServiceEntity> serviceEntity = serviceOps.byId(serviceId);
        if (serviceEntity.isPresent()) {
            if (serviceEntity.get().getDeletedAt() != null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            ServiceEntity service = serviceEntity.get();
            service.setVideoRecord(status);
            serviceOps.createOrUpdate(service);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
