package com.h2h.pda.api.services;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.*;
import com.h2h.pda.pojo.group.GroupRole;
import com.h2h.pda.pojo.group.GroupUserWrapper;
import com.h2h.pda.pojo.group.GroupsEntityWrapper;
import com.h2h.pda.pojo.service.*;
import com.h2h.pda.repository.ConnectionUserRepository;
import com.h2h.pda.repository.SessionRepository;
import com.h2h.pda.repository.TenantRepository;
import com.h2h.pda.service.api.*;
import com.h2h.pda.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.vault.VaultException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/service")
public class V1ServiceController extends ConnectionUserController {

    private static final Logger log = LoggerFactory.getLogger(V1ServiceController.class);

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
    UsersOps usersOps;

    @Autowired
    GroupOps groupOps;

    @Autowired
    ActionPdaService actionPdaService;

    @GetMapping(path = "/all")
    public ResponseEntity<List<ServiceEntityWrapper>> allServices() {
        return getServicesWithSortMethod("", new ServicePagination(0, Integer.MAX_VALUE, ""));
    }

    @PostMapping(path = "/groups/{serviceId}")
    public ResponseEntity<Set<GroupsEntityWrapper>> groups(@PathVariable String serviceId, @RequestBody Pagination pagination) {
        List<GroupsEntity> groupsEntities = serviceOps.effectiveGroups(serviceId, pagination.getFilter());
        return ResponseEntity.ok(groupsEntities.stream().map(g -> new GroupsEntityWrapper(g)).collect(Collectors.toSet()));
    }

    @GetMapping(path = "/members/{serviceId}")
    public ResponseEntity<Set<GroupUserWrapper>> members(@PathVariable String serviceId) {
        Set<GroupUserWrapper> groupUserWrappers = new HashSet<>();
        List<GroupsEntity> groupsEntities = serviceOps.effectiveGroups(serviceId);

        for (GroupsEntity ge : groupsEntities) {
            Iterable<GroupUserEntity> groupUserEntities = groupOps.effectiveMembers(ge.getGroupId());
            for (GroupUserEntity gue : groupUserEntities) {
                GroupUserWrapper wrapper = new GroupUserWrapper(gue.getUser(), gue.getGroup());
                wrapper.setCreatedAt(gue.getCreatedAt());
                wrapper.setMembershipRole(gue.getMembershipRole() != null ? gue.getMembershipRole().name() : "none");
                wrapper.setMembershipType(gue.getMembershipType() != null ? gue.getMembershipType().name() : "none");

                groupUserWrappers.add(wrapper);
            }
        }

        return ResponseEntity.ok(groupUserWrappers);
    }

    @PostMapping("/counters")
    public ResponseEntity<UserServiceCounter> userCounters() {
        UserEntity securedUser = usersOps.securedUser();
        UserServiceCounter userGroupCounter = serviceOps.userCounters(securedUser.getUserId());
        return ResponseEntity.ok(userGroupCounter);
    }

    @PostMapping(path = "/sort/{sort}")
    public ResponseEntity<List<ServiceEntityWrapper>> getServicesWithSortMethod(@PathVariable String sort, @RequestBody ServicePagination pagination) {
        UserEntity user = usersOps.securedUser();

        PageRequest req;
        switch (sort) {
            case CREATE:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(CREATED_AT));
                break;
            case "os":
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(OPERATING_SYS_ID));
                break;
            case OS_DESC:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(OPERATING_SYS_ID).descending());
                break;
            case OP_SERVICE:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(SERVICE_TYPE_ID));
                break;
            case OP_SERVICE_DESC:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(SERVICE_TYPE_ID).descending());
                break;
            case NAME:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(NAME));
                break;
            case NAME_DESC:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(NAME).descending());
                break;
            default:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(CREATED_AT).descending());
                break;
        }

        List<ServiceEntity> serviceEntities;
        if (pagination.getActiveAgent() != null) {
            serviceEntities = serviceOps.getAgentServices(user.getUserId(), pagination.getActiveAgent(), pagination.getFilter(), req);
        } else if ("all".equals(pagination.getCategory())) {
            serviceEntities = serviceOps.search(user.getUserId(), pagination.getFilter(), pagination.getFilter(), req);
        } else if ("yours".equals(pagination.getCategory())) {
            serviceEntities = serviceOps.search(user.getUserId(), GroupRole.ADMIN, pagination.getFilter(), pagination.getFilter(), req);
        } else if ("marked".equals(pagination.getCategory())) {
            serviceEntities = serviceOps.search(user.getUserId(), GroupRole.USER, pagination.getFilter(), pagination.getFilter(), req);
        } else {
            serviceEntities = serviceOps.search(user.getUserId(), pagination.getFilter(), pagination.getFilter(), req);
        }

        List<ServiceEntityWrapper> collect = serviceEntities.stream().map(s -> {
            ServiceEntityWrapper wrap = new ServiceEntityWrapper().wrap(s);
            wrap.setServiceCounters(serviceOps.counters(s.getInventoryId()));
            return wrap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(collect);
    }

    @GetMapping(path = "/sort/{sort}")
    public ResponseEntity<List<ServiceEntityWrapper>> getAllServicesWithSortMethod(@PathVariable String sort) {
        UserEntity user = usersOps.securedUser();

        int perPage = Integer.MAX_VALUE;
        PageRequest req;
        switch (sort) {
            case CREATE:
                req = PageRequest.of(0, perPage, Sort.by(CREATED_AT));
                break;
            case "os":
                req = PageRequest.of(0, perPage, Sort.by(OPERATING_SYS_ID));
                break;
            case OS_DESC:
                req = PageRequest.of(0, perPage, Sort.by(OPERATING_SYS_ID).descending());
                break;
            case OP_SERVICE:
                req = PageRequest.of(0, perPage, Sort.by(SERVICE_TYPE_ID));
                break;
            case OP_SERVICE_DESC:
                req = PageRequest.of(0, perPage, Sort.by(SERVICE_TYPE_ID).descending());
                break;
            case "name":
                req = PageRequest.of(0, perPage, Sort.by("name"));
                break;
            case NAME_DESC:
                req = PageRequest.of(0, perPage, Sort.by("name").descending());
                break;
            default:
                req = PageRequest.of(0, perPage, Sort.by(CREATED_AT).descending());
                break;
        }

        List<ServiceEntity> serviceEntities = serviceOps.search(user.getUserId(), null, null, req);
        return getWrapperList(serviceEntities);

    }

    @GetMapping(path = "/user/{userId}")
    public ResponseEntity<List<ServiceEntityWrapper>> userServices(@PathVariable String userId) {
        List<ServiceEntityWrapper> list = new ArrayList<>();

        List<ServiceEntity> entities = serviceOps.collectEffectiveService(userId);

        for (ServiceEntity serviceEntity : entities) {
            getServiceEntityWrapper(list, serviceEntity);
        }

        return ResponseEntity.ok(list);
    }

    @PostMapping(path = "/user/{username}/{sort}")
    public ResponseEntity<List<ServiceEntityWrapper>> getServicesWithSortMethod(@PathVariable String username, @PathVariable String sort, @RequestBody Pagination pagination) {
        Optional<UserEntity> byName = usersOps.byName(username);
        if (!byName.isPresent())
            return ResponseEntity.noContent().build();

        PageRequest req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(CREATED_AT).descending());
        switch (sort) {
            case CREATE:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(CREATED_AT));
                break;
            case "create-desc":
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(CREATED_AT).descending());
                break;
            case "os":
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(OPERATING_SYS_ID));
                break;
            case OS_DESC:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(OPERATING_SYS_ID).descending());
                break;
            case OP_SERVICE:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(SERVICE_TYPE_ID));
                break;
            case OP_SERVICE_DESC:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by(SERVICE_TYPE_ID).descending());
                break;
            case "name":
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("name"));
                break;
            case NAME_DESC:
                req = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("name").descending());
                break;
        }

        List<ServiceEntity> serviceEntities = serviceOps.search(byName.get().getUserId(), pagination.getFilter(), pagination.getFilter(), req);
        return getWrapperList(serviceEntities);

    }


    @GetMapping(path = "/id/{serviceId}")
    public ResponseEntity<ServiceEntityWrapper> getServiceByInventoryId(@PathVariable String serviceId) {

        Optional<ServiceEntity> serviceEntity = serviceOps.byId(serviceId);
        if (serviceEntity.isPresent()) {
            ServiceEntity service = serviceEntity.get();
            ServiceEntityWrapper serviceWrapper = new ServiceEntityWrapper(service);
            serviceWrapper.setServiceType(service.getServiceTypeId().getMeta().getPropertyString());
            serviceWrapper.setAdmin(serviceOps.isEqualsMembershipRole(service.getInventoryId(), usersOps.securedUser().getUserId(), GroupRole.ADMIN));
            return new ResponseEntity<>(serviceWrapper, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("service")
    public ResponseEntity<String> addService(@RequestBody ServiceCreateParams serviceCreateParams) {
        UserEntity user = usersOps.securedUser();

        if (serviceCreateParams.getGroupid() == null || serviceCreateParams.getGroupid().equals(""))
            return new ResponseEntity<>("Group is cannot be empty", HttpStatus.BAD_REQUEST);

        Optional<GroupsEntity> groupsEntity = groupOps.byId(serviceCreateParams.getGroupid());
        if (!groupsEntity.isPresent())
            return new ResponseEntity<>("Group is not found!", HttpStatus.BAD_REQUEST);

        ServiceEntityWrapper serviceEntityWrapper = serviceCreateParams.getServiceEntity();
        ServiceEntity serviceEntity = serviceEntityWrapper.unWrap();

        if (serviceEntity.getName() == null || serviceEntity.getName().isEmpty())
            return new ResponseEntity<>("Service name cannot be empty!", HttpStatus.BAD_REQUEST);

        if (serviceEntity.getServiceTypeId() == null || serviceEntity.getServiceTypeId() == ServiceType.UNKNOWN)
            return new ResponseEntity<>("Service type is wrong!", HttpStatus.BAD_REQUEST);

        if (serviceEntity.getOperatingSystemId() == null || serviceEntity.getOperatingSystemId() == ServiceOs.UNKNOWN_SERVICE)
            return new ResponseEntity<>("Operating system is wrong!", HttpStatus.BAD_REQUEST);

        if (serviceCreateParams.getIpaddress() == null || serviceCreateParams.getIpaddress().isEmpty())
            return new ResponseEntity<>("IP Address is wrong!", HttpStatus.BAD_REQUEST);

        if (serviceCreateParams.getPort() < 0 || serviceCreateParams.getPort() > 65535)
            return new ResponseEntity<>("Port is wrong!", HttpStatus.BAD_REQUEST);

        Optional<ServiceEntity> optionalServiceEntity =  serviceOps.byIp(serviceCreateParams.getIpaddress());
        if (optionalServiceEntity.isPresent()) {
            return new ResponseEntity<>("Service registered on this IP already exists", HttpStatus.BAD_REQUEST);
        }

        serviceEntity.setWhoCreate(user.getUserId());
        serviceEntity.setIpAddress(serviceCreateParams.getIpaddress());
        serviceEntity.setPort(serviceCreateParams.getPort());
        serviceEntity.setDbName(serviceCreateParams.getDbname());
        serviceEntity.setPath(serviceCreateParams.getPath());

        String serviceId = serviceOps.createOrUpdate(serviceEntity);


        if (StringUtils.hasText(serviceCreateParams.getGroupid())) {
            groupOps.addServicesTo(serviceCreateParams.getGroupid(), Collections.singletonList(serviceId));
        }

        saveCredential(serviceId, serviceCreateParams.getAdmin(), serviceCreateParams.getConnectionUser(), serviceEntity, groupsEntity.get(), true);

        for (Credential credential : serviceCreateParams.getVaults()) {
            saveCredential(serviceId, credential, serviceCreateParams.getConnectionUser(), serviceEntity, groupsEntity.get(), false);
        }

        actionPdaService.saveAction(String.format("%s service is created", serviceEntity.getName()));

        return new ResponseEntity<>(serviceId, HttpStatus.OK);
    }

    @PostMapping(path = "/record/enable/{id}")
    public ResponseEntity<Void> enableVideoRecord(@PathVariable String id) {
        return recordChange(id, true);
    }

    @PostMapping(path = "/record/disable/{id}")
    public ResponseEntity<Void> disableVideoRecord(@PathVariable String id) {
        return recordChange(id, false);
    }

    @PostMapping(path = "/property/save")
    public ResponseEntity<Void> saveProperty(@RequestBody PropertyParams params) {
        Optional<ServiceEntity> property = serviceOps.byId(params.getServiceId());

        if (property.isPresent()) {
            ServiceProperty serviceProperty = new ServiceProperty().setKey(ServiceProperties.ofElseThrow(params.getKey())).setValue(params.getValue());

            property.get().getProperties().remove(serviceProperty);
            property.get().getProperties().add(serviceProperty);

            serviceOps.createOrUpdate(property.get());

            actionPdaService.saveAction(String.format("service with %s id propery saved", params.getServiceId()));

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    // TODO: Entity Fix
    @GetMapping("/property/get")
    public ResponseEntity<ServiceProperty> getServiceProperty(@RequestParam("serviceid") String serviceId, @RequestParam String key) {
        Optional<ServiceProperties> serviceProperty = ServiceProperties.of(key);
        Optional<ServiceEntity> byId = serviceOps.byId(serviceId);

        Optional<ServiceProperty> first = byId.get().getProperties().stream().filter(s -> s.getKey() == serviceProperty.get()).findFirst();


        return first.map(property -> new ResponseEntity<>(property, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> softDeleteService(@PathVariable String id) {
        Optional<ServiceEntity> serviceEntity = serviceOps.byId(id);
        if (serviceEntity.isPresent()) {
            ServiceEntity service = serviceEntity.get();
            serviceOps.delete(service.getInventoryId());
            actionPdaService.saveAction(String.format("%s service is deleted", service.getName()));
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping(path = "/credential/{serviceId}")
    public ResponseEntity<Void> changeCredential(@RequestBody CredentialParam param, @PathVariable String serviceId) {
        Optional<ServiceEntity> serviceEntity = serviceOps.byId(serviceId);
        if (serviceEntity.isPresent()) {
            ServiceEntity service = serviceEntity.get();
            service.setCredantial(param.getCredential());
            serviceOps.createOrUpdate(service);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/host/{id}")
    public ResponseEntity<HostPortDTO> getHostPort(@PathVariable String id) {

        Optional<ServiceEntity> optionalServiceEntity = serviceOps.byId(id);
        if (!optionalServiceEntity.isPresent()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        ServiceEntity serviceEntity = optionalServiceEntity.get();

        HostPortDTO hostPortDTO = new HostPortDTO();
        hostPortDTO.setHost(serviceEntity.getIpAddress());
        hostPortDTO.setPort(serviceEntity.getPort());
        hostPortDTO.setId(id);
        hostPortDTO.setPath(serviceEntity.getPath());

        return new ResponseEntity<>(hostPortDTO, HttpStatus.OK);

    }

    @PutMapping(path = "/host")
    public ResponseEntity<HostPortDTO> updateHostPort(@RequestBody HostPortDTO hostportdto) {

        Optional<ServiceEntity> optionalServiceEntity = serviceOps.byId(hostportdto.getId());
        if (!optionalServiceEntity.isPresent()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        ServiceEntity serviceEntity = optionalServiceEntity.get();
        serviceEntity.setIpAddress(hostportdto.getHost());
        serviceEntity.setPort(hostportdto.getPort());
        serviceEntity.setPath(hostportdto.getPath());
        serviceOps.createOrUpdate(serviceEntity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> updateService(@PathVariable String id,
                                              @RequestParam(name = "service_name") String name,
                                              @RequestParam(name = "service_description") String description,
                                              @RequestParam(name = "service_version") String serviceVersion) {
        UserEntity userEntity = usersOps.securedUser();

        Optional<ServiceEntity> optionalServiceEntity = serviceOps.byId(id);
        if (optionalServiceEntity.isPresent()) {
            ServiceEntity serviceEntity = optionalServiceEntity.get();
            String oldName = serviceEntity.getName();
            serviceEntity.setName(name);
            serviceEntity.setDescription(description);
            serviceEntity.setOperatingSystemVersion(serviceVersion);
            serviceEntity.setWhoUpdate(userEntity.getUserId());
            serviceEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            serviceOps.createOrUpdate(serviceEntity);
            actionPdaService.saveAction(String.format("changed and updated %s oldName to service %s name", oldName, serviceEntity.getName()));
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/connect")
    public ResponseEntity<Void> serviceConnectionTest(@RequestBody ConnectionParams params) {

        CredentialManagerResponse verify = credentialManager.verify(new VerifyRequest().setHostname(params.getServerName()).setPort(params.getPort()));

        if (verify == null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/app/{id}")
    public ResponseEntity<String> addServiceApp(@PathVariable String id,
                                                @RequestParam(name = "name") String appName,
                                                @RequestParam(name = "app") String app,
                                                @RequestParam(name = "directory") String appDirectory,
                                                @RequestParam(name = "args") String appArgs) {

        Optional<ServiceEntity> serviceEntities = serviceOps.byId(id);
        if (!serviceEntities.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        UserEntity user = usersOps.securedUser();

        ServiceEntity originService = serviceEntities.get();

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setInventoryId(UUID.randomUUID().toString());
        serviceEntity.setName(originService.getName() + "-" + appName);
        serviceEntity.setDescription(originService.getDescription());
        serviceEntity.setCredantial(originService.getCredantial());
        serviceEntity.setServiceTypeId(originService.getServiceTypeId());
        serviceEntity.setOperatingSystemId(originService.getOperatingSystemId());
        serviceEntity.setOriginId(originService.getInventoryId());
        serviceEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        serviceEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        serviceEntity.setWhoCreate(user.getUserId());
        String serviceOpsOrUpdate = serviceOps.createOrUpdate(serviceEntity);

        List<GroupsEntity> groupsEntities = serviceOps.effectiveGroups(originService.getInventoryId());

        for (GroupsEntity groupServiceEntity : groupsEntities) {
            groupOps.addServicesTo(groupServiceEntity.getGroupId(), Collections.singletonList(serviceOpsOrUpdate));
        }

        ServiceProperty serviceProperty = new ServiceProperty();
        serviceProperty.setKey(ServiceProperties.REMOTE_APP);
        serviceProperty.setValue(app);
        originService.getProperties().add(serviceProperty);
        //servicePropsRepository.save(serviceProperty);

        serviceProperty = new ServiceProperty();
        serviceProperty.setKey(ServiceProperties.REMOTE_APP_DIR);
        serviceProperty.setValue(appDirectory);
        originService.getProperties().add(serviceProperty);
        //servicePropsRepository.save(serviceProperty);

        serviceProperty = new ServiceProperty();
        serviceProperty.setKey(ServiceProperties.REMOTE_APP_ARGS);
        serviceProperty.setValue(appArgs);
        originService.getProperties().add(serviceProperty);
        //servicePropsRepository.save(serviceProperty);

        serviceOps.createOrUpdate(originService);

        return new ResponseEntity<>(serviceEntity.getInventoryId(), HttpStatus.OK);
    }

    // TODO: Entity Fix
    @GetMapping(path = "/config/{id}")
    public ResponseEntity<Set<ServiceProperty>> getServiceConfig(@PathVariable(name = "id") String serviceId) {
        Optional<ServiceEntity> serviceEntity = serviceOps.byId(serviceId);
        return serviceEntity.isPresent() ?
                ResponseEntity.ok(serviceEntity.get().getProperties()) : ResponseEntity.badRequest().build();
    }

    @PostMapping(path = "/info")
    public ResponseEntity<ConnectionParamIds> getServiceInfo(@RequestBody ConnectionParamNames paramNames) {
        Optional<GroupsEntity> optionalGroupsEntity = groupOps.byName(paramNames.getGroupName());
        Optional<CredentialEntity> optionalCredentialEntity = credentialManager.getCredentialByNameAndGroup(paramNames.getCredentialName(), paramNames.getGroupName());
        if (optionalGroupsEntity.isPresent() && optionalCredentialEntity.isPresent() && optionalCredentialEntity.get().getConnectionUser() != null && optionalCredentialEntity.get().getConnectionUser().getServiceEntity() != null) {
            GroupsEntity groupsEntity = optionalGroupsEntity.get();
            CredentialEntity credentialEntity = optionalCredentialEntity.get();
            ConnectionParamIds connectionParamIds = new ConnectionParamIds(groupsEntity.getGroupId(), credentialEntity.getConnectionUser().getServiceEntity().getInventoryId(), credentialEntity.getCredentialId());
            return new ResponseEntity<>(connectionParamIds, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // TODO: Fix for credentail logics
    @GetMapping(path = "/putty-key/{credential_id}")
    public ResponseEntity<String> getServicePuttyKey(@PathVariable(name = "credential_id") String credentialId, HttpServletRequest request) {
        UserEntity user = usersOps.securedUser();

        CredentialEntity credentialEntity = credentialManager.getCredential(credentialId);
        if (credentialEntity == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        try {
            CredentialParams credentialParams = credentialManager.breakCredential(credentialId, "Break credential for Putty Key", RequestUtil.remoteAddress(request), false);
            if (credentialParams == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            if (credentialParams.getPpKey() == null || credentialParams.getPpKey().equals("")) {
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(MediaType.APPLICATION_JSON);
                requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                HttpEntity<String> requestEntity = new HttpEntity<>(credentialParams.getKey(), requestHeaders);
                String ppKey = restTemplate.exchange(KEY_SERVER_URL, HttpMethod.POST, requestEntity, String.class).getBody();
                credentialParams.setPpKey(ppKey);
                credentialManager.updateCredentialData(credentialParams);
            }

            return new ResponseEntity<>(credentialParams.getPpKey(), HttpStatus.OK);

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/admin")
    public ResponseEntity<List<ServiceEntityWrapper>> getServiceOfAdminGroup() {
        List<ServiceEntity> serviceEntities = new ArrayList<>();
        List<GroupsEntity> groupsEntities = groupOps.searchBy(usersOps.securedUser().getUserId(), GroupRole.ADMIN);
        for (GroupsEntity groupsEntity : groupsEntities) {
            List<ServiceEntity> serviceEntityList = groupOps.effectiveServices(groupsEntity.getGroupId());
            for (ServiceEntity serviceEntity : serviceEntityList) {
                if (!serviceEntities.contains(serviceEntity)) {
                    serviceEntities.add(serviceEntity);
                }
            }
        }
        List<ServiceEntityWrapper> serviceEntityWrappers = serviceEntities.stream().map(ServiceEntityWrapper::new).collect(Collectors.toList());
        return ResponseEntity.ok(serviceEntityWrappers);
    }

    @GetMapping(path = "/role/{id}")
    public ResponseEntity<GroupRole> getServiceRole(@PathVariable("id") String serviceId) {
        boolean isAdminForService = serviceOps.isEqualsMembershipRole(serviceId, usersOps.securedUser().getUserId(), GroupRole.ADMIN);
        return new ResponseEntity<>(isAdminForService ? GroupRole.ADMIN : GroupRole.USER, HttpStatus.OK);
    }

}