package com.h2h.pda.entity;

import com.h2h.pda.pojo.group.GroupProperties;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class GroupPropertyConverter implements AttributeConverter<GroupProperties, String> {
    @Override
    public String convertToDatabaseColumn(GroupProperties attribute) {
        return attribute.getPropertyString();
    }

    @Override
    public GroupProperties convertToEntityAttribute(String dbData) {
        return GroupProperties.of(dbData);
    }
}
