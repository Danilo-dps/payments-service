package pay.domain.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pay.application.exceptions.*;
import pay.domain.adapter.*;
import pay.domain.dto.StoreDTO;
import pay.domain.model.Store;
import pay.domain.model.TransferHistory;
import pay.domain.record.StoreResponse;
import pay.domain.record.TransferResponse;
import pay.domain.repository.StoreRepository;
import pay.domain.service.StoreService;
import pay.domain.utils.validations.EmailValidator;
import pay.domain.utils.validator.StoreValidator;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class StoreServiceImpl implements StoreService {
    private static final Logger logger = Logger.getLogger(StoreServiceImpl.class.getName());

    private final StoreRepository storeRepository;
    private final StoreValidator storeValidator;
    private final EmailValidator emailValidator;

    public StoreServiceImpl(StoreRepository storeRepository, StoreValidator storeValidator, EmailValidator emailValidator){
        this.storeRepository = storeRepository;
        this.storeValidator = storeValidator;
        this.emailValidator = emailValidator;
    }

    @Override
    @Transactional
    public StoreDTO create(StoreDTO storeDTO) {
        logger.info("Criando login...");

        storeValidator.validate(storeDTO);

        if (storeDTO.getStoreName() == null || storeDTO.getStoreName().trim().isEmpty()) {
            logger.warning("Erro. Nome está vazio");
            throw new NameEmptyException();
        }
        if (storeRepository.findByCnpj(storeDTO.getCnpj()).isPresent()) {
            logger.warning("Erro. CPF já cadastrado");
            throw new DuplicateCNPJException(storeDTO.getCnpj());
        }
        if (storeRepository.findByStoreEmail(storeDTO.getStoreEmail()).isPresent()) {
            logger.warning("Erro. Email já cadastrado");
            throw new DuplicateEmailException(storeDTO.getStoreEmail());
        }

        Store store = Store.builder().storeName(storeDTO.getStoreName()).storeEmail(storeDTO.getStoreEmail()).cnpj(storeDTO.getCnpj()).build();
        logger.info("Usuário criado!");
        Store savedStore = storeRepository.save(store);
        return Store2StoreDTO.convert(savedStore);
    }

    @Override
    @Transactional
    public StoreResponse getById(UUID storeId) {
        Objects.requireNonNull(storeId, "User ID não pode ser null");
        logger.info("Procurando usuário...");
        return storeRepository.findById(storeId)
                .map(Store2StoreResponse::convert)
                .orElseThrow(() -> {
                    logger.warning("Usuário não encontrado com ID: " + storeId);
                    return new NotFoundException(storeId);
                });
    }

    @Override
    @Transactional
    public StoreResponse getByEmail(String storeEmail) {
        Objects.requireNonNull(storeEmail, "Email não pode ser null");
        logger.info("Procurando usuário...");
        return storeRepository.findByStoreEmail(storeEmail)
                .map(Store2StoreResponse::convert)
                .orElseThrow(() -> {
                    logger.warning("Usuário não encontrado com Email: " + storeEmail);
                    return new NotFoundException(storeEmail);
                });
       }

    @Override
    @Transactional
    public StoreDTO update(UUID storeId, StoreResponse storeResponse) {
        logger.info("Atualizando dados...");
        Store existingStore = storeRepository.findById(storeId).orElseThrow(() -> {logger.warning("Usuário não encontrado com ID: " + storeId); return new NotFoundException(storeId);});

        emailValidator.validate(storeResponse.storeEmail());

        if (storeResponse.storeEmail() != null
                && !storeResponse.storeEmail().equals(existingStore.getStoreEmail())
                && storeRepository.findByStoreEmail(storeResponse.storeEmail()).isPresent()) {
                logger.warning("Erro. email já cadastrado");
                throw new DuplicateEmailException(storeResponse.storeEmail());
        }

        if (storeResponse.storeName() != null && !storeResponse.storeName().isBlank()) {
            existingStore.setStoreName(storeResponse.storeName());
        }

        if (storeResponse.storeEmail() != null && !storeResponse.storeEmail().isBlank()) {
            existingStore.setStoreEmail(storeResponse.storeEmail());
        }

        logger.info("Usuário atualizado");
        Store updatedStore = storeRepository.save(existingStore);
        return Store2StoreDTO.convert(updatedStore);
    }

    @Override
    @Transactional
    public void delete(UUID storeId) {
        logger.info("Verificando a existência do usuário para excluir...");
        if (!storeRepository.existsById(storeId)) {
            logger.warning("Erro. Usuário não encontrado");
            throw new NotFoundException(storeId);
        }

        logger.info("Usuário excluído");
        storeRepository.deleteById(storeId);
    }

    @Override
    @Transactional
    public List<TransferResponse> getAllTransfers(UUID storeId){
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {logger.warning("Usuário não encontrado com ID: " + storeId); return new NotFoundException(storeId);});
        List<TransferHistory> listAllTransfer = store.getTransferHistory();
        return TransferHistory2TransferResponse.convertToList(listAllTransfer);
    }

}
