package com.h2h.pda.entity;

import com.h2h.pda.pojo.policy.PolicyBehavior;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("1")
public class ProxyPolicyEntity extends PolicyEntity {

    private PolicyBehavior behavior;

    @Column(name = "service_type")
    private String servicetype;

    @Column(name = "service_meta_type")
    private String servicemeta;

    @Column(name = "operating_system")
    private String operatingsystem;

    @Column(name = "upper_id")
    private String upperid;

    @Column(name = "id_type")
    private String idtype;

    public ProxyPolicyEntity() {
        // Constructor
    }

    public PolicyBehavior getBehavior() {
        return behavior;
    }

    public ProxyPolicyEntity setBehavior(PolicyBehavior behavior) {
        this.behavior = behavior;
        return this;
    }

    public String getServicetype() {
        return servicetype;
    }

    public ProxyPolicyEntity setServicetype(String servicetype) {
        this.servicetype = servicetype;
        return this;
    }

    public String getServicemeta() {
        return servicemeta;
    }

    public ProxyPolicyEntity setServicemeta(String servicemeta) {
        this.servicemeta = servicemeta;
        return this;
    }

    public String getOperatingsystem() {
        return operatingsystem;
    }

    public ProxyPolicyEntity setOperatingsystem(String operatingsystem) {
        this.operatingsystem = operatingsystem;
        return this;
    }

    public String getUpperid() {
        return upperid;
    }

    public ProxyPolicyEntity setUpperid(String upperid) {
        this.upperid = upperid;
        return this;
    }

    public String getIdtype() {
        return idtype;
    }

    public ProxyPolicyEntity setIdtype(String idtype) {
        this.idtype = idtype;
        return this;
    }
}
