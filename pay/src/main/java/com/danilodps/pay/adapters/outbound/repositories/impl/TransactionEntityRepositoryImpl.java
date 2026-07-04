package com.danilodps.pay.adapters.outbound.repositories.impl;

import com.danilodps.pay.adapters.outbound.entities.JpaTransactionEntity;
import com.danilodps.pay.adapters.outbound.repositories.JpaTransactionEntityRepository;
import com.danilodps.pay.adapters.outbound.repositories.projection.TransactionProjection;
import com.danilodps.pay.domain.mappers.entities.jpa.JpaTransactionEntity2TransactionEntity;
import com.danilodps.pay.domain.mappers.entities.core.TransactionEntity2JpaTransactionEntity;
import com.danilodps.pay.domain.model.TransactionEntity;
import com.danilodps.pay.domain.model.TransactionEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionEntityRepositoryImpl implements TransactionEntityRepository {

    private final JpaTransactionEntityRepository jpaTransactionEntityRepository;

    public TransactionEntityRepositoryImpl(JpaTransactionEntityRepository jpaTransactionEntityRepository) {
        this.jpaTransactionEntityRepository = jpaTransactionEntityRepository;
    }

    @Override
    public TransactionEntity save(TransactionEntity transactionEntity) {
        JpaTransactionEntity jpaTransactionEntity = TransactionEntity2JpaTransactionEntity.convert(transactionEntity);
        this.jpaTransactionEntityRepository.save(jpaTransactionEntity);
        return JpaTransactionEntity2TransactionEntity.convert(jpaTransactionEntity);
    }

    @Override
    public TransactionEntity findByTransactionId(String transactionId) {
        JpaTransactionEntity jpaTransactionEntity =  this.jpaTransactionEntityRepository.findByTransactionId(transactionId);
        return JpaTransactionEntity2TransactionEntity.convert(jpaTransactionEntity);
    }

    @Override
    public List<TransactionProjection> findAll(String profileId) {
        return this.jpaTransactionEntityRepository.findTransactionsByProfileId(profileId);
    }

}
