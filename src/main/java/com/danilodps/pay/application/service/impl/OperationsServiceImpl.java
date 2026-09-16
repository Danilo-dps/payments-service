package com.danilodps.pay.application.service.impl;

import com.danilodps.commons.application.exceptions.InsufficientBalanceException;
import com.danilodps.commons.application.exceptions.InvalidValueException;
import com.danilodps.commons.application.exceptions.NotFoundException;
import com.danilodps.commons.domain.model.response.DepositResponse;
import com.danilodps.commons.domain.model.response.TransactionResponse;
import com.danilodps.pay.adapters.inbound.controller.request.create.operations.DepositRequest;
import com.danilodps.pay.adapters.inbound.controller.request.create.operations.TransactionRequest;
import com.danilodps.pay.adapters.outbound.repositories.projection.DepositProjection;
import com.danilodps.pay.adapters.outbound.repositories.projection.TransactionProjection;
import com.danilodps.pay.application.usecases.OperationsUseCase;
import com.danilodps.pay.domain.mappers.DepositEntity2DepositResponse;
import com.danilodps.pay.domain.mappers.TransactionEntity2TransactionResponse;
import com.danilodps.pay.domain.model.DepositEntityRepository;
import com.danilodps.pay.domain.model.ProfileEntityRepository;
import com.danilodps.pay.domain.model.TransactionEntityRepository;
import com.danilodps.pay.domain.model.entities.DepositEntity;
import com.danilodps.pay.domain.model.entities.ProfileEntity;
import com.danilodps.pay.domain.model.entities.TransactionEntity;
import com.danilodps.pay.infrastrucure.config.KafkaEventProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationsServiceImpl implements OperationsUseCase {

    private static final ZoneId SAO_PAULO_ZONE = ZoneId.of("America/Sao_Paulo");
    private final KafkaEventProducer kafkaEventProducer;
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

        ProfileEntity profileEntity = profileEntityRepository.findByProfileEmail(requestDeposit.userEmail())
                .orElseThrow(() -> new NotFoundException(requestDeposit.userEmail()));

        DepositEntity deposit = new DepositEntity(UUID.randomUUID().toString(), LocalDateTime.now(SAO_PAULO_ZONE), requestDeposit.amount(), profileEntity.getProfileId());

        profileEntity.setBalance(profileEntity.getBalance().add(requestDeposit.amount()));
        depositEntityRepository.save(deposit);
        profileEntityRepository.save(profileEntity);

        kafkaEventProducer.publishDepositEventNotification(DepositEntity2DepositResponse.convert(deposit, profileEntity));
        return DepositEntity2DepositResponse.convert(deposit, profileEntity);
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransactionRequest transactionRequest) {
        if (transactionRequest.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidValueException();
        }

        ProfileEntity profileSender = profileEntityRepository.findAndLockByProfileEmail(transactionRequest.senderEmail())
                .orElseThrow(() -> new NotFoundException("Usuário remetente não encontrado para o e-mail " + transactionRequest.senderEmail()));

        ProfileEntity profileReceiver = profileEntityRepository.findAndLockByProfileEmail(transactionRequest.receiverEmail())
                .orElseThrow(() -> new NotFoundException("Usuário destinatário não encontrado para o e-mail " + transactionRequest.receiverEmail()));

        if (profileSender.getBalance().compareTo(transactionRequest.amount()) < 0) {
            throw new InsufficientBalanceException();
        }

        profileSender.setBalance(profileSender.getBalance().subtract(transactionRequest.amount()));
        profileReceiver.setBalance(profileReceiver.getBalance().add(transactionRequest.amount()));
        TransactionEntity transaction = new TransactionEntity(UUID.randomUUID().toString(), transactionRequest.amount(), LocalDateTime.now(SAO_PAULO_ZONE), profileSender.getProfileId(), profileReceiver.getProfileId());

        profileEntityRepository.save(profileSender);
        profileEntityRepository.save(profileReceiver);

        transactionEntityRepository.save(transaction);

        kafkaEventProducer.publishTransferEventNotification(TransactionEntity2TransactionResponse.convert(transaction, profileSender.getProfileEmail(), profileReceiver.getProfileEmail()));
        return TransactionEntity2TransactionResponse.convert(transaction, profileSender.getProfileEmail(), profileReceiver.getProfileEmail());
    }

    @Override
    public List<DepositProjection> getAllDeposits(String profileId) {
        return depositEntityRepository.findDepositsByProfileId(profileId);
    }

    @Override
    public List<TransactionProjection> getAllTransactions(String profileId) {
        return transactionEntityRepository.findAll(profileId);
    }

}
