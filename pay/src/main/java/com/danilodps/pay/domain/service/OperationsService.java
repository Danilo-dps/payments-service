package com.danilodps.pay.domain.service;

import com.danilodps.pay.domain.model.request.create.operations.DepositRequest;
import com.danilodps.pay.domain.model.request.create.operations.TransactionRequest;
import com.danilodps.pay.domain.model.response.operations.DepositResponse;
import com.danilodps.pay.domain.model.response.operations.TransactionResponse;

public interface OperationsService {

    DepositResponse deposit(DepositRequest requestDeposit);
    TransactionResponse transfer(TransactionRequest transactionRequest);
    TransactionResponse bankStatement(TransactionRequest transactionRequest);
}
