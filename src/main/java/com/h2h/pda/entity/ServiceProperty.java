package com.h2h.pda.entity;

import com.h2h.pda.pojo.service.ServiceProperties;

import javax.persistence.Embeddable;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Objects;

@Table(name = "service_properties")
@Embeddable
public class ServiceProperty {

//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "servicePropertiesSequenceGenerator")
//    @SequenceGenerator(name = "servicePropertiesSequenceGenerator", sequenceName = "service_properties_table_sequence", initialValue = 1, allocationSize = 1)
//    private int id;

    //    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "service_id", referencedColumnName = "inventory_id")
    @Transient
    @Deprecated
    private ServiceEntity serviceEntity;

    private ServiceProperties key;
    private String value;

    public ServiceProperty() {
    }

    public ServiceProperty(ServiceProperties key, String value) {
        this.key = key;
        this.value = value;
    }

    public ServiceProperties getKey() {
        return key;
    }

    public ServiceProperty setKey(ServiceProperties key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ServiceProperty setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceProperty that = (ServiceProperty) o;
        return key == that.key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
