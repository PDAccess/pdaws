package com.h2h.pda.entity;

import com.h2h.pda.pojo.service.ServiceType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ServiceTypeConverter implements AttributeConverter<ServiceType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ServiceType attribute) {
        return attribute.getIntValue();
    }

    @Override
    public ServiceType convertToEntityAttribute(Integer dbData) {
        return ServiceType.of(dbData);
    }
}
