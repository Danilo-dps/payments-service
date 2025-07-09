package pay.domain.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pay.application.exceptions.InsufficientBalanceException;
import pay.application.exceptions.InvalidValueException;
import pay.application.exceptions.NotFoundException;
import pay.domain.adapter.DepositHistory2DepositResponse;
import pay.domain.adapter.TransferHistory2TransferResponse;
import pay.domain.dto.DepositRequestDTO;
import pay.domain.dto.TransferRequestDTO;
import pay.domain.model.DepositHistory;
import pay.domain.model.TransferHistory;
import pay.domain.model.User;
import pay.domain.model.enums.EOperationType;
import pay.domain.record.DepositResponse;
import pay.domain.record.TransferResponse;
import pay.domain.repository.UserRepository;
import pay.domain.service.OperationsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
public class OperationsServiceImpl implements OperationsService {
    private static final Logger logger = Logger.getLogger(OperationsServiceImpl.class.getName());

    private final UserRepository userRepository;

    public OperationsServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public DepositResponse deposit(DepositRequestDTO requestDeposit) {

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

        return DepositHistory2DepositResponse.convert(persistedDeposit);
    }

    @Override
    @Transactional
    public TransferResponse transfer(TransferRequestDTO requestTransfer) {
        if (requestTransfer.getAmount() == null || requestTransfer.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidValueException();
        }

        User fromUser = userRepository.findByEmail(requestTransfer.getEmail()).orElseThrow(() -> new NotFoundException(requestTransfer.getEmail()));
        User destinationUser = userRepository.findByEmail(requestTransfer.getDestinationEmail()).orElseThrow(() -> new NotFoundException(requestTransfer.getDestinationEmail()));

        if (fromUser.getBalance() == null || fromUser.getBalance().compareTo(requestTransfer.getAmount()) < 0) {
            throw new InsufficientBalanceException();
        }

        TransferHistory transfer = new TransferHistory(LocalDateTime.now(), destinationUser.getEmail(), EOperationType.TRANSFER, requestTransfer.getAmount(), fromUser);

        fromUser.getTransferHistory().add(transfer);

        fromUser.setBalance(fromUser.getBalance().subtract(requestTransfer.getAmount()));
        destinationUser.setBalance(destinationUser.getBalance().add(requestTransfer.getAmount()));

        userRepository.save(fromUser);
        userRepository.save(destinationUser);

        TransferHistory persistedTransfer = fromUser.getTransferHistory().getLast();

        return TransferHistory2TransferResponse.convert(persistedTransfer);
    }

}
