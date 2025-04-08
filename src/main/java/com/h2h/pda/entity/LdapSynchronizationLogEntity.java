package com.h2h.pda.entity;

import javax.persistence.*;

@Entity
@Table(name = "ldap_synchronization_logs")
public class LdapSynchronizationLogEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ldapSynchronizationLogSequenceGenerator")
    @SequenceGenerator(name = "ldapSynchronizationLogSequenceGenerator", sequenceName = "ldap_synchronization_logs_table_sequence", initialValue = 1, allocationSize = 1)
    private int id;

    @Column(name = "group_id")
    private String groupId;

    @Column(name = "created_users")
    private String createdUsers;

    @Column(name = "added_users")
    private String addedUsers;

    @Column(name = "deleted_users")
    private String deletedUsers;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddedUsers() {
        return addedUsers;
    }

    public void setAddedUsers(String addedUsers) {
        this.addedUsers = addedUsers;
    }

    public String getDeletedUsers() {
        return deletedUsers;
    }

    public void setDeletedUsers(String deletedUsers) {
        this.deletedUsers = deletedUsers;
    }

    public String getCreatedUsers() {
        return createdUsers;
    }

    public void setCreatedUsers(String createdUsers) {
        this.createdUsers = createdUsers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
