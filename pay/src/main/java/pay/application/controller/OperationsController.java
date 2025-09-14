package pay.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pay.domain.dto.DepositRequestDTO;
import pay.domain.record.DepositResponse;
import pay.domain.record.TransferRequest;
import pay.domain.record.TransferResponse;
import pay.domain.service.OperationsService;

import java.math.BigDecimal;

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
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequest transferRequest){
        TransferResponse transferHistoryCreated = operationsService.transfer(transferRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(transferHistoryCreated);
    }
}
