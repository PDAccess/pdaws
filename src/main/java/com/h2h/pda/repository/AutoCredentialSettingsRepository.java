package com.h2h.pda.repository;

import com.h2h.pda.entity.AutoCredantialSettingsEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoCredentialSettingsRepository extends CrudRepository<AutoCredantialSettingsEntity, String> {

    List<AutoCredantialSettingsEntity> findAll();

    AutoCredantialSettingsEntity findByCredantialId(String credentialId);

}
