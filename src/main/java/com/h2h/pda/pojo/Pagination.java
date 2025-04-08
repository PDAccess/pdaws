package com.h2h.pda.pojo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Function;

public class Pagination {
    Integer perPage;
    Integer currentPage;
    String sort;
    List<String> usersFilter;
    String filter;
    List<String> servicesFilter;
    Timestamp startTime;
    Timestamp endTime;
    String category;

    public static Pagination of(Integer currentPage, Integer perPage) {
        return new Pagination(currentPage, perPage, "name");
    }

    public Pagination() {
    }

    public Pagination(String filter) {
        this.filter = filter;
    }

    public Pagination(Integer currentPage, Integer perPage) {
        this.perPage = perPage;
        this.currentPage = currentPage;
    }

    public Pagination(Integer currentPage, Integer perPage, String sort) {
        this.perPage = perPage;
        this.currentPage = currentPage;
        this.sort = sort;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public Pagination setPerPage(Integer perPage) {
        this.perPage = perPage;
        return this;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public Pagination setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public String getSort() {
        return sort;
    }

    public Pagination setSort(String sort) {
        this.sort = sort;
        return this;
    }

    public List<String> getUsersFilter() {
        return usersFilter;
    }

    public Pagination setUsersFilter(List<String> usersFilter) {
        this.usersFilter = usersFilter;
        return this;
    }

    public String getFilter() {
        return filter;
    }

    public Pagination setFilter(String filter) {
        this.filter = filter;
        return this;
    }

    public List<String> getServicesFilter() {
        return servicesFilter;
    }

    public Pagination setServicesFilter(List<String> servicesFilter) {
        this.servicesFilter = servicesFilter;
        return this;
    }

    public PageRequest toRequest(Function<String, Sort> sortMapper) {
        return PageRequest.of(getCurrentPage(), getPerPage(), sortMapper.apply(getSort()));
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
