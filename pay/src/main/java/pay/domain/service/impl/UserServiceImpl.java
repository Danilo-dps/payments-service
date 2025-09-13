package pay.domain.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pay.application.exceptions.DuplicateEmailException;
import pay.application.exceptions.NotFoundException;
import pay.domain.adapter.DepositHistory2DepositResponse;
import pay.domain.adapter.TransferHistory2TransferResponse;
import pay.domain.adapter.User2UserDTO;
import pay.domain.adapter.User2UserResponse;
import pay.domain.dto.UserDTO;
import pay.domain.model.DepositHistory;
import pay.domain.model.TransferHistory;
import pay.domain.model.User;
import pay.domain.record.DepositResponse;
import pay.domain.record.TransferResponse;
import pay.domain.record.UserResponse;
import pay.domain.repository.UserRepository;
import pay.domain.service.UserService;
import pay.domain.utils.validations.EmailValidator;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailValidator emailValidator;

    public UserServiceImpl(UserRepository userRepository, EmailValidator emailValidator){
        this.userRepository = userRepository;
        this.emailValidator = emailValidator;
    }

    @Override
    @Transactional
    public UserResponse getById(UUID idUser) {
        Objects.requireNonNull(idUser, "User ID não pode ser null");
        log.info("Procurando usuário para o ID {}", idUser);
        return userRepository.findById(idUser)
                .map(User2UserResponse::convert)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado com ID: {}", idUser);
                    return new NotFoundException(idUser);
                });
    }

    @Override
    @Transactional
    public UserResponse getByEmail(String userEmail) {
        Objects.requireNonNull(userEmail, "Email não pode ser null");
        log.info("Procurando usuário para o email {}", userEmail);
        return userRepository.findByEmail(userEmail)
                .map(User2UserResponse::convert)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado com Email {}", userEmail);
                    return new NotFoundException(userEmail);
                });
       }

    @Override
    @Transactional
    public UserDTO update(UUID userId, UserResponse userResponse) {
        log.info("Atualizando dados para o ID {}", userId);
        User existingUser = userRepository.findById(userId).orElseThrow(() -> {log.warn("Usuário não encontrado com ID {} ", userId); return new NotFoundException(userId);});

        emailValidator.validate(userResponse.email());

        if (userResponse.email() != null
                && !userResponse.email().equals(existingUser.getEmail())
                && userRepository.findByEmail(userResponse.email()).isPresent()) {
                log.warn("Erro. {} email já cadastrado", userResponse.email());
                throw new DuplicateEmailException(userResponse.email());
        }

        if (userResponse.username() != null && !userResponse.username().isBlank()) {
            existingUser.setUsername(userResponse.username());
        }

        if (userResponse.email() != null && !userResponse.email().isBlank()) {
            existingUser.setEmail(userResponse.email());
        }

        log.info("Usuário atualizado");
        User updatedUser = userRepository.save(existingUser);
        return User2UserDTO.convert(updatedUser);
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        log.info("Verificando a existência do usuário de ID {} para excluir", userId);
        if (!userRepository.existsById(userId)) {
            log.error("Erro. Usuário de ID {} não encontrado", userId);
            throw new NotFoundException(userId);
        }

        log.info("Usuário excluído");
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public List<DepositResponse> getAllDeposits(UUID userId){
        User user = userRepository.findById(userId).orElseThrow(() -> {log.error("Usuário não encontrado com ID: {}", userId); return new NotFoundException(userId);});
        List<DepositHistory> listAllDeposit = user.getDepositHistory();
        return DepositHistory2DepositResponse.convertToList(listAllDeposit);
    }

    @Override
    @Transactional
    public List<TransferResponse> getAllTransfers(UUID userId){
        User user = userRepository.findById(userId).orElseThrow(() -> {log.error("Usuário não encontrado com ID: {} ", userId); return new NotFoundException(userId);});
        List<TransferHistory> listAllTransfer = user.getTransferHistory();
        return TransferHistory2TransferResponse.convertToList(listAllTransfer);
    }

}
