package com.h2h.pda.entity;

import com.h2h.pda.pojo.service.ServiceOs;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ServiceOsConverter implements AttributeConverter<ServiceOs, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ServiceOs attribute) {
        return attribute.getIntValue();
    }

    @Override
    public ServiceOs convertToEntityAttribute(Integer dbData) {
        return ServiceOs.of(dbData);
    }
}
