package pay.domain.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pay.application.exceptions.InsufficientBalanceException;
import pay.application.exceptions.InvalidValueException;
import pay.application.exceptions.NotFoundException;
import pay.domain.adapter.DepositHistory2DepositResponse;
import pay.domain.adapter.TransferHistory2TransferResponse;
import pay.domain.config.KafkaEventProducer;
import pay.domain.dto.DepositRequestDTO;
import pay.domain.dto.TransferRequestDTO;
import pay.domain.model.DepositHistory;
import pay.domain.model.Store;
import pay.domain.model.TransferHistory;
import pay.domain.model.User;
import pay.domain.model.enums.EOperationType;
import pay.domain.record.DepositResponse;
import pay.domain.record.TransferResponse;
import pay.domain.repository.StoreRepository;
import pay.domain.repository.UserRepository;
import pay.domain.service.OperationsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.logging.Logger;

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
    public TransferResponse transfer(TransferRequestDTO requestTransfer) {
        log.info("Inicializando processo de transferência");
        if (requestTransfer.getAmount() == null || requestTransfer.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidValueException();
        }

        User fromUser = userRepository.findByEmail(requestTransfer.getEmail()).orElseThrow(() -> new NotFoundException(requestTransfer.getEmail()));

        // Novo método para buscar o usuário de destino em ambos os repositórios
        Object destination = findDestination(requestTransfer.getDestinationEmail());

        if (fromUser.getBalance() == null || fromUser.getBalance().compareTo(requestTransfer.getAmount()) < 0) {
            throw new InsufficientBalanceException();
        }

        TransferHistory transfer = new TransferHistory(LocalDateTime.now(), requestTransfer.getDestinationEmail(), EOperationType.TRANSFER, requestTransfer.getAmount(), fromUser);
        fromUser.getTransferHistory().add(transfer);

        fromUser.setBalance(fromUser.getBalance().subtract(requestTransfer.getAmount()));

        // Lógica para adicionar o valor ao saldo do destino, seja ele User ou Store
        if (destination instanceof User) {
            User destinationUser = (User) destination;
            destinationUser.setBalance(destinationUser.getBalance().add(requestTransfer.getAmount()));
            userRepository.save(destinationUser);
        } else if (destination instanceof Store) {
            Store destinationStore = (Store) destination;
            destinationStore.setBalance(destinationStore.getBalance().add(requestTransfer.getAmount()));
            storeRepository.save(destinationStore);
        }

        userRepository.save(fromUser);

        TransferHistory persistedTransfer = fromUser.getTransferHistory().getLast();
        kafkaEventProducer.publishKafkaTransferEventNotification(TransferHistory2TransferResponse.convert(persistedTransfer));
        return TransferHistory2TransferResponse.convert(persistedTransfer);
    }

    private Object findDestination(String email) {
        // Tenta encontrar o usuário no repositório de usuários
        return userRepository.findByEmail(email)
                .map(u -> (Object) u) // Converte para Object para ter um tipo de retorno único
                .orElseGet(() ->
                        // Se não encontrar, tenta encontrar no repositório de lojas
                        storeRepository.findByStoreEmail(email)
                                .orElseThrow(() -> new NotFoundException(email)) // Lança exceção se não encontrar em nenhum
                );
    }
}
