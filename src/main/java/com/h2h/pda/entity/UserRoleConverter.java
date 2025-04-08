package com.h2h.pda.entity;

import com.h2h.pda.pojo.user.UserRole;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {
    @Override
    public String convertToDatabaseColumn(UserRole attribute) {
        if (attribute == null) {
            return "";
        }

        return attribute.getName();
    }

    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        return UserRole.of(dbData);
    }
}
