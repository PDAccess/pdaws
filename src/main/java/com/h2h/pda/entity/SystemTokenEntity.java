package com.h2h.pda.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "system_tokens")
public class SystemTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "systemTokenSequenceGenerator")
    @SequenceGenerator(name = "systemTokenSequenceGenerator", sequenceName = "system_tokens_table_sequence", initialValue = 1, allocationSize = 1)
    private int id;

    private String name;
    private String token;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
