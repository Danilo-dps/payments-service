package com.danilodps.pay.adapters.outbound.repositories.impl;

import com.danilodps.pay.adapters.outbound.repositories.JpaDepositEntityRepository;
import com.danilodps.pay.adapters.outbound.repositories.projection.DepositProjection;
import com.danilodps.pay.domain.model.DepositEntityRepository;
import com.danilodps.pay.domain.model.entities.DepositEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DepositEntityRepositoryImpl implements DepositEntityRepository {

    private final JpaDepositEntityRepository jpaDepositEntityRepository;

    public DepositEntityRepositoryImpl(JpaDepositEntityRepository jpaDepositEntityRepository) {
        this.jpaDepositEntityRepository = jpaDepositEntityRepository;
    }

    @Override
    public DepositEntity save(DepositEntity depositEntity) {
        return this.jpaDepositEntityRepository.saveAndFlush(depositEntity);
    }

    @Override
    public DepositEntity findById(String depositId) {
        return this.jpaDepositEntityRepository.findById(depositId).orElse(null);
    }

    @Override
    public List<DepositProjection> findDepositsByProfileId(String profileId) {
        return this.jpaDepositEntityRepository.findDepositsByProfileId(profileId);
    }

    @Override
    public List<DepositEntity> findAll() {
        return this.jpaDepositEntityRepository.findAll();
    }

}
