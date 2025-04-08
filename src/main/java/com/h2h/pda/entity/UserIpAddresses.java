package com.h2h.pda.entity;

import javax.persistence.*;

@Table(name = "user_ip_addresses")
@Entity
public class UserIpAddresses {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "ip_address")
    private String ipAddress;

    public UserIpAddresses() {
    }

    public UserIpAddresses(String userId, String ipAddress) {
        this.userId = userId;
        this.ipAddress = ipAddress;
    }

    public UserIpAddresses(Integer id, String userId, String ipAddress) {
        this.id = id;
        this.userId = userId;
        this.ipAddress = ipAddress;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
