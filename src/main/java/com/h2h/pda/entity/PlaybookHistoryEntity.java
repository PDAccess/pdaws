package com.h2h.pda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

@Entity
@Table(name = "ansible_histories")
public class PlaybookHistoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ansibleHistorySequenceGenerator")
    @SequenceGenerator(name = "ansibleHistorySequenceGenerator", sequenceName = "ansible_histories_table_sequence", initialValue = 1, allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "installer_id", referencedColumnName = "id")
    @Fetch(FetchMode.JOIN)
    private PlaybookInstallerEntity installerEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  referencedColumnName = "user_id")
    @Fetch(FetchMode.JOIN)
    private UserEntity userEntity;

    private boolean finished;
    private boolean success;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PlaybookInstallerEntity getInstallerEntity() {
        return installerEntity;
    }

    public void setInstallerEntity(PlaybookInstallerEntity installerEntity) {
        this.installerEntity = installerEntity;
    }
    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
