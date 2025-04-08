package com.h2h.pda.entity;

import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.pojo.user.UserShell;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class UserShellConverter implements AttributeConverter<UserShell, String> {
    @Override
    public String convertToDatabaseColumn(UserShell attribute) {
        if (attribute == null) {
            return "";
        }

        return attribute.getName();
    }

    @Override
    public UserShell convertToEntityAttribute(String dbData) {
        return UserShell.of(dbData);
    }
}
