package com.h2h.pda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h2h.pda.pojo.permission.Permissions;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "permissions")
public class PermissionEntity extends WhoCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permissionsUserSequenceGenerator")
    @SequenceGenerator(name = "permissionsUserSequenceGenerator", sequenceName = "permissions_table_sequence", initialValue = 1, allocationSize = 1)
    private int permissionId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "credential_id")
    CredentialEntity credential;

    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity user;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "permissions_collection", joinColumns = @JoinColumn(name = "permission_id"))
    @Column(name = "collection_id", nullable = false)
    @Enumerated
    Set<Permissions> permissionsSet;

    public CredentialEntity getCredential() {
        return credential;
    }

    public PermissionEntity setCredential(CredentialEntity credential) {
        this.credential = credential;
        return this;
    }

    public UserEntity getUser() {
        return user;
    }

    public PermissionEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public PermissionEntity setPermissionId(int permissionId) {
        this.permissionId = permissionId;
        return this;
    }

    public Set<Permissions> getPermissionsSet() {
        return permissionsSet;
    }

    public PermissionEntity setPermissionsSet(Set<Permissions> permissionsSet) {
        this.permissionsSet = permissionsSet;
        return this;
    }
}
