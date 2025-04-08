package com.h2h.pda.pojo;

import java.util.List;

public class FileFilter {
    private List<String> users;
    private List<String> paths;

    public FileFilter() {
    }

    public FileFilter(List<String> users, List<String> paths) {
        this.users = users;
        this.paths = paths;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
}
