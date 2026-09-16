package com.danilodps.pay.domain.mappers;

import com.danilodps.commons.domain.model.response.TransactionResponse;
import com.danilodps.pay.domain.model.entities.TransactionEntity;

public class TransactionEntity2TransactionResponse {

    private TransactionEntity2TransactionResponse(){}

    public static TransactionResponse convert(TransactionEntity transactionEntity, String sender, String receiver){
        return new TransactionResponse(
                transactionEntity.getTransactionId(),
                transactionEntity.getAmount(),
                transactionEntity.getTransactionAt(),
                sender,
                receiver);
    }

}

