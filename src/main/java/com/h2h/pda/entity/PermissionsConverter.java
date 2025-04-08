package com.h2h.pda.entity;

import com.h2h.pda.pojo.permission.Permissions;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PermissionsConverter implements AttributeConverter<Permissions, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Permissions attribute) {
        return attribute.getProperty();
    }

    @Override
    public Permissions convertToEntityAttribute(Integer dbData) {
        return Permissions.of(dbData);
    }
}
