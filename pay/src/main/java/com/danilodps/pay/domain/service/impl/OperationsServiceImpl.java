package com.danilodps.pay.domain.service.impl;

import com.danilodps.pay.domain.adapter.Transaction2TransactionResponse;
import com.danilodps.pay.domain.model.DepositHistory;
import com.danilodps.pay.domain.model.Store;
import com.danilodps.pay.domain.model.Transaction;
import com.danilodps.pay.domain.model.User;
import com.danilodps.pay.domain.repository.StoreRepository;
import com.danilodps.pay.domain.repository.TransactionRepository;
import com.danilodps.pay.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.danilodps.pay.application.exceptions.InsufficientBalanceException;
import com.danilodps.pay.application.exceptions.InvalidValueException;
import com.danilodps.pay.application.exceptions.NotFoundException;
import com.danilodps.pay.domain.adapter.DepositHistory2DepositResponse;
import com.danilodps.pay.domain.config.KafkaEventProducer;
import com.danilodps.pay.domain.dto.DepositRequestDTO;
import com.danilodps.pay.domain.model.enums.EOperationType;
import com.danilodps.pay.domain.model.request.TransactionRequest;
import com.danilodps.pay.domain.model.response.DepositResponse;
import com.danilodps.pay.domain.model.response.TransactionResponse;
import com.danilodps.pay.domain.repository.*;
import com.danilodps.pay.domain.service.OperationsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class OperationsServiceImpl implements OperationsService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final TransactionRepository transactionRepository;
    private final KafkaEventProducer kafkaEventProducer;

    public OperationsServiceImpl(
            UserRepository userRepository,
            StoreRepository storeRepository,
            TransactionRepository transactionRepository,
            KafkaEventProducer kafkaEventProducer) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.kafkaEventProducer = kafkaEventProducer;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public DepositResponse deposit(DepositRequestDTO requestDeposit) {
        log.info("Inicializando processo de depósito");
        if (requestDeposit.getAmount() == null || requestDeposit.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidValueException();
        }

        User user = userRepository.findByEmail(requestDeposit.getEmail())
                .orElseThrow(() -> new NotFoundException(requestDeposit.getEmail()));

        DepositHistory deposit = new DepositHistory(LocalDateTime.now(), EOperationType.DEPOSIT, requestDeposit.getAmount(), user);

        user.getDepositHistory().add(deposit);
        user.setBalance(user.getBalance().add(requestDeposit.getAmount()));
        User savedUser = userRepository.saveAndFlush(user);
        DepositHistory persistedDeposit = savedUser.getDepositHistory().getLast();

        kafkaEventProducer.publishKafkaDepositEventNotification(DepositHistory2DepositResponse.convert(persistedDeposit));
        return DepositHistory2DepositResponse.convert(persistedDeposit);
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransactionRequest transactionRequest) {
        if (transactionRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidValueException();
        }

        Transaction transaction = null;
        User userSent = userRepository.findAndLockByEmail(transactionRequest.getUserSenderEmail())
                .orElseThrow(() -> new NotFoundException("Usuário remetente não encontrado para o e-mail " + transactionRequest.getUserSenderEmail()));

        if (userSent.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
            throw new InsufficientBalanceException();
        }

        userSent.setBalance(userSent.getBalance().subtract(transactionRequest.getAmount()));

        Optional<User> destinationUser = userRepository.findByEmail(transactionRequest.getReceiverEmail());

        if (destinationUser.isPresent()) {
            User userReceived = destinationUser.get();
            userReceived.setBalance(userReceived.getBalance().add(transactionRequest.getAmount()));
            transaction = Transaction.builder().amount(transactionRequest.getAmount()).transactionTimestamp(LocalDateTime.now()).userSender(userSent).userReceiver(userReceived).build();
            transactionRequest.setReceiverName(transaction.getUserSender().getUsername());
            transactionRepository.saveAndFlush(transaction);
            userRepository.saveAndFlush(userReceived);
        } else {
            Store destinationStore = storeRepository.findByStoreEmail(transactionRequest.getReceiverEmail())
                    .orElseThrow(() -> new NotFoundException("Conta de destino não encontrada para o e-mail " + transactionRequest.getReceiverEmail()));
            destinationStore.setBalance(destinationStore.getBalance().add(transactionRequest.getAmount()));
            transaction = Transaction.builder().amount(transactionRequest.getAmount()).transactionTimestamp(LocalDateTime.now()).userSender(userSent).storeReceiver(destinationStore).build();
            transactionRequest.setReceiverName(transaction.getStoreReceiver().getStoreName());
            transactionRepository.saveAndFlush(transaction);
            storeRepository.saveAndFlush(destinationStore);
        }

        userRepository.saveAndFlush(userSent);
        transactionRequest.setUserSenderName(transaction.getUserSender().getUsername());
        transactionRequest.setTransactionId(transaction.getTransactionId());
        transactionRequest.setTransactionTimestamp(transaction.getTransactionTimestamp());

        kafkaEventProducer.publishKafkaTransferEventNotification(Transaction2TransactionResponse.convertToUser(transactionRequest));
        return Transaction2TransactionResponse.convertToUser(transactionRequest);
    }

    @Override
    public TransactionResponse bankStatement(TransactionRequest transactionRequest) {
        return null;
    }
}
