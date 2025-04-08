package com.h2h.pda.entity;

import com.h2h.pda.pojo.policy.PolicyBehavior;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("2")
public class SudoPolicyEntity extends PolicyEntity {

    private PolicyBehavior behavior;

    @Column(name = "sudo_user")
    private String sudoUser;

    @Column(name = "sudo_option")
    private String option;

    @Column(name = "run_as_user")
    private String sudoRunAsUser;

    @Column(name = "run_as_group")
    private String sudoRunAsGroup;

    @Column(name = "not_before")
    private String sudoNotBefore;

    @Column(name = "not_after")
    private String sudoNotAfter;

    @Column(name = "sudo_host")
    private String sudoHost;

    @Column(name = "sudo_order")
    private Integer sudoOrder;

    public String getSudoUser() {
        return sudoUser;
    }

    public SudoPolicyEntity setSudoUser(String sudoUser) {
        this.sudoUser = sudoUser;
        return this;
    }

    public String getOption() {
        return option;
    }

    public SudoPolicyEntity setOption(String option) {
        this.option = option;
        return this;
    }

    public String getSudoRunAsUser() {
        return sudoRunAsUser;
    }

    public SudoPolicyEntity setSudoRunAsUser(String sudoRunAsUser) {
        this.sudoRunAsUser = sudoRunAsUser;
        return this;
    }

    public String getSudoRunAsGroup() {
        return sudoRunAsGroup;
    }

    public SudoPolicyEntity setSudoRunAsGroup(String sudoRunAsGroup) {
        this.sudoRunAsGroup = sudoRunAsGroup;
        return this;
    }

    public String getSudoNotBefore() {
        return sudoNotBefore;
    }

    public SudoPolicyEntity setSudoNotBefore(String sudoNotBefore) {
        this.sudoNotBefore = sudoNotBefore;
        return this;
    }

    public String getSudoNotAfter() {
        return sudoNotAfter;
    }

    public SudoPolicyEntity setSudoNotAfter(String sudoNotAfter) {
        this.sudoNotAfter = sudoNotAfter;
        return this;
    }

    public String getSudoHost() {
        return sudoHost;
    }

    public SudoPolicyEntity setSudoHost(String sudoHost) {
        this.sudoHost = sudoHost;
        return this;
    }

    public Integer getSudoOrder() {
        return sudoOrder;
    }

    public SudoPolicyEntity setSudoOrder(Integer sudoOrder) {
        this.sudoOrder = sudoOrder;
        return this;
    }

    public PolicyBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior(PolicyBehavior behavior) {
        this.behavior = behavior;
    }
}