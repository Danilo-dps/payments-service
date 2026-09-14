package com.danilodps.pay.domain.mappers.entities.jpa;

import com.danilodps.pay.adapters.outbound.entities.JpaTransactionEntity;
import com.danilodps.pay.domain.model.TransactionEntity;

import java.util.List;
import java.util.stream.Collectors;

public class JpaTransactionEntity2TransactionEntity {

    private JpaTransactionEntity2TransactionEntity(){ }

    public static TransactionEntity convert(JpaTransactionEntity jpaTransactionEntity){
        return new TransactionEntity(
                jpaTransactionEntity.getTransactionId(),
                jpaTransactionEntity.getAmount(),
                jpaTransactionEntity.getTransactionAt(),
                JpaProfileEntity2ProfileEntity.convert(jpaTransactionEntity.getProfileSender()),
                JpaProfileEntity2ProfileEntity.convert(jpaTransactionEntity.getProfileReceiver()));
    }

    public static List<TransactionEntity> convert(List<JpaTransactionEntity> jpaTransactionEntities){
        return jpaTransactionEntities.stream()
                .map(JpaTransactionEntity2TransactionEntity::convert)
                .collect(Collectors.toList());
    }

}
