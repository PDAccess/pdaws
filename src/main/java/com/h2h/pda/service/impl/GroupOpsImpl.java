package com.h2h.pda.service.impl;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.group.*;
import com.h2h.pda.repository.GroupsRepository;
import com.h2h.pda.repository.ServiceRepository;
import com.h2h.pda.repository.UserRepository;
import com.h2h.pda.service.api.GroupOps;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.h2h.pda.config.CacheNames.GROUP_COUNTER;
import static com.h2h.pda.config.CacheNames.GROUP_USER_COUNTER;

@Service
public class GroupOpsImpl implements GroupOps {
    private final Logger log = LoggerFactory.getLogger(GroupOpsImpl.class);

    @Autowired
    GroupsRepository groupsRepository;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JmsTemplate jmsTemplate;

    @Override
    public List<UserEntity> effectiveUsers(String groupId) {
        Optional<GroupsEntity> byGroupId = groupsRepository.findById(groupId);
        if (!byGroupId.isPresent())
            return Collections.emptyList();

        GroupsEntity groupsEntity = byGroupId.get();
        //groupsEntity.getMembers().size();
        List<UserEntity> collect1 = groupsEntity.getMembers()
                .stream().map(groupUserEntity -> groupUserEntity.getUser()).collect(Collectors.toList());


        return collect1;
    }

    @Override
    @Transactional
    public Iterable<GroupUserEntity> effectiveMembers(String groupId) {
        Optional<GroupsEntity> id = groupsRepository.findById(groupId);
        if (id.isPresent()) {
            GroupsEntity entity = id.get();
            entity.getMembers().size();

            return entity.getMembers().stream().filter(groupUserEntity -> !groupUserEntity.getUser().isDeleted()).collect(Collectors.toList());
        }

        return Collections.emptySet();
    }

    @Override
    public List<ServiceEntity> effectiveServices(String groupId) {
        Objects.requireNonNull(groupId);

        Optional<GroupsEntity> id = groupsRepository.findByIdAndNotDeleted(groupId);
        if (id.isPresent()) {
            Set<GroupServiceEntity> services = id.get().getServices();
            return services.stream().map(GroupServiceEntity::getService).filter(service -> !service.isDeleted()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public int addServicesTo(String groupId, List<String> serviceList) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(serviceList);

        Optional<GroupsEntity> id = groupsRepository.findById(groupId);
        AtomicInteger i = new AtomicInteger(0);

        List<String> groupServices = effectiveServices(groupId).stream().map(ServiceEntity::getInventoryId).collect(Collectors.toList());

        if (id.isPresent()) {
            GroupsEntity groupsEntity = id.get();

            serviceList.forEach(s -> {
                if (!groupServices.contains(s)) {
                    GroupServiceEntity entity = new GroupServiceEntity();
                    entity.setId(new GroupServicePK());
                    entity.setGroup(groupsEntity);
                    Optional<ServiceEntity> byId = serviceRepository.findById(s);
                    entity.setService(byId.get());

                    groupsEntity.getServices().add(entity);
                    i.incrementAndGet();
                }
            });
        }

        return i.get();
    }

    @Override
    @Transactional
    public int removeServicesFrom(String groupId, List<String> serviceList) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(serviceList);

        Optional<GroupsEntity> id = groupsRepository.findById(groupId);
        AtomicInteger i = new AtomicInteger(0);

        if (id.isPresent()) {
            GroupsEntity groupsEntity = id.get();

            List<GroupServiceEntity> collect = serviceList.stream()
                    .map(s -> new GroupServiceEntity(new GroupServicePK(groupsEntity.getGroupId(), s)))
                    .collect(Collectors.toList());

            if (groupsEntity.getServices().removeAll(collect))
                i.set(collect.size());
        }

        return i.get();
    }

    @Override
    @Transactional
    public int addUsersTo(String groupId, List<String> userList, GroupMembership membership, GroupRole... roles) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(userList);

        Optional<GroupsEntity> id = groupsRepository.findById(groupId);

        if (id.isPresent()) {
            GroupsEntity groupsEntity = id.get();
            List<UserEntity> userEntities = new ArrayList<>();
            for (String userId : userList) {
                Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
                optionalUserEntity.ifPresent(userEntities::add);
            }
            List<GroupUserEntity> collect = userEntities.stream().map(user -> new GroupUserEntity()
                        .setId(new GroupUserPK().setGroupId(groupId).setUserId(user.getUserId()))
                        .setGroup(groupsEntity)
                        .setUser(user)
                        .setCreatedAt(Timestamp.from(Instant.now()))
                        .setMembershipType(GroupMembership.NORMAL)
                        .setMembershipRole((roles == null || roles.length == 0) ? GroupRole.USER : roles[0])).collect(Collectors.toList());

            //Iterable<GroupUserEntity> entities = groupsUserRepository.saveAll(collect);
            groupsEntity.getMembers().addAll(collect);

            for (GroupsEntity group : groupsRepository.findChildGroups(groupId)) {
                collect = userEntities.stream().map(user -> new GroupUserEntity()
                            .setId(new GroupUserPK().setGroupId(group.getGroupId()).setUserId(user.getUserId()))
                            .setGroup(group)
                            .setUser(user)
                            .setCreatedAt(Timestamp.from(Instant.now()))
                            .setMembershipType(GroupMembership.NORMAL)
                            .setMembershipRole((roles == null || roles.length == 0) ? GroupRole.USER : roles[0])).collect(Collectors.toList());
                group.getMembers().addAll(collect);
            }

            return groupsEntity.getMembers().size();
        }

        return 0;
    }

    @Override
    @Transactional
    public int addUsersTo(String groupId, List<String> userList, GroupMembership membership, Timestamp expireDate, GroupRole... roles) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(userList);

        Optional<GroupsEntity> id = groupsRepository.findById(groupId);
        List<GroupsEntity> groupsEntities = groupsRepository.findChildGroups(groupId);

        if (id.isPresent()) {
            GroupsEntity groupsEntity = id.get();
            List<UserEntity> userEntities = new ArrayList<>();
            for (String userId : userList) {
                Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
                optionalUserEntity.ifPresent(userEntities::add);
            }
            List<GroupUserEntity> collect = userEntities.stream().map(user -> new GroupUserEntity()
                    .setId(new GroupUserPK().setGroupId(groupId).setUserId(user.getUserId()))
                    .setGroup(groupsEntity)
                    .setUser(user)
                    .setCreatedAt(Timestamp.from(Instant.now()))
                    .setMembershipType(GroupMembership.NORMAL)
                    .setMembershipRole((roles == null || roles.length == 0) ? GroupRole.USER : roles[0])
                    .setExpireDate(expireDate)).collect(Collectors.toList());

            //Iterable<GroupUserEntity> entities = groupsUserRepository.saveAll(collect);
            groupsEntity.getMembers().addAll(collect);

            for (GroupsEntity group : groupsEntities) {
                collect = userEntities.stream().map(user -> new GroupUserEntity()
                        .setId(new GroupUserPK().setGroupId(group.getGroupId()).setUserId(user.getUserId()))
                        .setGroup(group)
                        .setUser(user)
                        .setCreatedAt(Timestamp.from(Instant.now()))
                        .setMembershipType(GroupMembership.NORMAL)
                        .setMembershipRole((roles == null || roles.length == 0) ? GroupRole.USER : roles[0])
                        .setExpireDate(expireDate)).collect(Collectors.toList());
                group.getMembers().addAll(collect);
            }

            return groupsEntity.getMembers().size();
        }

        return 0;
    }

    @Override
    @Transactional
    public int addUsersTo(String groupId, List<String> userList, GroupRole... roles) {
        return addUsersTo(groupId, userList, GroupMembership.NORMAL, roles);
    }

    @Override
    @Transactional
    public int removeUsersFrom(String groupId, List<String> userList) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(userList);

        Optional<GroupsEntity> id = groupsRepository.findById(groupId);
        List<GroupsEntity> groupsEntities = groupsRepository.findChildGroups(groupId);

        AtomicInteger i = new AtomicInteger(0);

        if (id.isPresent()) {
            GroupsEntity entity = id.get();
            Iterator<GroupUserEntity> members = entity.getMembers().iterator();

            while (members.hasNext()) {
                GroupUserEntity gue = members.next();
                if (userList.contains(gue.getId().getUserId())) {
                    members.remove();
                    i.incrementAndGet();
                }

            }

            for (GroupsEntity groupsEntity : groupsEntities) {
                groupsEntity.getMembers().removeIf(gue -> userList.contains(gue.getId().getUserId()));
            }

            //groupsRepository.save(entity);
        }

        return i.get();
    }

    @Override
    @Transactional
    public void deleteById(String groupId) throws IllegalArgumentException {
        Optional<GroupsEntity> id = groupsRepository.findById(groupId);
        if (!id.isPresent())
            throw new IllegalArgumentException("There is no group with id: " + groupId);

        GroupsEntity groupsEntity = id.get();
        groupsEntity.setDeletedAt(Timestamp.from(Instant.now()));
        groupsRepository.save(groupsEntity);

        //groupsRepository.deleteById(groupId);
    }

    @Override
    @Transactional
    public String newGroup(GroupsEntity entityWrapper, UserEntity whoCreate) {
        GroupsEntity groupsEntity = new GroupsEntity();
        groupsEntity.setCreatedAt(Timestamp.from(Instant.now()));
        groupsEntity.setUpdatedAt(Timestamp.from(Instant.now()));
        groupsEntity.setDeletedAt(null);
        groupsEntity.setGroupId(UUID.randomUUID().toString());
        groupsEntity.setDescription(entityWrapper.getDescription());
        groupsEntity.setGroupName(entityWrapper.getGroupName());
        groupsEntity.setGroupType(entityWrapper.getGroupType());
        groupsEntity.setLdapRdn(entityWrapper.getLdapRdn());
        groupsEntity.setLdapDn(entityWrapper.getLdapDn());
        groupsEntity.setGroupCategory(entityWrapper.getGroupCategory());
        groupsEntity.setParent(entityWrapper.getParent());

        groupsEntity = groupsRepository.save(groupsEntity);

        GroupUserEntity groupUserEntity = new GroupUserEntity();
        groupUserEntity.setId(new GroupUserPK(groupsEntity.getGroupId(), whoCreate.getUserId()));
        groupUserEntity.setGroup(groupsEntity);
        Optional<UserEntity> id = userRepository.findById(whoCreate.getUserId());

        groupUserEntity.setUser(id.get());
        groupUserEntity.setWhoCreate(whoCreate.getUserId());
        groupUserEntity.setCreatedAt(Timestamp.from(Instant.now()));
        groupUserEntity.setMembershipRole(GroupRole.ADMIN);
        groupUserEntity.setMembershipType(GroupMembership.NORMAL);
        //groupUserEntity = groupsUserRepository.save(groupUserEntity);

        groupsEntity.setMembers(new HashSet<>()).getMembers().add(groupUserEntity);

        if (groupsEntity.getParent() != null) {
            for (GroupUserEntity gue : groupsEntity.getParent().getMembers()) {
                addUsersTo(groupsEntity.getGroupId(), Collections.singletonList(gue.getUser().getUserId()), gue.getMembershipType(), gue.getExpireDate(), gue.getMembershipRole());
            }
        }

        jmsTemplate.convertAndSend("multicast://pda.group.create", groupsEntity);

        return groupsEntity.getGroupId();
    }

    @Override
    public Optional<GroupsEntity> byId(String groupId) {
        return groupsRepository.findById(groupId);
    }

    @Override
    public Optional<GroupsEntity> byName(String groupName) {
        return groupsRepository.findByNameAndNotDeleted(groupName);
    }

    @Override
    public Optional<GroupProperty> byProperty(String groupId, GroupProperties property) {
        Optional<GroupsEntity> groupsRepositoryById = byId(groupId);
        if (groupsRepositoryById.isPresent()) {
            GroupsEntity groupsEntity = groupsRepositoryById.get();
            if (groupsEntity.getProperties().contains(property)) {
                return groupsEntity.getProperties().stream().filter(gp -> gp.getKey().equals(property)).findFirst();

            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<GroupProperty> addProperty(String groupId, GroupProperties property) {
        Optional<GroupsEntity> entity = byId(groupId);
        if (entity.isPresent()) {
            GroupsEntity groups = entity.get();
            Set<GroupProperty> properties = groups.getProperties();

        }

        return Optional.empty();
    }

    @Override
    public void update(GroupsEntity groupsEntity) {
        groupsEntity.setUpdatedAt(Timestamp.from(Instant.now()));
        groupsRepository.save(groupsEntity);
    }

    @Override
    @Transactional
    public void update(GroupsEntity groupsEntity, String parentGroup) {
        if (StringUtils.hasText(parentGroup)) {
            Optional<GroupsEntity> optionalParentGroup = byId(parentGroup);
            if (optionalParentGroup.isPresent()) {
                GroupsEntity parentGroupEntity = optionalParentGroup.get();
                groupsEntity.setParent(parentGroupEntity);
                for (GroupUserEntity groupUserEntity : parentGroupEntity.getMembers()) {
                    addUsersTo(groupsEntity.getGroupId(), Collections.singletonList(groupUserEntity.getUser().getUserId()), groupUserEntity.getMembershipType(), groupUserEntity.getExpireDate(), groupUserEntity.getMembershipRole());
                }
            }
        } else {
            groupsEntity.setParent(null);
        }

        groupsEntity.setUpdatedAt(Timestamp.from(Instant.now()));
        groupsRepository.save(groupsEntity);
    }

    @Override
    public List<GroupsEntity> searchBy(String userId, GroupRole... roles) {
        if (roles == null || roles.length == 0) {
            roles = new GroupRole[]{GroupRole.ADMIN, GroupRole.USER};
        }
        List<GroupsEntity> groups = groupsRepository.findByUserGroups(userId, roles);

        return groups;
    }

    @Override
    public List<GroupsEntity> searchBy(String userId, PageRequest request, GroupCategory groupCategory, String filter, GroupRole... roles) {
        if (roles == null || roles.length == 0) {
            roles = new GroupRole[]{GroupRole.ADMIN, GroupRole.USER};
        }
        List<GroupsEntity> groups = groupsRepository.findByUserGroups(userId, roles, filter, request);

        return groups;
    }

    @Override
    public List<GroupsEntity> searchBy(String userId, PageRequest request, GroupCategory groupCategory, GroupRole... roles) {
        if (roles == null || roles.length == 0) {
            roles = new GroupRole[]{GroupRole.ADMIN, GroupRole.USER};
        }
        List<GroupsEntity> groups = groupsRepository.findByUserGroups(userId, roles, request);

        return groups;
    }

    @Override
    public List<GroupsEntity> searchBy(GroupCategory groupCategory) {
        return groupsRepository.findByGroupTypeAndNotDeleted(groupCategory);
    }

    @Override
    public List<GroupsEntity> searchParentGroupBy(String userId, GroupRole... roles) {
        if (roles == null || roles.length == 0) {
            roles = new GroupRole[]{GroupRole.ADMIN, GroupRole.USER};
        }
        return groupsRepository.findParentGroupsByUser(userId, roles);
    }

    @Override
    @Cacheable(cacheNames = GROUP_COUNTER, key = "#groupId")
    public GroupCounter counters(String groupId, GroupRole groupRole) {
        GroupCounter counter = new GroupCounter();
        counter.setService(groupsRepository.countOfServices(groupId));
        counter.setCredential(groupsRepository.countOfCredentials(groupId));

        if (groupRole == GroupRole.ADMIN) {
            counter.setMembers(groupsRepository.countOfMembers(groupId));
            counter.setPolicy(groupsRepository.countOfPolicies(groupId));
            counter.setAlarm(groupsRepository.countOfAlarms(groupId));
        }

        return counter;
    }

    @Override
    @Cacheable(cacheNames = GROUP_USER_COUNTER, key = "#userId")
    public UserGroupCounter userCounters(String userId) {
        UserGroupCounter userGroupCounter = new UserGroupCounter();
        userGroupCounter.setYours(groupsRepository.countByUsers(userId, GroupRole.ADMIN));
        userGroupCounter.setJoined(groupsRepository.countByUsers(userId, GroupRole.USER));
        userGroupCounter.setAll(userGroupCounter.getJoined() + userGroupCounter.getYours());

        return userGroupCounter;
    }

    @Override
    public boolean isEqualsMembershipRole(String groupId, String userId, GroupRole groupRole) {
        Iterable<GroupUserEntity> groupUserEntities = effectiveMembers(groupId);
        if (groupUserEntities != null) {
            for (GroupUserEntity entity : groupUserEntities) {
                if (userId.equals(entity.getId().getUserId()) && entity.getMembershipRole() == groupRole) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<GroupUserEntity> getGroupMemberByExpire(int remainDay) {
        return groupsRepository.findGroupMemberByExpire(remainDay);
    }
}