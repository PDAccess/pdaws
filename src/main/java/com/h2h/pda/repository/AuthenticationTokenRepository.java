package com.h2h.pda.repository;

import com.h2h.pda.entity.AuthenticationTokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthenticationTokenRepository extends CrudRepository<AuthenticationTokenEntity, Integer> {

    AuthenticationTokenEntity findByUsername(String username);

    List<AuthenticationTokenEntity> findAll();

}
