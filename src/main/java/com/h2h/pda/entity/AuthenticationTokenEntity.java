package com.h2h.pda.entity;

import javax.persistence.*;

@Entity
@Table(name = "authentication_tokens")
public class AuthenticationTokenEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authenticationTokensSequenceGenerator")
    @SequenceGenerator(name = "authenticationTokensSequenceGenerator", sequenceName = "authentication_tokens_table_sequence", initialValue = 1, allocationSize = 1)
    private long id;

    private String username;
    private String token;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
