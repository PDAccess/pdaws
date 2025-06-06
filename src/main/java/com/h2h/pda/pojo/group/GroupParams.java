package com.h2h.pda.pojo.group;

import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.pojo.EntityToDTO;

public class GroupParams implements EntityToDTO<GroupParams, GroupsEntity> {

    private String id;
    private String name;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public GroupParams wrap(GroupsEntity entity) {
        setId(entity.getGroupId());
        setName(entity.getGroupName());
        setDescription(entity.getDescription());
        return this;
    }

    @Override
    public GroupsEntity unWrap() {
        return null;
    }
}
