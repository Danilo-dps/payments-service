package com.danilodps.pay.adapters.outbound.repositories.impl;

import com.danilodps.pay.adapters.outbound.repositories.JpaTransactionEntityRepository;
import com.danilodps.pay.adapters.outbound.repositories.projection.TransactionProjection;
import com.danilodps.pay.domain.model.TransactionEntityRepository;
import com.danilodps.pay.domain.model.entities.TransactionEntity;
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
        return this.jpaTransactionEntityRepository.save(transactionEntity);
    }

    @Override
    public TransactionEntity findByTransactionId(String transactionId) {
        return this.jpaTransactionEntityRepository.findByTransactionId(transactionId);
    }

    @Override
    public List<TransactionProjection> findAll(String profileId) {
        return this.jpaTransactionEntityRepository.findTransactionsByProfileId(profileId);
    }

}
