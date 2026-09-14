package com.danilodps.pay.application.service.impl;

import com.danilodps.commons.application.exceptions.DuplicateEmailException;
import com.danilodps.commons.application.exceptions.NotFoundException;
import com.danilodps.commons.domain.validation.EmailValidator;
import com.danilodps.pay.adapters.inbound.controller.request.update.ProfileRequestUpdate;
import com.danilodps.pay.adapters.inbound.controller.response.ProfileResponse;
import com.danilodps.pay.application.usecases.ProfileUseCase;
import com.danilodps.pay.domain.mappers.ProfileEntity2ProfileResponse;
import com.danilodps.pay.domain.model.ProfileEntity;
import com.danilodps.pay.domain.model.ProfileEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileUseCase {

    private final EmailValidator emailValidator;
    private final PasswordEncoder passwordEncoder;
    private final ProfileEntityRepository profileEntityRepository;

    @Override
    @Transactional
    public ProfileResponse getById(String profileId) {
        Objects.requireNonNull(profileId, "User ID não pode ser null");
        log.info("Procurando usuário para o ID {}", profileId);
        return profileEntityRepository.findById(profileId)
                .map(ProfileEntity2ProfileResponse::convert)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado com ID: {}", profileId);
                    return new NotFoundException(profileId);
                });
    }

    @Override
    @Transactional
    public ProfileResponse getByEmail(String profileEmail) {
        Objects.requireNonNull(profileEmail, "Email não pode ser null");
        log.info("Procurando usuário para o userEmail {}", profileEmail);
        return profileEntityRepository.findByProfileEmail(profileEmail)
                .map(ProfileEntity2ProfileResponse::convert)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado com Email {}", profileEmail);
                    return new NotFoundException(profileEmail);
                });
    }

    @Override
    @Transactional
    public ProfileResponse update(String profileId, ProfileRequestUpdate profileRequestUpdate) {
        log.info("Iniciando atualização de perfil para o ID {}", profileId);

        ProfileEntity existingUser = profileEntityRepository.findById(profileId).orElseThrow(() -> new NotFoundException(profileId));

        if (profileRequestUpdate.newEmail() != null && !profileRequestUpdate.newEmail().isBlank() && !profileRequestUpdate.newEmail().equals(existingUser.getProfileEmail())) {
                emailValidator.validate(profileRequestUpdate.newEmail());
                if (profileEntityRepository.findByProfileEmail(profileRequestUpdate.newEmail()).isPresent()) {
                    throw new DuplicateEmailException(profileRequestUpdate.newEmail());
                }
                existingUser.setProfileEmail(profileRequestUpdate.newEmail());
                existingUser.setLastUpdated(LocalDateTime.now(ZoneId.systemDefault()));
            }


        if (profileRequestUpdate.newPassword() != null && !profileRequestUpdate.newPassword().isBlank()) {
            log.info("Alterando senha do usuário {}", profileId);
            existingUser.setPassword(passwordEncoder.encode(profileRequestUpdate.newPassword()));
            existingUser.setLastUpdated(LocalDateTime.now(ZoneId.systemDefault()));
        }

        ProfileEntity profileEntity = profileEntityRepository.save(existingUser);
        log.info("Perfil atualizado com sucesso");

        return ProfileEntity2ProfileResponse.convert(profileEntity);
    }

    @Override
    @Transactional
    public void delete(String profileId) {
        log.info("Verificando a existência do usuário de ID {} para excluir", profileId);
        Optional<ProfileEntity> existingUser = profileEntityRepository.findById(profileId);
        existingUser.ifPresent(profileEntityRepository::delete);
    }

}
