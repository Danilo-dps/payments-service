package pay.domain.service;

import pay.domain.dto.DepositRequestDTO;
import pay.domain.model.request.TransactionRequest;
import pay.domain.model.response.DepositResponse;
import pay.domain.model.response.TransactionResponse;

public interface OperationsService {

    DepositResponse deposit(DepositRequestDTO requestDeposit);
    TransactionResponse transfer(TransactionRequest transactionRequest);
    TransactionResponse bankStatement(TransactionRequest transactionRequest);
}
