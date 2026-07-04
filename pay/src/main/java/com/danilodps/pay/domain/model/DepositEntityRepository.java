package com.danilodps.pay.domain.model;

import com.danilodps.pay.adapters.outbound.repositories.projection.DepositProjection;

import java.util.List;

public interface DepositEntityRepository {

    DepositEntity  save(DepositEntity depositEntity);
    DepositEntity  findById(String depositId);
    List<DepositProjection> findDepositsByProfileId(String profileId);
    List<DepositEntity> findAll();

}
