package com.h2h.pda.pojo.user;

public class UserToken {
    String user;
    String token;

    public UserToken(String user, String token) {
        this.user = user;
        this.token = token;
    }

    public String getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}
