package com.h2h.pda.service.api;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.group.*;
import org.springframework.data.domain.PageRequest;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface GroupOps {
    List<UserEntity> effectiveUsers(String groupId);

    Iterable<GroupUserEntity> effectiveMembers(String groupId);

    List<ServiceEntity> effectiveServices(String groupId);

    int addServicesTo(String groupId, List<String> serviceList);

    int removeServicesFrom(String groupId, List<String> serviceList);

    int addUsersTo(String groupId, List<String> userList, GroupMembership membership, GroupRole... roles);

    int addUsersTo(String groupId, List<String> userList, GroupMembership membership, Timestamp expireDate, GroupRole... roles);

    int addUsersTo(String groupId, List<String> userList, GroupRole... roles);

    int removeUsersFrom(String groupId, List<String> userList);

    void deleteById(String groupId) throws IllegalArgumentException;

    String newGroup(GroupsEntity groupsEntity, UserEntity currentUser);

    Optional<GroupsEntity> byId(String groupId);

    Optional<GroupsEntity> byName(String groupName);

    Optional<GroupProperty> byProperty(String groupId, GroupProperties properties);

    Optional<GroupProperty> addProperty(String groupId, GroupProperties properties);

    void update(GroupsEntity groupsEntity);

    void update(GroupsEntity groupsEntity, String parentGroup);

    List<GroupsEntity> searchBy(String userId, GroupRole... roles);

    List<GroupsEntity> searchBy(String userId, PageRequest request, GroupCategory groupCategory, String filter, GroupRole... roles);

    List<GroupsEntity> searchBy(String userId, PageRequest request, GroupCategory groupCategory, GroupRole... roles);

    List<GroupsEntity> searchBy(GroupCategory groupCategory);

    List<GroupsEntity> searchParentGroupBy(String userId, GroupRole... roles);

    GroupCounter counters(String groupId, GroupRole groupRole);

    UserGroupCounter userCounters(String userId);

    boolean isEqualsMembershipRole(String groupId, String userId, GroupRole groupRole);

    List<GroupUserEntity> getGroupMemberByExpire(int remainDay);
}
