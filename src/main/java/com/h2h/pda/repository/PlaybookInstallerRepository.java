package com.h2h.pda.repository;

import com.h2h.pda.entity.PlaybookInstallerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaybookInstallerRepository extends CrudRepository<PlaybookInstallerEntity, Integer> {

    List<PlaybookInstallerEntity> findAll();
    List<PlaybookInstallerEntity> findAllByUserId(String userId);
    @Query(value = "SELECT c FROM PlaybookInstallerEntity c WHERE NOT c.userId = :userId AND (c.isPrivate = false OR c.isPrivate IS NULL)")
    List<PlaybookInstallerEntity> findPublicAnsible(@Param("userId") String userId);

}
