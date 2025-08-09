package pay.domain.service.impl;

import pay.application.exceptions.*;
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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pay.domain.utils.validations.EmailValidator;
import pay.domain.utils.validator.UserValidator;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

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
        logger.info("Procurando usuário...");
        return userRepository.findById(idUser)
                .map(User2UserResponse::convert)
                .orElseThrow(() -> {
                    logger.warning("Usuário não encontrado com ID: " + idUser);
                    return new NotFoundException(idUser);
                });
    }

    @Override
    @Transactional
    public UserResponse getByEmail(String userEmail) {
        Objects.requireNonNull(userEmail, "Email não pode ser null");
        logger.info("Procurando usuário...");
        return userRepository.findByEmail(userEmail)
                .map(User2UserResponse::convert)
                .orElseThrow(() -> {
                    logger.warning("Usuário não encontrado com Email: " + userEmail);
                    return new NotFoundException(userEmail);
                });
       }

    @Override
    @Transactional
    public UserDTO update(UUID userId, UserResponse userResponse) {
        logger.info("Atualizando dados...");
        User existingUser = userRepository.findById(userId).orElseThrow(() -> {logger.warning("Usuário não encontrado com ID: " + userId); return new NotFoundException(userId);});

        emailValidator.validate(userResponse.email());

        if (userResponse.email() != null
                && !userResponse.email().equals(existingUser.getEmail())
                && userRepository.findByEmail(userResponse.email()).isPresent()) {
                logger.warning("Erro. email já cadastrado");
                throw new DuplicateEmailException(userResponse.email());
        }

        if (userResponse.username() != null && !userResponse.username().isBlank()) {
            existingUser.setUsername(userResponse.username());
        }

        if (userResponse.email() != null && !userResponse.email().isBlank()) {
            existingUser.setEmail(userResponse.email());
        }

        logger.info("Usuário atualizado");
        User updatedUser = userRepository.save(existingUser);
        return User2UserDTO.convert(updatedUser);
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        logger.info("Verificando a existência do usuário para excluir...");
        if (!userRepository.existsById(userId)) {
            logger.warning("Erro. Usuário não encontrado");
            throw new NotFoundException(userId);
        }

        logger.info("Usuário excluído");
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public List<DepositResponse> getAllDeposits(UUID userId){
        User user = userRepository.findById(userId).orElseThrow(() -> {logger.warning("Usuário não encontrado com ID: " + userId); return new NotFoundException(userId);});
        List<DepositHistory> listAllDeposit = user.getDepositHistory();
        return DepositHistory2DepositResponse.convertToList(listAllDeposit);
    }

    @Override
    @Transactional
    public List<TransferResponse> getAllTransfers(UUID userId){
        User user = userRepository.findById(userId).orElseThrow(() -> {logger.warning("Usuário não encontrado com ID: " + userId); return new NotFoundException(userId);});
        List<TransferHistory> listAllTransfer = user.getTransferHistory();
        return TransferHistory2TransferResponse.convertToList(listAllTransfer);
    }

}
