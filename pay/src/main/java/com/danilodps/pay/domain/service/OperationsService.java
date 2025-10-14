package com.danilodps.pay.domain.service;

import com.danilodps.pay.domain.dto.DepositRequestDTO;
import com.danilodps.pay.domain.model.request.TransactionRequest;
import com.danilodps.pay.domain.model.response.DepositResponse;
import com.danilodps.pay.domain.model.response.TransactionResponse;

public interface OperationsService {

    DepositResponse deposit(DepositRequestDTO requestDeposit);
    TransactionResponse transfer(TransactionRequest transactionRequest);
    TransactionResponse bankStatement(TransactionRequest transactionRequest);
}
