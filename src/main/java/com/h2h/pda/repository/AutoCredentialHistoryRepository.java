package com.h2h.pda.repository;

import com.h2h.pda.entity.AutoCredentialsHistoryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoCredentialHistoryRepository extends CrudRepository<AutoCredentialsHistoryEntity, String> {
    List<AutoCredentialsHistoryEntity> findAll();

    List<AutoCredentialsHistoryEntity> findAllByInventoryId(String inventoryId);

    AutoCredentialsHistoryEntity findTop1ByCredentialIdOrderByEndAtDesc(String credentialId);

    List<AutoCredentialsHistoryEntity> findByCredentialId(String credentialId, Pageable pageable);
}
