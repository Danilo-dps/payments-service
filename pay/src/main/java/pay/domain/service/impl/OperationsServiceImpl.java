package pay.domain.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pay.application.exceptions.InsufficientBalanceException;
import pay.application.exceptions.InvalidValueException;
import pay.application.exceptions.NotFoundException;
import pay.domain.adapter.DepositHistory2DepositResponse;
import pay.domain.config.KafkaEventProducer;
import pay.domain.dto.DepositRequestDTO;
import pay.domain.model.*;
import pay.domain.model.enums.EOperationType;
import pay.domain.model.request.TransactionRequest;
import pay.domain.model.response.DepositResponse;
import pay.domain.model.response.TransactionResponse;
import pay.domain.model.response.TransferRequest;
import pay.domain.model.response.TransferResponse;
import pay.domain.repository.*;
import pay.domain.service.OperationsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

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
        User userSent = userRepository.findAndLockByEmail(transactionRequest.getUserSender())
                .orElseThrow(() -> new NotFoundException("Usuário remetente não encontrado para o e-mail " + transactionRequest.getUserSender()));

        if (userSent.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
            throw new InsufficientBalanceException();
        }

        userSent.setBalance(userSent.getBalance().subtract(transactionRequest.getAmount()));

        Optional<User> destinationUser = userRepository.findByEmail(transactionRequest.getReceiver());

        if (destinationUser.isPresent()) {
            User userReceived = destinationUser.get();
            userReceived.setBalance(userReceived.getBalance().add(transactionRequest.getAmount()));
            transaction = Transaction.builder().amount(transactionRequest.getAmount()).transactionTimestamp(LocalDateTime.now()).userSender(userSent).userReceiver(userReceived).build();
            transactionRepository.saveAndFlush(transaction);
            userRepository.saveAndFlush(userReceived);
        } else {
            Store destinationStore = storeRepository.findByStoreEmail(transactionRequest.getReceiver()).orElseThrow(() -> new NotFoundException("Conta de destino não encontrada para o e-mail " + transactionRequest.getReceiver()));
            destinationStore.setBalance(destinationStore.getBalance().add(transactionRequest.getAmount()));
            transaction = Transaction.builder().amount(transactionRequest.getAmount()).transactionTimestamp(LocalDateTime.now()).userSender(userSent).storeReceiver(destinationStore).build();
            transactionRepository.saveAndFlush(transaction);
            storeRepository.saveAndFlush(destinationStore);
        }

        userRepository.saveAndFlush(userSent);

        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .amount(transactionRequest.getAmount())
                .transactionTimestamp(LocalDateTime.now())
                .userSender(transactionRequest.getUserSender())
                .receiver(transactionRequest.getReceiver())
                .build();
    }

    @Override
    public TransactionResponse bankStatement(TransactionRequest transactionRequest) {
        return null;
    }
}
