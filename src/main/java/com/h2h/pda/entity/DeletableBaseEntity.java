package com.h2h.pda.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@MappedSuperclass
public abstract class DeletableBaseEntity extends BaseEntity {
    @Column(name = "deleted_at")
    Timestamp deletedAt;

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public DeletableBaseEntity setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
