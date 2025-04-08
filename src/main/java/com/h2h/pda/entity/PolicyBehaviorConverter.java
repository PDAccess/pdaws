package com.h2h.pda.entity;

import com.h2h.pda.pojo.policy.PolicyBehavior;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Optional;

@Converter(autoApply = true)
public class PolicyBehaviorConverter implements AttributeConverter<PolicyBehavior, String> {
    @Override
    public String convertToDatabaseColumn(PolicyBehavior attribute) {
        if (attribute == null) return null;
        return attribute.getPropertyString();
    }

    @Override
    public PolicyBehavior convertToEntityAttribute(String dbData) {
        Optional<PolicyBehavior> policyBehaviorOptional = PolicyBehavior.of(dbData);
        return policyBehaviorOptional.orElse(null);
    }
}
