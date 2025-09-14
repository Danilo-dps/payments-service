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
import pay.domain.model.DepositHistory;
import pay.domain.model.Store;
import pay.domain.model.User;
import pay.domain.model.enums.EOperationType;
import pay.domain.record.DepositResponse;
import pay.domain.record.TransferRequest;
import pay.domain.record.TransferResponse;
import pay.domain.repository.StoreRepository;
import pay.domain.repository.UserRepository;
import pay.domain.service.OperationsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class OperationsServiceImpl implements OperationsService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final KafkaEventProducer kafkaEventProducer;

    public OperationsServiceImpl(UserRepository userRepository, StoreRepository storeRepository, KafkaEventProducer kafkaEventProducer){
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.kafkaEventProducer = kafkaEventProducer;
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
    public TransferResponse transfer(TransferRequest transferRequest) {

        if(transferRequest.amount().compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidValueException();
        }

        User userSent;
        Optional<User> user;
        Optional<Store> store;

        userSent = userRepository.findByEmail(transferRequest.fromEmail()).orElseThrow(() -> new NotFoundException("Usuário não encontrado para o id " + transferRequest.fromEmail()));
        user = userRepository.findByEmail(transferRequest.destinationEmail());

        if (userSent.getBalance().compareTo(transferRequest.amount()) <= 0){
            throw new InsufficientBalanceException();
        }

        if (user.isPresent()){
            userSent.setBalance(userSent.getBalance().subtract(transferRequest.amount()));
            user.get().setBalance(user.get().getBalance().add(transferRequest.amount()));
            userRepository.save(userSent);
            userRepository.save(user.get());
            return TransferResponse.builder()
                    .transferId(userSent.getSentTransferHistory().getLast().getSentId())
                    .fullName(userSent.getUsername())
                    .fromEmail(userSent.getEmail())
                    .destinationEmail(user.get().getEmail())
                    .amount(transferRequest.amount())
                    .whenDidItHappen(LocalDateTime.now())
                    .build();
        }

        store = Optional.ofNullable(storeRepository.findByStoreEmail(transferRequest.destinationEmail()).orElseThrow(() -> new NotFoundException("Usuário não encontrado para o id " + transferRequest.destinationEmail())));
        userSent.setBalance(userSent.getBalance().subtract(transferRequest.amount()));
        store.get().setBalance(store.get().getBalance().add(transferRequest.amount()));
        userRepository.save(userSent);
        storeRepository.save(store.get());
        return TransferResponse.builder()
                .transferId(userSent.getSentTransferHistory().getLast().getSentId())
                .fullName(userSent.getUsername())
                .fromEmail(userSent.getEmail())
                .destinationEmail(store.get().getStoreEmail())
                .amount(transferRequest.amount())
                .whenDidItHappen(LocalDateTime.now())
                .build();
    }
}
