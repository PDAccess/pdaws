package com.h2h.pda.repository;

import com.h2h.pda.entity.PlaybookInstallerServiceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaybookInstallerServiceRepository extends CrudRepository<PlaybookInstallerServiceEntity, Integer> {

    List<PlaybookInstallerServiceEntity> findAll();
    List<PlaybookInstallerServiceEntity> findAllByIdInstallerId(Integer id);
    List<PlaybookInstallerServiceEntity> findAllByIdServiceId(String id);
    void deleteAllByIdServiceId(String id);
}
