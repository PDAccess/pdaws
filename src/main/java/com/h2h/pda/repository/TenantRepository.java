package com.h2h.pda.repository;

import com.h2h.pda.entity.TenantEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository extends CrudRepository<TenantEntity, String> {
    @Query("SELECT c FROM TenantEntity c WHERE c.deletedAt IS NULL")
    List<TenantEntity> findByNotDeleted();
}
