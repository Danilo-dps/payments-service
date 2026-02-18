package com.danilodps.pay.domain.service;

import com.danilodps.commons.domain.model.response.DepositResponse;
import com.danilodps.commons.domain.model.response.TransactionResponse;
import com.danilodps.pay.domain.model.request.create.operations.DepositRequest;
import com.danilodps.pay.domain.model.request.create.operations.TransactionRequest;

public interface OperationsService {

    DepositResponse deposit(DepositRequest requestDeposit);
    TransactionResponse transfer(TransactionRequest transactionRequest);
    TransactionResponse bankStatement(TransactionRequest transactionRequest);
}
