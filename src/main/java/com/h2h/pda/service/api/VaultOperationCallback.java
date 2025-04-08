package com.h2h.pda.service.api;

import org.springframework.lang.Nullable;
import org.springframework.web.client.RestOperations;

@FunctionalInterface
public interface VaultOperationCallback<T> {
    @Nullable
    T doWithRestOperations(RestOperations restOperations);
}
