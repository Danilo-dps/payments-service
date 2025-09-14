package pay.domain.service;

import pay.domain.dto.DepositRequestDTO;
import pay.domain.record.DepositResponse;
import pay.domain.record.TransferRequest;
import pay.domain.record.TransferResponse;

import java.math.BigDecimal;

public interface OperationsService {

    DepositResponse deposit(DepositRequestDTO requestDeposit);
    TransferResponse transfer(TransferRequest transferRequest);
}
