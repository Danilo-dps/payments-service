package com.danilodps.pay.application.controller;

import com.danilodps.commons.domain.model.response.DepositResponse;
import com.danilodps.commons.domain.model.response.TransactionResponse;
import com.danilodps.pay.domain.model.request.create.operations.DepositRequest;
import com.danilodps.pay.domain.model.request.create.operations.TransactionRequest;
import com.danilodps.pay.domain.repository.projection.DepositProjection;
import com.danilodps.pay.domain.repository.projection.TransactionProjection;
import com.danilodps.pay.domain.service.OperationsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/operations/v1")
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

    @GetMapping("/deposit/{profileId}")
    @PreAuthorize("#profileId == authentication.principal.profileId && hasAnyAuthority('USER')")
    public ResponseEntity<List<DepositProjection>> getAllDeposit(@PathVariable String profileId){
        List<DepositProjection> listAllDeposits = operationsService.getAllDeposits(profileId);
        return listAllDeposits.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.ok(listAllDeposits);
    }

    @GetMapping("/transaction/{profileId}")
    @PreAuthorize("#profileId == authentication.principal.profileId && hasAnyAuthority('USER')")
    public ResponseEntity<List<TransactionProjection>> getAllTransaction(@PathVariable String profileId){
        List<TransactionProjection> listAllTransactions = operationsService.getAllTransactions(profileId);
        return listAllTransactions.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.ok(listAllTransactions);
    }

}
