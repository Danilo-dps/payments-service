package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.model.request.TransactionRequest;
import com.danilodps.pay.domain.model.response.TransactionResponse;

public class Transaction2TransactionResponse {

    private Transaction2TransactionResponse(){}

    public static TransactionResponse convertToUser(TransactionRequest transactionRequest){
        return new TransactionResponse(
                transactionRequest.getTransactionId(),
                transactionRequest.getAmount(),
                transactionRequest.getTransactionTimestamp(),
                transactionRequest.getUserSenderEmail(),
                transactionRequest.getReceiverEmail(),
                transactionRequest.getUserSenderName(),
                transactionRequest.getReceiverName());
    }
}

