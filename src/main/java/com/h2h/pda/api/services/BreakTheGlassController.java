package com.h2h.pda.api.services;

import com.h2h.pda.entity.*;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.pojo.*;
import com.h2h.pda.pojo.service.ServiceProperties;
import com.h2h.pda.repository.BreakTheGlassRepository;
import com.h2h.pda.repository.ConnectionUserRepository;
import com.h2h.pda.service.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.h2h.pda.pojo.Credential.SECRET_INVENTORY;

@RestController
@RequestMapping("/api/v1/break")
public class BreakTheGlassController {

    private static final Logger log = LoggerFactory.getLogger(BreakTheGlassController.class);
    private static final String VAULT_ERROR = "Vault error: {}";
    private static final String CREATED_AT = "createdAt";

    @Autowired
    @Deprecated
    BreakTheGlassRepository breaktheglassRepository;

    @Autowired
    ConnectionUserRepository connectionUserRepo;

    @Autowired
    SystemSettings systemSettings;

    @Autowired
    VaultService vaultService;

    @Autowired
    CredentialManager credentialManager;

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    UsersOps usersOps;

    @GetMapping(path = "/service/{serviceId}")
    public ResponseEntity<Boolean> breakTheGlassStatus(@PathVariable("serviceId") String serviceId) {
        UserEntity user = usersOps.securedUser();
        Optional<ServiceEntity> serviceEntity = serviceOps.byId(serviceId);

        ConnectionUserEntity connectionUserEntity = getConnectionUserEntity(serviceId, user.getUserId());

        List<BreakTheGlassEntity> breakTheGlassEntities = Collections.emptyList();

        if (serviceEntity.isPresent()) {
            ServiceEntity service = serviceEntity.get();
            if (service.getIpAddress() == null || service.getIpAddress().isEmpty()) {
                breakTheGlassEntities = breaktheglassRepository.findByServiceAndCheck(serviceId);
            } else if (connectionUserEntity == null) {
                return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
            } else {
                breakTheGlassEntities = breaktheglassRepository.findByServiceAndUserAndCheck(connectionUserEntity.getId(), serviceId);
            }
        }
        return breakTheGlassEntities.isEmpty() ? new ResponseEntity<>(false, HttpStatus.OK) : new ResponseEntity<>(breakTheGlassEntities.get(0).getUserId().equals(user.getUserId()), HttpStatus.OK);
    }

    @PostMapping(path = "/service/{serviceId}")
    public ResponseEntity<BreakData> breakTheGlass(@RequestBody(required = false) String reason, @PathVariable String serviceId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();
        UserEntity user = usersOps.securedUser();
        Optional<ServiceEntity> serviceEntity = serviceOps.byId(serviceId);

        if (!serviceEntity.isPresent())
            return ResponseEntity.notFound().build();
        ServiceEntity service = serviceEntity.get();
        ConnectionUserEntity connectionUserEntity = getConnectionUserEntity(serviceId, user.getUserId());

        if (service.getIpAddress() == null || service.getIpAddress().isEmpty()) {
            connectionUserEntity = null;
        }

        BreakTheGlassEntity breakTheGlass = new BreakTheGlassEntity();
        breakTheGlass.setBreakId(UUID.randomUUID().toString());
        breakTheGlass.setUserId(user.getUserId());
        breakTheGlass.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        breakTheGlass.setServiceid(serviceId);
        breakTheGlass.setServicename(service.getName());
        breakTheGlass.setReason(reason);
        breakTheGlass.setConnectionUserEntity(connectionUserEntity);
        breaktheglassRepository.save(breakTheGlass);

        VaultTemplate template = vaultService.newTemplate(details.getToken());

        try {

            BreakData breakData = new BreakData();
            if (service.getIpAddress() == null || service.getIpAddress().isEmpty()) {
                VaultResponseSupport<Inventory> vaultResponseSupport =
                        template.read(SECRET_INVENTORY + serviceId, Inventory.class);
                Inventory inventory = vaultResponseSupport.getData();
                if (inventory == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

                breakData.setDbname(inventory.getDbname());
                breakData.setIpaddress(inventory.getIpaddress());
                breakData.setPort(Integer.parseInt(inventory.getPort()));
                breakData.setUsername(inventory.getUsername());
                breakData.setPassword(inventory.getPassword());
                breakData.setKey(inventory.getKey());
                breakData.setPassphrase(inventory.getPassphrase());
                breakData.setPpKey(inventory.getPpKey());
            } else {

                VaultResponseSupport<Credential> vaultResponseSupport =
                        template.read(SECRET_INVENTORY + serviceId + "/" + connectionUserEntity.getId(), Credential.class);
                Credential credentials = vaultResponseSupport.getData();
                if (credentials == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

                breakData.setDbname(service.getDbName());
                breakData.setIpaddress(service.getIpAddress());
                breakData.setPort(service.getPort());
                breakData.setUsername(credentials.getUsername());
                breakData.setPassword(credentials.getPassword());
                breakData.setKey(credentials.getKey());
                breakData.setPassphrase(credentials.getPassphrase());
                breakData.setPpKey(credentials.getPpKey());
            }

            return new ResponseEntity<>(breakData, HttpStatus.OK);

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Deprecated
    @PostMapping(path = "/checkout/{service_id}")
    public ResponseEntity<Void> checkBreakTheGlass(@PathVariable("service_id") String serviceId) {
        UserEntity user = usersOps.securedUser();

        Optional<ServiceEntity> optionalServiceEntity = serviceOps.byId(serviceId);

        if (!optionalServiceEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ServiceEntity serviceEntity = optionalServiceEntity.get();

        ConnectionUserEntity connectionUserEntity = getConnectionUserEntity(serviceId, user.getUserId());

        List<BreakTheGlassEntity> breakTheGlassEntities;
        if (serviceEntity.getIpAddress() == null || serviceEntity.getIpAddress().isEmpty()) {
            breakTheGlassEntities = breaktheglassRepository.findByServiceAndCheck(serviceId);
        } else {
            breakTheGlassEntities = breaktheglassRepository.findByServiceAndUserAndCheck(connectionUserEntity.getId(), serviceId);
        }

        for (BreakTheGlassEntity breaktheglassEntity : breakTheGlassEntities) {
            breaktheglassEntity.setChecked(true);
            breaktheglassEntity.setCheckedTime(new Timestamp(System.currentTimeMillis()));
            breaktheglassRepository.save(breaktheglassEntity);
        }

        CredentialManagerWrapper data = new CredentialManagerWrapper();
        data.setIpAddress(serviceEntity.getIpAddress());
        data.setPort(serviceEntity.getPort());
        data.setServiceId(serviceId);
        //data.setToken(((TokenDetails) authentication.getDetails()).getToken());
        data.setType(serviceEntity.getServiceTypeId());
        data.setUserId(connectionUserEntity.getId());

        credentialManager.pushChangeRequest(data);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ConnectionUserEntity getConnectionUserEntity(String serviceId, String userId) {
        ConnectionUserEntity connectionUserEntity = new ConnectionUserEntity();

        if (connectionUserEntity == null) {
            String connectionUserSetting = systemSettings.tagValue("default_connection_user").get();
            Optional<ServiceEntity> service = serviceOps.byId(serviceId);
            Set<ServiceProperty> properties = service.get().getProperties();

            Optional<ServiceProperty> serviceProperty =
                    properties.stream().filter(p -> p.getKey() == ServiceProperties.USE_DEFAULT_CONNECTION_USER).findFirst();

            if (connectionUserSetting.equals("true")) {
                if (!serviceProperty.isPresent()) {
                    connectionUserEntity = getDefaultConnectionUser(connectionUserEntity, serviceId);
                } else if (serviceProperty.get().getValue().equals("true")) {
                    connectionUserEntity = getDefaultConnectionUser(connectionUserEntity, serviceId);
                }
            } else {
                if (serviceProperty.isPresent() && serviceProperty.get().getValue().equals("true")) {
                    connectionUserEntity = getDefaultConnectionUser(connectionUserEntity, serviceId);
                }
            }
        }
        return connectionUserEntity;
    }

    private ConnectionUserEntity getDefaultConnectionUser(ConnectionUserEntity connectionUserEntity, String serviceId) {
        List<ConnectionUserEntity> connectionUsers = connectionUserRepo.findByServiceId(serviceId);
        if (!connectionUsers.isEmpty())
            connectionUserEntity = connectionUsers.get(0);
        return connectionUserEntity;
    }

    @PostMapping()
    public ResponseEntity<List<BreakTheGlassParams>> getBreakTheGlass(@RequestBody Pagination pagination) {
        PageRequest req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("checkedTime").descending());
        if (pagination.getSort() != null) {
            if (pagination.getSort().equals("userdesc")) {
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("userEntity.username").descending());

            } else if (pagination.getSort().equals("created")) {
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("checkedTime"));

            } else if (pagination.getSort().equals("user")) {
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("userEntity.username"));

            }
        }
        List<String> usersFilter = pagination.getUsersFilter();
        List<String> servicesFilter = pagination.getServicesFilter();

        Specification<BreakTheGlassEntity> where = null;

        if (usersFilter != null && !usersFilter.isEmpty()) {
            where = Specification.where(BreakTheGlassRepository.QueryFilter.findByUsers(usersFilter));
        }

        if (servicesFilter != null && !servicesFilter.isEmpty()) {
            Specification<BreakTheGlassEntity> filterServices = Specification.where(Specification.where(BreakTheGlassRepository.QueryFilter.findByServices(servicesFilter)));
            where = where == null ? filterServices : where.and(filterServices);
        }

        List<BreakTheGlassEntity> list = breaktheglassRepository.findAll(where, req).getContent();
        return ResponseEntity.ok(list.stream().map(e -> new BreakTheGlassParams().wrap(e)).collect(Collectors.toList()));
    }
}