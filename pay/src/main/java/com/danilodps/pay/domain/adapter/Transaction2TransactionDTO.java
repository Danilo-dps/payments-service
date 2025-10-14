package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.model.request.TransactionRequest;
import com.danilodps.pay.domain.model.Transaction;

public class Transaction2TransactionDTO {

    private Transaction2TransactionDTO(){}

    public static TransactionRequest convertUser(Transaction transaction){
        return new TransactionRequest(transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getTransactionTimestamp(),
                transaction.getUserSender().getEmail(),
                transaction.getUserReceiver().getEmail());
    }

    public static TransactionRequest convertStore(Transaction transaction){
        return new TransactionRequest(transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getTransactionTimestamp(),
                transaction.getUserSender().getEmail(),
                transaction.getStoreReceiver().getStoreEmail());
    }

}
