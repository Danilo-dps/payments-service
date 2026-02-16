package com.danilodps.pay.domain.service.impl;

import com.danilodps.pay.application.exceptions.InsufficientBalanceException;
import com.danilodps.pay.application.exceptions.InvalidValueException;
import com.danilodps.pay.application.exceptions.NotFoundException;
import com.danilodps.pay.domain.adapter.DepositEntity2DepositResponse;
import com.danilodps.pay.domain.adapter.TransactionEntity2TransactionResponse;
import com.danilodps.pay.domain.model.DepositEntity;
import com.danilodps.pay.domain.model.ProfileEntity;
import com.danilodps.pay.domain.model.TransactionEntity;
import com.danilodps.pay.domain.model.request.create.operations.DepositRequest;
import com.danilodps.pay.domain.model.request.create.operations.TransactionRequest;
import com.danilodps.pay.domain.model.response.operations.DepositResponse;
import com.danilodps.pay.domain.model.response.operations.TransactionResponse;
import com.danilodps.pay.domain.repository.DepositEntityRepository;
import com.danilodps.pay.domain.repository.ProfileEntityRepository;
import com.danilodps.pay.domain.repository.TransactionEntityRepository;
import com.danilodps.pay.domain.service.OperationsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

//TODO: garantir que usuário A não acesse os dados do usuário B
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

        String currentUserEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        if (!requestDeposit.email().equals(currentUserEmail)) {
            throw new AccessDeniedException("Você não tem permissão para acessar este recurso");
        }

        ProfileEntity profileEntity = profileEntityRepository.findByProfileEmail(requestDeposit.email())
                .orElseThrow(() -> new NotFoundException(requestDeposit.email()));

        DepositEntity deposit = DepositEntity.builder()
                .depositId(UUID.randomUUID().toString())
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

        String currentUserEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        if (!transactionRequest.senderEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("Você não tem permissão para acessar este recurso");
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
                .transactionId(UUID.randomUUID().toString())
                .amount(transactionRequest.amount())
                .transactionTimestamp(LocalDateTime.now())
                .profileSender(profileSender)
                .profileReceiver(profileDestination)
                .build();

        profileEntityRepository.saveAndFlush(profileSender);
        profileEntityRepository.saveAndFlush(profileDestination);

        //aqui, seria melhor um saveAndFlush ou uma query especifica para os campos atualizados?
        transactionEntityRepository.saveAndFlush(transaction);

        //kafkaEventProducer.publishKafkaTransferEventNotification(TransactionEntity2TransactionResponse.convertToUser(transactionRequest));
        return TransactionEntity2TransactionResponse.convert(transaction);
    }

    @Override
    public TransactionResponse bankStatement(TransactionRequest transactionRequest) {
        return null;
    }
}
