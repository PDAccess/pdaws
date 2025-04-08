package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.h2h.pda.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public class LiveSessionParams {

    private List<UserDTO> userEntities;
    private Pagination pagination;

    @JsonProperty("Sort")
    private String sort;

    public LiveSessionParams() {
    }

    public LiveSessionParams(List<UserEntity> userEntities, Pagination pagination, String sort) {
        this.userEntities = userEntities.stream().map(u -> new UserDTO(u)).collect(Collectors.toList());
        this.pagination = pagination;
        this.sort = sort;
    }

    public List<UserDTO> getUserEntities() {
        return userEntities;
    }

    public void setUserEntities(List<UserDTO> userEntities) {
        this.userEntities = userEntities;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
