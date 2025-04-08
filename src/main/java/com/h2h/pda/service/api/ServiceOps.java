package com.h2h.pda.service.api;

import com.h2h.pda.entity.ConnectionUserEntity;
import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.group.GroupRole;
import com.h2h.pda.pojo.ldap.LdapAccount;
import com.h2h.pda.pojo.service.ServiceCounter;
import com.h2h.pda.pojo.service.ServiceType;
import com.h2h.pda.pojo.service.UserServiceCounter;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface ServiceOps {

    Optional<ServiceEntity> byId(String serviceId);

    Optional<ServiceEntity> byName(String serviceName);

    Optional<ServiceEntity> byIp(String ipAddress);

    List<UserEntity> collectEffectiveUsers(String serviceId);

    List<ServiceEntity> collectEffectiveService(String userId);

    int followUser(String userId, List<String> serviceId);

    int unfollowUser(String userId, List<String> serviceId);

    List<ServiceEntity> userFollowList(String userId);

    String createOrUpdate(ServiceEntity serviceEntity);

    void addAccount(String serviceId, LdapAccount ldapAccount);

    void addLocalAccount(String serviceId, ConnectionUserEntity connectionUserEntity);

    Iterable<ServiceEntity> search(ServiceType ldap);

    List<ServiceEntity> search();

    List<ServiceEntity> search(String userId, String filter, String filter1, PageRequest req);

    List<ServiceEntity> search(String userId, GroupRole groupRole, String filter, String filter1, PageRequest req);

    void delete(String serviceId);

    ServiceCounter counters(String serviceId);

    UserServiceCounter userCounters(String userId);

    List<GroupsEntity> effectiveGroups(String serviceId, String filter);

    List<GroupsEntity> effectiveGroups(String serviceId);

    List<GroupsEntity> effectiveGroupsByServiceAndUser(String serviceId, String userId);

    List<ServiceEntity> getAgentServices(String userId, boolean isActiveAgent, String filter, PageRequest req);

    boolean isEqualsMembershipRole(String serviceId, String userId, GroupRole groupRole);

    boolean isMembership(String serviceId, String userId);

}
