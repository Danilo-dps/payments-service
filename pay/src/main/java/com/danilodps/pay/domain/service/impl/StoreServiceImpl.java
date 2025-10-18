package com.danilodps.pay.domain.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.danilodps.pay.application.exceptions.DuplicateEmailException;
import com.danilodps.pay.application.exceptions.NotFoundException;
import com.danilodps.pay.domain.adapter.Store2StoreDTO;
import com.danilodps.pay.domain.adapter.Store2StoreResponse;
import com.danilodps.pay.domain.dto.StoreDTO;
import com.danilodps.pay.domain.model.Store;
import com.danilodps.pay.domain.model.response.StoreResponse;
import com.danilodps.pay.domain.repository.StoreRepository;
import com.danilodps.pay.domain.service.StoreService;
import com.danilodps.pay.domain.utils.validations.EmailValidator;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

@Slf4j
@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final EmailValidator emailValidator;

    public StoreServiceImpl(StoreRepository storeRepository, EmailValidator emailValidator){
        this.storeRepository = storeRepository;
        this.emailValidator = emailValidator;
    }

    @Override
    @Transactional
    public StoreResponse getById(UUID storeId) {
        Objects.requireNonNull(storeId, "User ID não pode ser null");
        log.info("Procurando usuário...");
        return storeRepository.findById(storeId)
                .map(Store2StoreResponse::convert)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado com ID: " + storeId);
                    return new NotFoundException(storeId);
                });
    }

    @Override
    @Transactional
    public StoreResponse getByEmail(String storeEmail) {
        Objects.requireNonNull(storeEmail, "Email não pode ser null");
        log.info("Procurando usuário...");
        return storeRepository.findByStoreEmail(storeEmail)
                .map(Store2StoreResponse::convert)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado com Email: " + storeEmail);
                    return new NotFoundException(storeEmail);
                });
       }

    @Override
    @Transactional
    public StoreDTO update(UUID storeId, StoreResponse storeResponse) {
        log.info("Atualizando dados...");
        Store existingStore = storeRepository.findById(storeId).orElseThrow(() -> {log.warn("Usuário não encontrado com ID: " + storeId); return new NotFoundException(storeId);});

        emailValidator.validate(storeResponse.storeEmail());

        if (storeResponse.storeEmail() != null
                && !storeResponse.storeEmail().equals(existingStore.getStoreEmail())
                && storeRepository.findByStoreEmail(storeResponse.storeEmail()).isPresent()) {
                log.warn("Erro. email já cadastrado");
                throw new DuplicateEmailException(storeResponse.storeEmail());
        }

        if (storeResponse.storeName() != null && !storeResponse.storeName().isBlank()) {
            existingStore.setStoreName(storeResponse.storeName());
        }

        if (storeResponse.storeEmail() != null && !storeResponse.storeEmail().isBlank()) {
            existingStore.setStoreEmail(storeResponse.storeEmail());
        }

        log.info("Usuário atualizado");
        Store updatedStore = storeRepository.saveAndFlush(existingStore);
        return Store2StoreDTO.convert(updatedStore);
    }

    @Override
    @Transactional
    public void delete(UUID storeId) {
        log.info("Verificando a existência do usuário para excluir...");
        if (!storeRepository.existsById(storeId)) {
            log.warn("Erro. Usuário não encontrado");
            throw new NotFoundException(storeId);
        }

        log.info("Usuário excluído");
        storeRepository.deleteById(storeId);
    }

}
