package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.model.TransactionEntity;
import com.danilodps.pay.domain.model.response.operations.TransactionResponse;

public class TransactionEntity2TransactionResponse {

    private TransactionEntity2TransactionResponse(){}

    public static TransactionResponse convert(TransactionEntity transactionEntity){
        return TransactionResponse.builder()
                .transactionId(transactionEntity.getTransactionId())
                .amount(transactionEntity.getAmount())
                .userSenderEmail(transactionEntity.getProfileSender().getProfileEmail())
                .receiverEmail(transactionEntity.getProfileReceiver().getProfileEmail())
                .transactionTimestamp(transactionEntity.getTransactionTimestamp())
                .build();
    }
}

