package com.h2h.pda.repository;

import com.h2h.pda.entity.ExecAuth;
import com.h2h.pda.pojo.ExecAuthWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExecAuthRepository extends CrudRepository<ExecAuth, Integer> {

    @Query("SELECT c FROM ExecAuth c WHERE c.serviceId=:service_id AND (:filter IS NULL OR LOWER(c.user) LIKE %:filter%)")
    Page<ExecAuth> findByServiceId(@Param("service_id") String serviceId, @Param("filter") String filter, Pageable pageable);

    @Query("SELECT new com.h2h.pda.pojo.ExecAuthWrapper(c, s.name) FROM ExecAuth c LEFT JOIN ServiceEntity s ON s.inventoryId=c.serviceId WHERE c.groupId=:group_id AND (:filter IS NULL OR LOWER(c.user) LIKE %:filter%)")
    List<ExecAuthWrapper> findByGroupId(@Param("group_id") String groupId, @Param("filter") String filter, Pageable pageable);

}
