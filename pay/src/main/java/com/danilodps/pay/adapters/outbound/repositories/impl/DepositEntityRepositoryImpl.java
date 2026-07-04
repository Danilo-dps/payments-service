package com.danilodps.pay.adapters.outbound.repositories.impl;

import com.danilodps.pay.adapters.outbound.entities.JpaDepositEntity;
import com.danilodps.pay.adapters.outbound.repositories.JpaDepositEntityRepository;
import com.danilodps.pay.adapters.outbound.repositories.projection.DepositProjection;
import com.danilodps.pay.domain.mappers.entities.core.DepositEntity2JpaDepositEntity;
import com.danilodps.pay.domain.mappers.entities.jpa.JpaDepositEntity2DepositEntity;
import com.danilodps.pay.domain.model.DepositEntity;
import com.danilodps.pay.domain.model.DepositEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.Objects.nonNull;

@Repository
public class DepositEntityRepositoryImpl implements DepositEntityRepository {

    private final JpaDepositEntityRepository jpaDepositEntityRepository;

    public DepositEntityRepositoryImpl(JpaDepositEntityRepository jpaDepositEntityRepository) {
        this.jpaDepositEntityRepository = jpaDepositEntityRepository;
    }

    @Override
    public DepositEntity save(DepositEntity depositEntity) {
        JpaDepositEntity jpaDepositEntity = this.jpaDepositEntityRepository.saveAndFlush(DepositEntity2JpaDepositEntity.convert(depositEntity));
        return JpaDepositEntity2DepositEntity.convert(jpaDepositEntity);
    }

    @Override
    public DepositEntity findById(String depositId) {
        JpaDepositEntity jpaDepositEntity =  this.jpaDepositEntityRepository.findById(depositId).orElse(null);
        if (nonNull(jpaDepositEntity)){
            return JpaDepositEntity2DepositEntity.convert(jpaDepositEntity);
        }
        return null;
    }

    @Override
    public List<DepositProjection> findDepositsByProfileId(String profileId) {
        return this.jpaDepositEntityRepository.findDepositsByProfileId(profileId);
    }

    @Override
    public List<DepositEntity> findAll() {
        List<JpaDepositEntity>  jpaDepositEntities = this.jpaDepositEntityRepository.findAll();
        if (!jpaDepositEntities.isEmpty()){
            return JpaDepositEntity2DepositEntity.convert(jpaDepositEntities);
        }
        return List.of();
    }

}
