package com.danilodps.pay.domain.service.impl;

import com.danilodps.pay.domain.adapter.TransactionEntity2TransactionResponse;
import com.danilodps.pay.domain.model.*;
import com.danilodps.pay.domain.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.danilodps.pay.application.exceptions.InsufficientBalanceException;
import com.danilodps.pay.application.exceptions.InvalidValueException;
import com.danilodps.pay.application.exceptions.NotFoundException;
import com.danilodps.pay.domain.adapter.DepositEntity2DepositResponse;
//import com.danilodps.pay.domain.config.KafkaEventProducer;
import com.danilodps.pay.domain.model.request.create.operations.DepositRequest;
import com.danilodps.pay.domain.model.request.create.operations.TransactionRequest;
import com.danilodps.pay.domain.model.response.operations.DepositResponse;
import com.danilodps.pay.domain.model.response.operations.TransactionResponse;
import com.danilodps.pay.domain.service.OperationsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationsServiceImpl implements OperationsService {

//    private final KafkaEventProducer kafkaEventProducer;
    private final ProfileEntityRepository profileEntityRepository;
    private final DepositEntityRepository depositEntityRepository;
    private final TransactionEntityRepository transactionEntityRepository;

    @Override
    @Transactional
    public DepositResponse deposit(DepositRequest requestDeposit) {
        log.info("Inicializando processo de depósito");
        if (requestDeposit.amount() == null || requestDeposit.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidValueException();
        }

        ProfileEntity profileEntity = profileEntityRepository.findByProfileEmail(requestDeposit.email())
                .orElseThrow(() -> new NotFoundException(requestDeposit.email()));

        DepositEntity deposit = DepositEntity.builder()
                .depositTimestamp(LocalDateTime.now())
                .amount(requestDeposit.amount())
                .profileEntity(profileEntity)
                .build();

        profileEntity.setBalance(profileEntity.getBalance().add(requestDeposit.amount()));
        depositEntityRepository.saveAndFlush(deposit);
        profileEntityRepository.saveAndFlush(profileEntity);

        //kafkaEventProducer.publishKafkaDepositEventNotification(DepositEntity2DepositResponse.convert(persistedDeposit));
        return DepositEntity2DepositResponse.convert(deposit);
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransactionRequest transactionRequest) {
        if (transactionRequest.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidValueException();
        }

        ProfileEntity profileSender = profileEntityRepository.findAndLockByProfileEmail(transactionRequest.senderEmail())
                .orElseThrow(() -> new NotFoundException("Usuário remetente não encontrado para o e-mail " + transactionRequest.senderEmail()));

        ProfileEntity profileDestination = profileEntityRepository.findByProfileEmail(transactionRequest.receiverEmail())
                .orElseThrow(() -> new NotFoundException("Usuário remetente não encontrado para o e-mail " + transactionRequest.receiverEmail()));

        if (profileSender.getBalance().compareTo(transactionRequest.amount()) < 0) {
            throw new InsufficientBalanceException();
        }

        profileSender.setBalance(profileSender.getBalance().subtract(transactionRequest.amount()));
        profileDestination.setBalance(profileDestination.getBalance().add(transactionRequest.amount()));
        TransactionEntity transaction = TransactionEntity.builder()
                .amount(transactionRequest.amount())
                .transactionTimestamp(LocalDateTime.now())
                .profileSender(profileSender)
                .profileReceiver(profileDestination)
                .build();

        profileEntityRepository.saveAndFlush(profileSender);
        profileEntityRepository.saveAndFlush(profileDestination);
        transactionEntityRepository.saveAndFlush(transaction);

        //kafkaEventProducer.publishKafkaTransferEventNotification(TransactionEntity2TransactionResponse.convertToUser(transactionRequest));
        return TransactionEntity2TransactionResponse.convert(transaction);
    }

    @Override
    public TransactionResponse bankStatement(TransactionRequest transactionRequest) {
        return null;
    }
}
