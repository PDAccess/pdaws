package com.h2h.pda.pojo.service;

import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.UserDTO;
import com.h2h.pda.pojo.group.GroupsEntityWrapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceGroupUsers {
    private Timestamp expireDate;
    private GroupsEntityWrapper group;
    private List<UserDTO> users;

    public ServiceGroupUsers() {
    }

    public ServiceGroupUsers(Timestamp expireDate, GroupsEntity group, List<UserEntity> users) {
        this.expireDate = expireDate;
        this.group = new GroupsEntityWrapper(group);
        this.users = users.stream().map(u -> new UserDTO(u)).collect(Collectors.toList());
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }

    public GroupsEntityWrapper getGroup() {
        return group;
    }

    public void setGroup(GroupsEntityWrapper group) {
        this.group = group;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }
}
