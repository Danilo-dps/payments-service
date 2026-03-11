package com.danilodps.pay.application.controller;

import com.danilodps.commons.domain.model.response.DepositResponse;
import com.danilodps.commons.domain.model.response.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.danilodps.pay.domain.model.request.create.operations.DepositRequest;
import com.danilodps.pay.domain.model.request.create.operations.TransactionRequest;
import com.danilodps.pay.domain.service.OperationsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/operations")
public class OperationsController {

    private final OperationsService operationsService;

    @PostMapping("/deposit")
    @PreAuthorize("#depositRequest.userEmail == authentication.principal.profileEmail && hasAnyAuthority('USER')")
    public ResponseEntity<DepositResponse> deposit(@RequestBody @Valid DepositRequest depositRequest) {
        DepositResponse depositHistoryCreated = operationsService.deposit(depositRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(depositHistoryCreated);
    }

    @PostMapping("/transfer")
    @PreAuthorize("#transactionRequest.senderEmail == authentication.principal.profileEmail && hasAnyAuthority('USER')")
    public ResponseEntity<TransactionResponse> transfer(@RequestBody @Valid TransactionRequest transactionRequest){
        TransactionResponse transferHistoryCreated = operationsService.transfer(transactionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(transferHistoryCreated);
    }
}
