package com.h2h.pda.repository;

import com.h2h.pda.entity.Oauth2Entity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Oauth2Repository extends CrudRepository<Oauth2Entity, String> {
    @Query(value = "SELECT c FROM Oauth2Entity c WHERE c.deletedAt IS NULL")
    List<Oauth2Entity> findByNotDeleted();
}