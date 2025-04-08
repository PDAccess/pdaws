package com.h2h.pda.entity;

import javax.persistence.Embeddable;
import javax.persistence.Table;
import java.util.Objects;

@Table(name="policyregex")
@Embeddable
public class PolicyRegexEntity {

    private String regex;

    public PolicyRegexEntity() {
    }

    public PolicyRegexEntity(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolicyRegexEntity that = (PolicyRegexEntity) o;
        return Objects.equals(regex, that.regex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regex);
    }
}