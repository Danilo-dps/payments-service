package com.danilodps.pay.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.danilodps.pay.domain.dto.DepositRequestDTO;
import com.danilodps.pay.domain.model.request.TransactionRequest;
import com.danilodps.pay.domain.model.response.DepositResponse;
import com.danilodps.pay.domain.model.response.TransactionResponse;
import com.danilodps.pay.domain.service.OperationsService;

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
    public ResponseEntity<TransactionResponse> transfer(@RequestBody TransactionRequest transactionRequest){
        TransactionResponse transferHistoryCreated = operationsService.transfer(transactionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(transferHistoryCreated);
    }
}
