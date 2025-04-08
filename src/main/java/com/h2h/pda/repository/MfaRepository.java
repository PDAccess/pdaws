package com.h2h.pda.repository;

import com.h2h.pda.entity.MfaEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MfaRepository extends CrudRepository<MfaEntity, String> {

    Optional<MfaEntity> findByUsername(String username);

}
