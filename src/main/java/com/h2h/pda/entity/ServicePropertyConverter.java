package com.h2h.pda.entity;

import com.h2h.pda.pojo.service.ServiceProperties;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ServicePropertyConverter implements AttributeConverter<ServiceProperties, String> {
    @Override
    public String convertToDatabaseColumn(ServiceProperties attribute) {
        return attribute.getPropertyString();
    }

    @Override
    public ServiceProperties convertToEntityAttribute(String dbData) {
        return ServiceProperties.of(dbData).get();
    }
}
