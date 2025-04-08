package com.h2h.pda.repository;

import com.h2h.pda.entity.SettingsEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingsRepository extends CrudRepository<SettingsEntity, String> {
    @Query("SELECT c FROM SettingsEntity c WHERE c.settingTag=?1")
    SettingsEntity findBySettingTag(String tag);

    @Query("SELECT c FROM SettingsEntity c WHERE c.settingCategory=?1")
    List<SettingsEntity> findBySettingCategory(String category);
}
