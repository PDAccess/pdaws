package com.h2h.pda.entity;

import com.h2h.pda.pojo.group.GroupProperties;

import javax.persistence.Embeddable;
import javax.persistence.Table;
import java.util.Objects;

@Table(name = "group_properties")
@Embeddable
public class GroupProperty {
    private GroupProperties key;
    private String value;

    public GroupProperty() {
    }

    public GroupProperty(GroupProperties key, String value) {
        this.key = key;
        this.value = value;
    }

    public GroupProperties getKey() {
        return key;
    }

    public GroupProperty setKey(GroupProperties key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public GroupProperty setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupProperty that = (GroupProperty) o;
        return key == that.key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
