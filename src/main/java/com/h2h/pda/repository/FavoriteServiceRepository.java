package com.h2h.pda.repository;

import com.h2h.pda.entity.FavoriteServiceEntity;
import com.h2h.pda.entity.FavoriteServicePK;
import com.h2h.pda.entity.ServiceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FavoriteServiceRepository extends CrudRepository<FavoriteServiceEntity, FavoriteServicePK> {
    @Transactional
    Integer deleteByIdUserIdAndIdServiceId(String userId, String serviceId);

    @Query("SELECT c.service FROM FavoriteServiceEntity c WHERE c.user.userId=?1")
    List<ServiceEntity> findByUser(String userId);

    @Query("SELECT c FROM FavoriteServiceEntity c WHERE c.id.serviceId=?1")
    List<FavoriteServiceEntity> findByService(String serviceId);
}
