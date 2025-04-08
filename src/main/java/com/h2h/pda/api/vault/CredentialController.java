package com.h2h.pda.api.vault;


import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.*;
import com.h2h.pda.pojo.group.GroupRole;
import com.h2h.pda.pojo.permission.Permissions;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;
import com.h2h.pda.pojo.vault.CredentialCounter;
import com.h2h.pda.pojo.vault.CredentialRequestCounter;
import com.h2h.pda.pojo.vault.CredentialRequestParams;
import com.h2h.pda.repository.ConnectionUserRepository;
import com.h2h.pda.service.api.*;
import com.h2h.pda.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/credentials")
public class CredentialController {

    @Autowired
    UsersOps usersOps;

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    GroupOps groupOps;

    @Autowired
    CredentialManager credentialManager;

    @Autowired
    ConnectionUserRepository connectionUserRepo;

    @Autowired
    PermissionService permissionService;

    @Autowired
    VaultService vaultService;

    @Autowired
    VaultOps vaultOps;

    static final String CREATE = "create";
    static final String CREATE_DESC = "create-desc";
    static final String NAME = "name";
    static final String NAME_DESC = "name-desc";

    @GetMapping
    public ResponseEntity<List<CredentialsDto>> getCredentials() {
        List<CredentialsDto> credentials = new ArrayList<>();
        UserEntity user = usersOps.securedUser();

        List<ServiceEntity> serviceEntities = serviceOps.collectEffectiveService(user.getUserId());
        List<ServiceEntityWrapper> serviceEntityWrappers = getWrapperList(serviceEntities);

        for (ServiceEntityWrapper service : serviceEntityWrappers) {
            List<ConnectionUserWrapper> connectionUsers = getConnectionUsers(service.getInventoryId());
            for (ConnectionUserWrapper connectionUser : connectionUsers) {
                credentials.add(new CredentialsDto(connectionUser.getId(), connectionUser.getUsername(), service,
                        connectionUser.getCreatedAt(), connectionUser.getUpdatedAt(), connectionUser.getAdmin()));
            }
        }
        return new ResponseEntity<>(credentials, HttpStatus.OK);
    }

    @PostMapping("vault/list/{sort}")
    public ResponseEntity<List<VaultCredentialParamsWrapper>> getVaultCredentials(@PathVariable String sort, @RequestBody Pagination pagination) {

        PageRequest req;
        switch (sort) {
            case CREATE:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("createdAt"));
                break;
            case NAME:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("username"));
                break;
            case NAME_DESC:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("username").descending());
                break;
            default:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("createdAt").descending());
                break;
        }

        List<VaultCredentialParams> credentials = new ArrayList<>();
        if (pagination.getCategory() == null || "list".equals(pagination.getCategory())) {
            credentials = credentialManager.getCredentialsByUserId(usersOps.securedUser().getUserId(), pagination.getFilter(), req);
        } else if ("yours".equals(pagination.getCategory())) {
            List<GroupsEntity> groupsEntities = groupOps.searchBy(usersOps.securedUser().getUserId(), GroupRole.ADMIN);
            List<String> groupIds = groupsEntities.stream().map(GroupsEntity::getGroupId).collect(Collectors.toList());
            credentials = credentialManager.getCredentialsByGroupIds(usersOps.securedUser().getUserId(), groupIds, pagination.getFilter(), req);
        }

        List<VaultCredentialParamsWrapper> paramsWrappers = new ArrayList<>();
        for (VaultCredentialParams vaultCredentialParams : credentials) {
            VaultCredentialParamsWrapper credentialParamsWrapper = new VaultCredentialParamsWrapper(vaultCredentialParams);
            BreakTheGlassEntity breakTheGlassEntity = credentialManager.getLastCredentialBreak(credentialParamsWrapper.getCredentialId());
            AutoCredentialsHistoryEntity autoCredentialsHistoryEntity = credentialManager.getLastCredentialChange(credentialParamsWrapper.getCredentialId());

            if (breakTheGlassEntity != null) {
                credentialParamsWrapper.setLastBreakedAt(breakTheGlassEntity.getCheckedTime());
                credentialParamsWrapper.setCheck(vaultCredentialParams.getCredentialEntity().isCheckStatus() && breakTheGlassEntity.getCheckoutTime() == null);
                credentialParamsWrapper.setCheckout(breakTheGlassEntity.getUserId().equals(usersOps.securedUser().getUserId()));
            }

            if (autoCredentialsHistoryEntity != null) {
                credentialParamsWrapper.setLastChangedAt(autoCredentialsHistoryEntity.getEndAt());
                credentialParamsWrapper.setLastChangeStatus(autoCredentialsHistoryEntity.getResult());
            }


            paramsWrappers.add(credentialParamsWrapper);
        }
        return ResponseEntity.ok(paramsWrappers);
    }

    // TODO: Entity Fix
    @PostMapping("{group_id}")
    public ResponseEntity<List<CredentialDetails>> getCredentials(@PathVariable("group_id") String groupId, @RequestBody Pagination pagination) {
        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("id").ascending());
        List<CredentialEntity> credentialEntities = credentialManager.getCredentials(groupId, pagination.getFilter(), pageRequest);
        return ResponseEntity.ok(credentialEntities.stream().map(ce -> new CredentialDetails().wrap(ce)).collect(Collectors.toList()));
    }

    @PostMapping("manage")
    public ResponseEntity<String> saveManagedCredentials(@RequestBody CredentialParams credentialParams) {

        if (!credentialParams.hasValidParams() && credentialParams.getAccount() == null) {
            return new ResponseEntity<>("Username or Password must not empty", HttpStatus.BAD_REQUEST);
        }

        Optional<ConnectionUserEntity> optionalConnectionUserEntity = connectionUserRepo.findById(credentialParams.getAccount().getId());
        if (!optionalConnectionUserEntity.isPresent()) {
            return new ResponseEntity<>("Managed account not found", HttpStatus.BAD_REQUEST);
        }

        ConnectionUserEntity connectionUserEntity = optionalConnectionUserEntity.get();
        List<GroupsEntity> groupsEntities = new ArrayList<>();
        if (connectionUserEntity.getServiceEntity() != null) {
            groupsEntities = serviceOps.effectiveGroups(connectionUserEntity.getServiceEntity().getInventoryId());
        }

        List<CredentialEntity> credentialEntities = new ArrayList<>();

        for (GroupsEntity groupsEntity : groupsEntities) {
            CredentialEntity credentialEntity = new CredentialEntity();
            credentialEntity.setCredentialId(UUID.randomUUID().toString());
            credentialEntity.setUsername(credentialParams.getUsername());
            credentialEntity.setConnectionUser(credentialParams.getAccount());
            credentialEntity.setGroup(groupsEntity);
            credentialEntity.setWhoCreate(usersOps.securedUser());
            credentialEntity.setCheckStatus(credentialParams.isCheck());
            credentialParams.setCredentialId(credentialEntity.getCredentialId());
            credentialEntity = credentialManager.newCredential(credentialEntity, credentialParams);
            credentialEntities.add(credentialEntity);
        }

        for (String serviceId : credentialParams.getServices()) {
            connectionUserEntity = new ConnectionUserEntity();
            connectionUserEntity.setUsername(credentialParams.getUsername());
            connectionUserEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            connectionUserEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            connectionUserEntity.setCredentialEntities(credentialEntities);
            serviceOps.addLocalAccount(serviceId, connectionUserEntity);
        }

        return ResponseEntity.ok("Credential is successfully saved.");
    }

    @PostMapping
    public ResponseEntity<String> saveCredentials(@RequestBody CredentialParams credentialParams) {

        Optional<GroupsEntity> optionalGroupsEntity = groupOps.byId(credentialParams.getGroupId());
        if (!optionalGroupsEntity.isPresent() || !credentialParams.hasValidParams()) {
            return new ResponseEntity<>("Username or Password must not empty", HttpStatus.BAD_REQUEST);
        }

        CredentialEntity credentialEntity = new CredentialEntity();
        credentialEntity.setCredentialId(UUID.randomUUID().toString());
        credentialEntity.setUsername(credentialParams.getUsername());
        credentialEntity.setConnectionUser(credentialParams.getAccount());
        credentialEntity.setGroup(optionalGroupsEntity.get());
        credentialEntity.setWhoCreate(usersOps.securedUser());
        credentialEntity.setCheckStatus(credentialParams.isCheck());

        credentialParams.setCredentialId(credentialEntity.getCredentialId());

        credentialEntity = credentialManager.newCredential(credentialEntity, credentialParams);
        credentialEntity.getCredentialId();

        for (String serviceId : credentialParams.getServices()) {
            ConnectionUserEntity connectionUserEntity = new ConnectionUserEntity();
            connectionUserEntity.setUsername(credentialEntity.getUsername());
            connectionUserEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            connectionUserEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            connectionUserEntity.setCredentialEntities(Collections.singletonList(credentialEntity));
            serviceOps.addLocalAccount(serviceId, connectionUserEntity);
        }

        return ResponseEntity.ok("Credential is successfully saved.");
    }

    // TODO: Entity Fix
    @GetMapping("{credential_id}")
    public ResponseEntity<CredentialResponse> getCredentials(@PathVariable("credential_id") String credentialId) {
        CredentialEntity credentialEntity = credentialManager.getCredential(credentialId);
        if (credentialEntity == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        if (!permissionService.hasPermission(credentialId, usersOps.securedUser().getUserId(), Permissions.CAN_MANAGE_OTHER_USERS)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        CredentialResponse credentialResponse = new CredentialResponse();
        credentialResponse.wrap(credentialEntity);
        credentialResponse.setPermissionEntity(permissionService.getPermissionByCredentialAndUser(credentialId, usersOps.securedUser().getUserId()));

        BreakTheGlassEntity breakTheGlassEntity = credentialManager.getLastCredentialBreak(credentialEntity.getCredentialId());
        if (breakTheGlassEntity != null) {
            credentialResponse.getCredential().setLastAccessTime(breakTheGlassEntity.getCheckedTime());
        }

        AutoCredentialsHistoryEntity autoCredentialsHistoryEntity = credentialManager.getLastCredentialChange(credentialEntity.getCredentialId());
        if (autoCredentialsHistoryEntity != null) {
            credentialResponse.getCredential().setLastPasswordChangeTime(autoCredentialsHistoryEntity.getEndAt());
            credentialResponse.getCredential().setPasswordChangeStatus(autoCredentialsHistoryEntity.getResult());
        }

        return ResponseEntity.ok(credentialResponse);
    }

    @PostMapping("break")
    public ResponseEntity<CredentialParams> breakCredential(@RequestBody BreakParams breakParams, HttpServletRequest request) {
        if (!permissionService.hasPermission(breakParams.getCredentialId(), usersOps.securedUser().getUserId(), Permissions.CAN_SEE_PASSWORD)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        CredentialParams credentialParams = credentialManager.breakCredential(breakParams.getCredentialId(), breakParams.getReason(), RequestUtil.remoteAddress(request), false);
        if (credentialParams == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(credentialParams);
    }

    @GetMapping("data/{credential_id}")
    public ResponseEntity<BreakData> getCredentialData(@PathVariable("credential_id") String credentialId, HttpServletRequest request) {
        if (!permissionService.hasPermission(credentialId, usersOps.securedUser().getUserId(), Permissions.CAN_CONNECT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        CredentialEntity credentialEntity = credentialManager.getCredential(credentialId);
        CredentialParams credentialParams = credentialManager.breakCredential(credentialId, "Proxy Connection", RequestUtil.remoteAddress(request), false);
        if (credentialEntity == null || credentialEntity.getConnectionUser() == null || credentialParams == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        ServiceEntity serviceEntity = credentialEntity.getConnectionUser().getServiceEntity();

        BreakData breakData = new BreakData();
        breakData.setUsername(credentialParams.getUsername());
        breakData.setPassword(credentialParams.getPassword());
        breakData.setKey(credentialParams.getKey());
        breakData.setIpaddress(serviceEntity.getIpAddress());
        breakData.setPort(serviceEntity.getPort());
        breakData.setDbname(serviceEntity.getDbName());
        breakData.setServiceTypeId(serviceEntity.getServiceTypeId().getIntValue());

        return ResponseEntity.ok(breakData);
    }

    @PutMapping("{credential_id}")
    public ResponseEntity<Void> updateCredential(@RequestBody CredentialUpdateParams params, @PathVariable(name = "credential_id") String credentialId) {
        CredentialEntity credentialEntity = credentialManager.getCredential(credentialId);
        if (credentialEntity == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Optional<GroupsEntity> optionalGroupsEntity = groupOps.byId(params.getGroupId());
        if (!optionalGroupsEntity.isPresent()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ConnectionUserEntity connectionUserEntity = null;
        if (params.getAccountId() != null) {
            Optional<ConnectionUserEntity> optionalConnectionUserEntity = connectionUserRepo.findById(params.getAccountId());
            connectionUserEntity = optionalConnectionUserEntity.orElse(null);
        }

        credentialEntity.setGroup(optionalGroupsEntity.get());
        credentialEntity.setConnectionUser(connectionUserEntity);
        credentialEntity.setCheckStatus(params.isCheck());
        credentialManager.updateCredential(credentialEntity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("{credential_id}")
    public ResponseEntity<Void> deleteCredential(@PathVariable(name = "credential_id") String credentialId) {
        credentialManager.deleteCredential(credentialId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("change-password")
    public ResponseEntity<Void> changeCredentialPassword(@RequestBody CredentialChangePasswordParams changePasswordParams) {

        if (!permissionService.hasPermission(changePasswordParams.getCredentialId(), usersOps.securedUser().getUserId(), Permissions.CAN_CHANGE_PASSWORD)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        CredentialEntity credentialEntity = credentialManager.getCredential(changePasswordParams.getCredentialId());
        if (credentialEntity == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (credentialEntity.getConnectionUser() == null || credentialEntity.getConnectionUser().getServiceEntity() == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        ConnectionUserEntity connectionUserEntity = credentialEntity.getConnectionUser();
        ServiceEntity serviceEntity = connectionUserEntity.getServiceEntity();

        CredentialManagerWrapper data = new CredentialManagerWrapper();
        data.setIpAddress(serviceEntity.getIpAddress());
        data.setPort(serviceEntity.getPort());
        data.setCredentialId(changePasswordParams.getCredentialId());
        data.setUserId(connectionUserEntity.getId());
        data.setServiceId(serviceEntity.getInventoryId());
        data.setType(serviceEntity.getServiceTypeId());
        data.setUserId(connectionUserEntity.getId());
        data.setWhoTriggered(usersOps.securedUser());
        data.setPassword(changePasswordParams.getPassword());

        credentialManager.pushChangeRequest(data);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("change-history/{credentialId}")
    public ResponseEntity<List<AutoCredentialsHistoryEntity>> getChangeHistory(@PathVariable String credentialId, @RequestBody Pagination pagination) {
        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("startAt").descending());
        return ResponseEntity.ok(credentialManager.getChangeHistory(credentialId, pageRequest));
    }

    @PostMapping("checkout/{credentialId}")
    public ResponseEntity<List<AutoCredentialsHistoryEntity>> checkoutCredential(@PathVariable String credentialId) {
        CredentialEntity credentialEntity = credentialManager.getCredential(credentialId);
        if (credentialEntity == null || !credentialEntity.isCheckStatus()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        credentialManager.checkoutCredential(credentialId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private List<ServiceEntityWrapper> getWrapperList(List<ServiceEntity> serviceEntities) {
        List<ServiceEntityWrapper> serviceEntityWrappers = new ArrayList<>();
        for (ServiceEntity serviceEntity : serviceEntities) {
            serviceEntityWrappers.add(new ServiceEntityWrapper(serviceEntity));
        }
        return serviceEntityWrappers;
    }

    public List<ConnectionUserWrapper> getConnectionUsers(String serviceId) {
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
            connectionUsers.add(wrapper);
        }
        return connectionUsers;
    }

    @GetMapping("group/admin")
    public ResponseEntity<List<CredentialEntity>> getCredentialOfAdminGroups() {
        List<GroupsEntity> groupsEntities = groupOps.searchBy(usersOps.securedUser().getUserId(), GroupRole.ADMIN);
        List<CredentialEntity> credentialEntities = new ArrayList<>();
        for (GroupsEntity groupsEntity : groupsEntities) {
            List<CredentialEntity> credentialEntityList = credentialManager.getAllCredentials(groupsEntity.getGroupId());
            for (CredentialEntity credentialEntity : credentialEntityList) {
                if (!credentialEntities.contains(credentialEntity)) {
                    credentialEntities.add(credentialEntity);
                }
            }
        }
        return ResponseEntity.ok(credentialEntities);
    }

    @PostMapping(path = "/data")
    public ResponseEntity<ConnectionResponse> getCredentialData(@RequestBody ConnectionParamIds paramIds, HttpServletRequest request) {
        if (permissionService.hasPermission(paramIds.getCredentialId(), usersOps.securedUser().getUserId(), Permissions.CAN_CONNECT)) {
            Optional<ServiceEntity> optionalServiceEntity = serviceOps.byId(paramIds.getServiceId());
            CredentialEntity credentialEntity = credentialManager.getCredential(paramIds.getCredentialId());
            if (optionalServiceEntity.isPresent() && credentialEntity != null) {
                CredentialParams credentialParams = credentialManager.breakCredential(credentialEntity.getCredentialId(), "Connection via Client Connector", RequestUtil.remoteAddress(request), false);
                if (credentialParams != null) {
                    ServiceEntity serviceEntity = optionalServiceEntity.get();
                    ConnectionResponse connectionResponse = new ConnectionResponse();
                    connectionResponse.setId(serviceEntity.getInventoryId());
                    connectionResponse.setDbName(serviceEntity.getDbName());
                    connectionResponse.setIpAddress(serviceEntity.getIpAddress());
                    connectionResponse.setPort(serviceEntity.getPort());
                    connectionResponse.setUsername(credentialParams.getUsername());
                    connectionResponse.setPassword(credentialParams.getPassword());
                    connectionResponse.setKey(credentialParams.getKey());

                    return new ResponseEntity<>(connectionResponse, HttpStatus.OK);
                }
            }

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/counters")
    public ResponseEntity<CredentialCounter> vaultCounters() {
        UserEntity securedUser = usersOps.securedUser();
        CredentialCounter credentialCounter = credentialManager.credentialCounters(securedUser.getUserId());
        return ResponseEntity.ok(credentialCounter);
    }

    @GetMapping("/list/group/{groupId}")
    public ResponseEntity<List<CredentialDetails>> getCredentialsByUserAndGroup(@PathVariable("groupId") String groupId) {
        List<CredentialEntity> credentialEntities = credentialManager.getCredentialsByUserIdAndGroupId(usersOps.securedUser().getUserId(), groupId);
        return ResponseEntity.ok(credentialEntities.stream().map(ce -> new CredentialDetails().wrap(ce)).collect(Collectors.toList()));
    }

    @PostMapping("/request/{credentialId}")
    public ResponseEntity<Void> requestCredential(@PathVariable("credentialId") String credentialId) {
        CredentialEntity credentialEntity = credentialManager.getCredential(credentialId);
        if (credentialEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        credentialManager.requestCredential(credentialId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/request/counters/{credentialId}")
    public ResponseEntity<CredentialRequestCounter> requestCredentialCounters(@PathVariable("credentialId") String credentialId) {
        CredentialEntity credentialEntity = credentialManager.getCredential(credentialId);
        if (credentialEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CredentialRequestCounter credentialRequestCounter = credentialManager.credentialRequestCounters(credentialId);
        return ResponseEntity.ok(credentialRequestCounter);
    }

    @PostMapping("/response/{requestId}")
    public ResponseEntity<Void> responseCredentialRequest(@PathVariable("requestId") int requestId, @RequestBody CredentialRequestResponseParams responseParams, HttpServletRequest request) {
        CredentialEntity credentialEntity = credentialManager.getCredential(responseParams.getCredentialId());
        CredentialRequestEntity credentialRequestEntity = credentialManager.getCredentialRequestById(requestId);
        if (credentialEntity == null && credentialRequestEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        credentialManager.responseCredentialRequest(requestId, RequestUtil.remoteAddress(request), responseParams.isApproved());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/requests/{tag}/{credentialId}")
    public ResponseEntity<List<CredentialRequestParams>> getCredentialRequests(@PathVariable("credentialId") String credentialId, @PathVariable("tag") String tag) {
        List<CredentialRequestEntity> credentialRequestEntities = new ArrayList<>();
        switch (tag) {
            case "approved":
                credentialRequestEntities = credentialManager.getRespondedCredentialRequests(credentialId, true);
                break;
            case "not-approved":
                credentialRequestEntities = credentialManager.getRespondedCredentialRequests(credentialId, false);
                break;
            default:
                credentialRequestEntities = credentialManager.getNotRespondedCredentialRequests(credentialId);
                break;
        }

        List<CredentialRequestParams> credentialRequestParamsList = new ArrayList<>();
        for (CredentialRequestEntity credentialRequestEntity:credentialRequestEntities) {
            CredentialRequestParams credentialRequestParams = new CredentialRequestParams();
            credentialRequestParams.setId(credentialRequestEntity.getId());
            credentialRequestParams.setCredentials(new CredentialDetails().wrap(credentialRequestEntity.getCredentialEntity()));
            credentialRequestParams.setRequestingUser(new UserDTO().wrap(credentialRequestEntity.getRequestingUser()));
            credentialRequestParams.setRespondingUser(new UserDTO().wrap(credentialRequestEntity.getRespondingUser()));
            credentialRequestParams.setRequestedAt(credentialRequestEntity.getRequestedAt());
            credentialRequestParams.setRespondedAt(credentialRequestEntity.getRespondedAt());
            credentialRequestParams.setApproval(credentialRequestEntity.isApproval());
            credentialRequestParamsList.add(credentialRequestParams);
        }

        return ResponseEntity.ok(credentialRequestParamsList);
    }
}


