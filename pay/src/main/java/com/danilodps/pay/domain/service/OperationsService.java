package com.danilodps.pay.domain.service;

import com.danilodps.commons.domain.model.response.DepositResponse;
import com.danilodps.commons.domain.model.response.TransactionResponse;
import com.danilodps.pay.domain.model.request.create.operations.DepositRequest;
import com.danilodps.pay.domain.model.request.create.operations.TransactionRequest;
import com.danilodps.pay.domain.repository.projection.DepositProjection;
import com.danilodps.pay.domain.repository.projection.TransactionProjection;

import java.util.List;

public interface OperationsService {

    DepositResponse deposit(DepositRequest requestDeposit);
    TransactionResponse transfer(TransactionRequest transactionRequest);
    List<DepositProjection> getAllDeposits(String profileId);
    List<TransactionProjection> getAllTransactions(String profileId);
}
