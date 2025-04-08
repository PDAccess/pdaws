package com.h2h.pda.pojo.policy;

import com.h2h.pda.entity.ProxyPolicyEntity;
import com.h2h.pda.pojo.EntityToDTO;

public class ProxyPolicyWrapper implements EntityToDTO<ProxyPolicyWrapper, ProxyPolicyEntity> {
    @Override
    public ProxyPolicyWrapper wrap(ProxyPolicyEntity entity) {
        return new ProxyPolicyWrapper();
    }

    @Override
    public ProxyPolicyEntity unWrap() {
        return new ProxyPolicyEntity();
    }
}
