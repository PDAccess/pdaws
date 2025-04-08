package com.h2h.pda.service.impl;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.Credential;
import com.h2h.pda.pojo.group.GroupRole;
import com.h2h.pda.pojo.ldap.LdapAccount;
import com.h2h.pda.pojo.service.ServiceCounter;
import com.h2h.pda.pojo.service.ServiceType;
import com.h2h.pda.pojo.service.UserServiceCounter;
import com.h2h.pda.repository.ConnectionUserRepository;
import com.h2h.pda.repository.FavoriteServiceRepository;
import com.h2h.pda.repository.ServiceRepository;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.IService;
import com.h2h.pda.service.api.ServiceOps;
import com.h2h.pda.service.api.VaultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static com.h2h.pda.config.CacheNames.SERVICE_COUNTER;
import static com.h2h.pda.config.CacheNames.SERVICE_USER_COUNTER;
import static com.h2h.pda.pojo.Credential.SECRET_INVENTORY;

@Service
public class ServiceOpsImpl implements ServiceOps {
    private Logger log = LoggerFactory.getLogger(ServiceOpsImpl.class);

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    VaultService vaultService;

    @Autowired
    GroupOps groupOps;

    @Autowired
    ConnectionUserRepository connectionUserRepository;

    @Autowired
    FavoriteServiceRepository favoriteServiceRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public Optional<ServiceEntity> byId(String serviceId) {
        return serviceRepository.findServiceById(serviceId);
    }

    @Override
    public Optional<ServiceEntity> byName(String serviceName) {
        return serviceRepository.findByName(serviceName);
    }

    @Override
    public Optional<ServiceEntity> byIp(String ipAddress) {
        return serviceRepository.findByIpAddress(ipAddress);
    }

    @Override
    public List<UserEntity> collectEffectiveUsers(String serviceId) {
        Objects.requireNonNull(serviceId);

        return serviceRepository.findUsers(serviceId);
    }

    @Override
    public List<ServiceEntity> collectEffectiveService(String userId) {
        Objects.requireNonNull(userId);

        return serviceRepository.findServices(userId);
    }

    @Override
    @Transactional
    public int followUser(String userId, List<String> serviceIds) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(serviceIds);

        int i = 0;

        for (String serviceId : serviceIds) {
            FavoriteServiceEntity entity = new FavoriteServiceEntity();
            FavoriteServicePK pk = new FavoriteServicePK();
            pk.setServiceId(serviceId);
            pk.setUserId(userId);
            entity.setId(pk);

            favoriteServiceRepository.save(entity);
            i++;
        }

        return i;
    }

    @Override
    @Transactional
    public int unfollowUser(String userId, List<String> serviceIds) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(serviceIds);

        int i = 0;
        for (String serviceId : serviceIds) {
            favoriteServiceRepository.deleteByIdUserIdAndIdServiceId(userId, serviceId);
            i++;
        }
        return i;
    }

    @Override
    public List<ServiceEntity> userFollowList(String userId) {
        Objects.requireNonNull(userId);
        List<ServiceEntity> user = favoriteServiceRepository.findByUser(userId);
        return user;
    }

    @Override
    public String createOrUpdate(ServiceEntity serviceEntity) {
        if (!StringUtils.hasText(serviceEntity.getInventoryId())) serviceEntity.setInventoryId(UUID.randomUUID().toString());
        serviceEntity.setLastAccessTime(Timestamp.from(Instant.now()));
        serviceEntity.setDeletedAt(null);
        if (serviceEntity.getCreatedAt() == null) {
            serviceEntity.setCreatedAt(Timestamp.from(Instant.now()));
        }
        IService save = serviceRepository.save(serviceEntity);

        jmsTemplate.convertAndSend("multicast://pda.service.create", save);
        return save.getInventoryId();
    }

    @Override
    public void addAccount(String serviceId, LdapAccount ldapAccount) {
        Optional<ServiceEntity> optionalServiceEntity = serviceRepository.findById(serviceId);
        if (optionalServiceEntity.isPresent()) {
            ServiceEntity serviceEntity = optionalServiceEntity.get();
            ConnectionUserEntity connectionUserEntity = new ConnectionUserEntity();
            connectionUserEntity.setUsername(ldapAccount.getUsername());
            connectionUserEntity.setServiceEntity(serviceEntity);
            connectionUserEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            connectionUserEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            connectionUserEntity.setAdmin(false);
            connectionUserEntity = connectionUserRepository.save(connectionUserEntity);

            Credential credential = new Credential();
            credential.setId(connectionUserEntity.getId());
            credential.setUsername(ldapAccount.getUsername());
            credential.setPassword(ldapAccount.getPassword());

            vaultService.write(SECRET_INVENTORY + serviceId + "/" + connectionUserEntity.getId(), (Object) credential);
        }
    }

    @Override
    public void addLocalAccount(String serviceId, ConnectionUserEntity connectionUserEntity) {
        Optional<ServiceEntity> optionalServiceEntity = byId(serviceId);
        if (optionalServiceEntity.isPresent()) {
            connectionUserEntity.setServiceEntity(optionalServiceEntity.get());
            connectionUserRepository.save(connectionUserEntity);
        }
    }

    @Override
    public Iterable<ServiceEntity> search(ServiceType serviceType) {
        return serviceRepository.findByServiceType(serviceType);
    }

    @Override
    public List<ServiceEntity> search() {
        return serviceRepository.findByNotDeleted();
    }

    @Override
    public List<ServiceEntity> search(String userId, String filter, String filter1, PageRequest req) {
        return serviceRepository.findByUserIdAndTenantIdAndFilter(userId, filter, filter1, req);
    }

    @Override
    public List<ServiceEntity> search(String userId, GroupRole groupRole, String filter, String filter1, PageRequest req) {
        return serviceRepository.findByUserIdAndTenantIdAndFilter(userId, groupRole, filter, filter1, req);
    }

    @Override
    @Transactional
    public void delete(String serviceId) {
        Optional<ServiceEntity> entity = byId(serviceId);
        ServiceEntity serviceEntity = entity.get();

        serviceEntity.setDeletedAt(new Timestamp(System.currentTimeMillis()));

        IService save = serviceRepository.save(serviceEntity);
        jmsTemplate.convertAndSend("multicast://pda.service.delete", save);
    }

    @Override
    @Cacheable(cacheNames = SERVICE_COUNTER, key = "#serviceId")
    public ServiceCounter counters(String serviceId) {
        ServiceCounter counter = new ServiceCounter();
        counter.setMembers(serviceRepository.countOfMembers(serviceId));
        counter.setGroups(serviceRepository.countOfGroups(serviceId));
        counter.setCredential(serviceRepository.countOfCredentials(serviceId));
        counter.setPolicy(serviceRepository.countOfPolicies(serviceId));
        counter.setAlarm(serviceRepository.countOfAlarms(serviceId));
        return counter;
    }

    @Override
    @Cacheable(cacheNames = SERVICE_USER_COUNTER, key = "#userId")
    public UserServiceCounter userCounters(String userId) {
        UserServiceCounter counter = new UserServiceCounter();
        counter.setAll(serviceRepository.countOfServices(userId));
        counter.setYours(serviceRepository.countOfServices(userId, GroupRole.ADMIN));
        counter.setJoined(serviceRepository.countOfServices(userId, GroupRole.USER));
        counter.setAgent(serviceRepository.countOfAgentServices(userId));

        return counter;
    }

    @Override
    public List<GroupsEntity> effectiveGroups(String serviceId, String filter) {
        return serviceRepository.findServiceGroups(serviceId, filter);
    }

    @Override
    public List<GroupsEntity> effectiveGroups(String serviceId) {
        return serviceRepository.findServiceGroups(serviceId, null);
    }

    @Override
    public List<GroupsEntity> effectiveGroupsByServiceAndUser(String serviceId, String userId) {
        return serviceRepository.findGroupsByServiceAndUserId(serviceId, userId);
    }

    @Override
    public List<ServiceEntity> getAgentServices(String userId, boolean isActiveAgent, String filter, PageRequest req) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, -5);
        Timestamp checkTime = new Timestamp(cal.getTimeInMillis());
        List<ServiceEntity> serviceEntities;
        if (isActiveAgent) {
            serviceEntities = serviceRepository.findActiveAgents(userId, filter, checkTime, req);
        } else {
            serviceEntities = serviceRepository.findDeactiveAgents(userId, filter, checkTime, req);
        }
        return serviceEntities;
    }

    @Override
    public boolean isEqualsMembershipRole(String serviceId, String userId, GroupRole groupRole) {
        Optional<ServiceEntity> optionalServiceEntity = serviceRepository.findByUserAndServiceIdAndRole(serviceId, userId, groupRole);
        return optionalServiceEntity.isPresent();
    }

    @Override
    public boolean isMembership(String serviceId, String userId) {
        Optional<ServiceEntity> optionalServiceEntity = serviceRepository.findByUserAndServiceId(serviceId, userId);
        return optionalServiceEntity.isPresent();
    }

}
