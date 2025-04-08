package com.h2h.pda.pojo.policy;

import com.h2h.pda.entity.SudoPolicyEntity;
import com.h2h.pda.pojo.EntityToDTO;

public class SudoPolicyWrapper implements EntityToDTO<SudoPolicyWrapper, SudoPolicyEntity> {

    @Override
    public SudoPolicyWrapper wrap(SudoPolicyEntity entity) {
        return new SudoPolicyWrapper();
    }

    @Override
    public SudoPolicyEntity unWrap() {
        return new SudoPolicyEntity();
    }
}
