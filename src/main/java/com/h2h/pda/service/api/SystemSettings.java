package com.h2h.pda.service.api;

import java.util.Optional;
import java.util.function.BiConsumer;

public interface SystemSettings {

    boolean hasTag(String tag);

    boolean hasTag(String category, String tag);

    boolean isTagValue(String tag);

    boolean checkTagValue(String tag, String value);

    Optional<String> tagValue(String tag);

    Optional<String> tagValue(String category, String tag);

    void updateTag(String tag, String value) throws IllegalArgumentException;

    void forEachTag(String category, BiConsumer<String, String> tagConsumer);
}
