package com.h2h.pda.service.impl;

import com.h2h.pda.entity.SettingsEntity;
import com.h2h.pda.repository.SettingsRepository;
import com.h2h.pda.service.api.SystemSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.function.BiConsumer;

@Service
public class SystemSettingsImpl implements SystemSettings {
    private static final Logger log = LoggerFactory.getLogger(SystemSettingsImpl.class);

    @Autowired
    SettingsRepository settingsRepository;

    @Override
    public boolean hasTag(String tag) {
        SettingsEntity systemMfaStatus = settingsRepository.findBySettingTag(tag);
        return systemMfaStatus != null;
    }

    @Override
    public boolean hasTag(String category, String tag) {
        StringBuilder value = new StringBuilder();
        forEachTag(category, (t, v) -> {
            if (t.equals(tag)) {
                value.append(v);
            }
        });

        return value.length() > 0;
    }


    @Override
    public boolean isTagValue(String tag) {
        SettingsEntity systemMfaStatus = settingsRepository.findBySettingTag(tag);
        String value = systemMfaStatus.getSettingValue();
        return value != null && value.trim().equals("1");
    }

    @Override
    public boolean checkTagValue(String tag, String value) {
        SettingsEntity settingsEntity = settingsRepository.findBySettingTag(tag);
        if (settingsEntity == null) {
            return false;
        }

        return settingsEntity.getSettingValue().equals(value);
    }

    @Override
    public Optional<String> tagValue(String tag) {
        SettingsEntity settingsEntity = settingsRepository.findBySettingTag(tag);
        return settingsEntity == null ? Optional.empty() : Optional.ofNullable(settingsEntity.getSettingValue());
    }

    @Override
    public Optional<String> tagValue(String category, String tag) {
        StringBuilder value = new StringBuilder();
        forEachTag(category, (t, v) -> {
            if (t.equals(tag)) {
                value.append(v);
            }
        });

        return Optional.of(value.toString());
    }


    @Override
    public void updateTag(String tag, String value) throws IllegalArgumentException {
        SettingsEntity settingTag = settingsRepository.findBySettingTag(tag);

        if (settingTag == null) {
            log.warn(String.format("there is no configuration: tag: %s", tag));
            throw new IllegalArgumentException(String.format("there is no configuration: tag: %s", tag));
        }

        settingTag.setUpdatedAt(Timestamp.from(Instant.now()));
        settingTag.setSettingTag(tag);
        settingTag.setSettingValue(value);

        settingsRepository.save(settingTag);
    }

    @Override
    public void forEachTag(String category, BiConsumer<String, String> tagConsumer) {
        for (SettingsEntity settingsEntity : settingsRepository.findBySettingCategory(category)) {
            tagConsumer.accept(settingsEntity.getSettingTag(), settingsEntity.getSettingValue());
        }
    }
}
