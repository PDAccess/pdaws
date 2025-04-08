package com.h2h.pda.pojo;

import java.util.List;

public class CommandFilter {
    private List<String> users;
    private List<String> regex;

    public CommandFilter() {
    }

    public CommandFilter(List<String> users, List<String> regex) {
        this.users = users;
        this.regex = regex;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<String> getRegex() {
        return regex;
    }

    public void setRegex(List<String> regex) {
        this.regex = regex;
    }
}
