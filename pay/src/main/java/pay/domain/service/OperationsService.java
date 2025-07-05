package pay.domain.service;

import pay.domain.dto.DepositRequestDTO;
import pay.domain.dto.TransferRequestDTO;
import pay.domain.record.DepositResponse;
import pay.domain.record.TransferResponse;

public interface OperationsService {

    DepositResponse deposit(DepositRequestDTO requestDeposit);
    TransferResponse transfer(TransferRequestDTO requestTransfer);
}
