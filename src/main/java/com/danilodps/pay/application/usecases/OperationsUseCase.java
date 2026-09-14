package com.danilodps.pay.application.usecases;

import com.danilodps.commons.domain.model.response.DepositResponse;
import com.danilodps.commons.domain.model.response.TransactionResponse;
import com.danilodps.pay.adapters.inbound.controller.request.create.operations.DepositRequest;
import com.danilodps.pay.adapters.inbound.controller.request.create.operations.TransactionRequest;
import com.danilodps.pay.adapters.outbound.repositories.projection.DepositProjection;
import com.danilodps.pay.adapters.outbound.repositories.projection.TransactionProjection;

import java.util.List;

public interface OperationsUseCase {

    DepositResponse deposit(DepositRequest requestDeposit);
    TransactionResponse transfer(TransactionRequest transactionRequest);
    List<DepositProjection> getAllDeposits(String profileId);
    List<TransactionProjection> getAllTransactions(String profileId);
}
