package com.h2h.pda.pojo;

public interface EntityToDTO<T, K> {
    T wrap(K entity);

    K unWrap();
}
