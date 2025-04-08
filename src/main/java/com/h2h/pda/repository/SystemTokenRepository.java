package com.h2h.pda.repository;

import com.h2h.pda.entity.SystemTokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemTokenRepository extends CrudRepository<SystemTokenEntity, Integer> {
    Optional<SystemTokenEntity> findByName(String name);
}
