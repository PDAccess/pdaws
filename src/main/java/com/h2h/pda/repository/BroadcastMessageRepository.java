package com.h2h.pda.repository;

import com.h2h.pda.entity.BroadcastMessageEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BroadcastMessageRepository extends CrudRepository<BroadcastMessageEntity, String> {

    @Override
    @Query("select e from #{#entityName} e where e.isDeleted=false")
    Iterable<BroadcastMessageEntity> findAll();

    //Soft delete.
    @Query("update #{#entityName} e set e.isDeleted=true where e.id=?1")
    @Modifying
    @Transactional
    void softDelete(String id);
}
