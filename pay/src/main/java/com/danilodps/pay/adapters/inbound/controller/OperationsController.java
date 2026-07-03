package com.danilodps.pay.adapters.inbound.controller;

import com.danilodps.commons.domain.model.response.DepositResponse;
import com.danilodps.commons.domain.model.response.TransactionResponse;
import com.danilodps.pay.adapters.inbound.controller.request.create.operations.DepositRequest;
import com.danilodps.pay.adapters.inbound.controller.request.create.operations.TransactionRequest;
import com.danilodps.pay.adapters.outbound.repositories.projection.DepositProjection;
import com.danilodps.pay.adapters.outbound.repositories.projection.TransactionProjection;
import com.danilodps.pay.application.usecases.OperationsUseCase;
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

    private final OperationsUseCase operationsUseCase;

    @PostMapping("/deposit")
    @PreAuthorize("#depositRequest.userEmail == authentication.principal.profileEmail && hasAnyAuthority('USER')")
    public ResponseEntity<DepositResponse> deposit(@RequestBody @Valid DepositRequest depositRequest) {
        DepositResponse depositHistoryCreated = operationsUseCase.deposit(depositRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(depositHistoryCreated);
    }

    @PostMapping("/transfer")
    @PreAuthorize("#transactionRequest.senderEmail == authentication.principal.profileEmail && hasAnyAuthority('USER')")
    public ResponseEntity<TransactionResponse> transfer(@RequestBody @Valid TransactionRequest transactionRequest){
        TransactionResponse transferHistoryCreated = operationsUseCase.transfer(transactionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(transferHistoryCreated);
    }

    @GetMapping("/deposit/{profileId}")
    @PreAuthorize("#profileId == authentication.principal.profileId && hasAnyAuthority('USER')")
    public ResponseEntity<List<DepositProjection>> getAllDeposit(@PathVariable String profileId){
        List<DepositProjection> listAllDeposits = operationsUseCase.getAllDeposits(profileId);
        return listAllDeposits.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.ok(listAllDeposits);
    }

    @GetMapping("/transaction/{profileId}")
    @PreAuthorize("#profileId == authentication.principal.profileId && hasAnyAuthority('USER')")
    public ResponseEntity<List<TransactionProjection>> getAllTransaction(@PathVariable String profileId){
        List<TransactionProjection> listAllTransactions = operationsUseCase.getAllTransactions(profileId);
        return listAllTransactions.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.ok(listAllTransactions);
    }

}
