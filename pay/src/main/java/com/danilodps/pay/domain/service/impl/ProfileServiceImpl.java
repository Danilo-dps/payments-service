package com.danilodps.pay.domain.service.impl;

import com.danilodps.commons.application.exceptions.DuplicateEmailException;
import com.danilodps.commons.application.exceptions.NotFoundException;
import com.danilodps.commons.domain.model.response.DepositResponse;
import com.danilodps.pay.domain.adapter.ProfileEntity2ProfileResponse;
import com.danilodps.pay.domain.model.ProfileEntity;
import com.danilodps.pay.domain.model.request.update.ProfileRequestUpdate;
import com.danilodps.pay.domain.model.response.ProfileResponse;
import com.danilodps.pay.domain.repository.ProfileEntityRepository;
import com.danilodps.pay.domain.security.jwt.JwtTokenGenerator;
import com.danilodps.pay.domain.service.ProfileService;
import com.danilodps.pay.domain.utils.validations.EmailValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
//TODO: garantir que usuário A não acesse os dados do usuário B
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final EmailValidator emailValidator;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final PasswordEncoder passwordEncoder;
//    private final KafkaEventProducer kafkaEventProducer;
    private final AuthenticationManager authenticationManager;
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
        log.info("Procurando usuário para o email {}", profileEmail);
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

        ProfileEntity existingUser = profileEntityRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException(profileId));

        if (profileRequestUpdate.newEmail() != null && !profileRequestUpdate.newEmail().isBlank()) {
            if (!profileRequestUpdate.newEmail().equals(existingUser.getProfileEmail())) {
                emailValidator.validate(profileRequestUpdate.newEmail());
                if (profileEntityRepository.findByProfileEmail(profileRequestUpdate.newEmail()).isPresent()) {
                    throw new DuplicateEmailException(profileRequestUpdate.newEmail());
                }
                existingUser.setProfileEmail(profileRequestUpdate.newEmail());
            }
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(existingUser.getUsername(), profileRequestUpdate.currentPassword()));

        if (profileRequestUpdate.newPassword() != null && !profileRequestUpdate.newPassword().isBlank()) {
            log.info("Alterando senha do usuário {}", profileId);
            existingUser.setPassword(passwordEncoder.encode(profileRequestUpdate.newPassword()));
        }

        ProfileEntity profileEntity = profileEntityRepository.saveAndFlush(existingUser);
        log.info("Perfil atualizado com sucesso");

        return ProfileEntity2ProfileResponse.convert(profileEntity);
    }

    @Override
    @Transactional
    public void delete(String profileId) {
        log.info("Verificando a existência do usuário de ID {} para excluir", profileId);
        if (!profileEntityRepository.existsById(profileId)) {
            log.error("Erro. Usuário de ID {} não encontrado", profileId);
            throw new NotFoundException(profileId);
        }

        log.info("Usuário excluído");
        profileEntityRepository.deleteById(profileId);
    }

    @Override
    @Transactional
    public List<DepositResponse> getAllDeposits(String profileId){
        ProfileEntity profileEntity = profileEntityRepository.findById(profileId).orElseThrow(() -> {log.error("Usuário não encontrado com ID: {}", profileId); return new NotFoundException(profileId);});
        //List<Deposit> listAllDeposit = profileEntity.getDeposit();
        return null;
    }

}
