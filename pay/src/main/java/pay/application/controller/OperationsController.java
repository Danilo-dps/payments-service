package pay.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pay.domain.dto.DepositRequestDTO;
import pay.domain.dto.TransferRequestDTO;
import pay.domain.record.DepositResponse;
import pay.domain.record.TransferResponse;
import pay.domain.service.OperationsService;

@RestController
@RequestMapping("/operations")
public class OperationsController {

    private final OperationsService operationsService;

    OperationsController(OperationsService operationsService){
        this.operationsService = operationsService;
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DepositResponse> deposit(@RequestBody DepositRequestDTO requestDeposit) {
        DepositResponse depositHistoryCreated = operationsService.deposit(requestDeposit);
        return ResponseEntity.status(HttpStatus.CREATED).body(depositHistoryCreated);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequestDTO requestTransfer){
        TransferResponse transferHistoryCreated = operationsService.transfer(requestTransfer);
        return ResponseEntity.status(HttpStatus.CREATED).body(transferHistoryCreated);
    }
}
