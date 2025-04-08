package com.h2h.pda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "alarms")
public class AlarmEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alarmSequenceGenerator")
    @SequenceGenerator(name = "alarmSequenceGenerator", sequenceName = "alarms_table_sequence", initialValue = 1, allocationSize = 1)
    private int id;

    private String name;
    private String description;
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "groupid")
    private GroupsEntity groupsEntity;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "alarm_regexes", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "regex")
    private Set<String> alarmRegexEntities;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "alarm_notifications",
            joinColumns = {@JoinColumn(name = "alarm_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<UserEntity> userEntities;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "alarmEntity")
    private Set<AlarmHistoryEntity> alarmHistoryEntities;

    private boolean active;

    @PreRemove
    private void preRemove() {
        for (AlarmHistoryEntity alarmHistoryEntity:alarmHistoryEntities) {
            alarmHistoryEntity.setAlarmEntity(null);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GroupsEntity getGroupsEntity() {
        return groupsEntity;
    }

    public void setGroupsEntity(GroupsEntity groupsEntity) {
        this.groupsEntity = groupsEntity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<String> getAlarmRegexEntities() {
        return alarmRegexEntities;
    }

    public void setAlarmRegexEntities(Set<String> alarmRegexEntities) {
        this.alarmRegexEntities = alarmRegexEntities;
    }

    public Set<UserEntity> getUserEntities() {
        return userEntities;
    }

    public void setUserEntities(Set<UserEntity> userEntities) {
        this.userEntities = userEntities;
    }

    public Set<AlarmHistoryEntity> getAlarmHistoryEntities() {
        return alarmHistoryEntities;
    }

    public void setAlarmHistoryEntities(Set<AlarmHistoryEntity> alarmHistoryEntities) {
        this.alarmHistoryEntities = alarmHistoryEntities;
    }
}
