package com.danilodps.pay.domain.model;

import com.danilodps.pay.adapters.outbound.repositories.projection.TransactionProjection;

import java.util.List;

public interface TransactionEntityRepository {

    TransactionEntity save(TransactionEntity transactionEntity);
    TransactionEntity findByTransactionId(String transactionId);
    List<TransactionProjection> findAll(String profileId);

}
