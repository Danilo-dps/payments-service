package com.danilodps.pay.domain.mappers.entities;

import com.danilodps.pay.adapters.outbound.entities.JpaTransactionEntity;
import com.danilodps.pay.domain.model.TransactionEntity;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionEntity2JpaTransactionEntity {

    private TransactionEntity2JpaTransactionEntity(){ }

    public static JpaTransactionEntity convert(TransactionEntity transactionEntity){
        return new JpaTransactionEntity(
                transactionEntity.getTransactionId(),
                transactionEntity.getAmount(),
                transactionEntity.getTransactionAt(),
                ProfileEntity2JpaProfileEntity.convert(transactionEntity.getProfileSender()),
                ProfileEntity2JpaProfileEntity.convert(transactionEntity.getProfileReceiver()));
    }

    public static List<JpaTransactionEntity> convert(List<TransactionEntity> transactionEntities){
        return transactionEntities.stream()
                .map(TransactionEntity2JpaTransactionEntity::convert)
                .collect(Collectors.toList());
    }

}
