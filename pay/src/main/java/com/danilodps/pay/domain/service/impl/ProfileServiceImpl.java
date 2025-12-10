package com.danilodps.pay.domain.service.impl;

import com.danilodps.pay.application.exceptions.DuplicateEmailException;
import com.danilodps.pay.application.exceptions.NotFoundException;
import com.danilodps.pay.domain.adapter.ProfileEntity2ProfileResponse;
//import com.danilodps.pay.domain.config.KafkaEventProducer;
import com.danilodps.pay.domain.model.ProfileEntity;
import com.danilodps.pay.domain.model.request.update.ProfileRequestUpdate;
import com.danilodps.pay.domain.model.response.operations.DepositResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final EmailValidator emailValidator;
    private final JwtTokenGenerator jwtTokenGenerator;
//    private final KafkaEventProducer kafkaEventProducer;
    private final AuthenticationManager authenticationManager;
    private final ProfileEntityRepository profileEntityRepository;

    @Override
    @Transactional
    public ProfileResponse getById(UUID profileId) {
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

    // TODO, é preciso atualizar para garantir hash para a nova senha
    @Override
    @Transactional
    public ProfileResponse update(UUID profileId, ProfileRequestUpdate profileRequestUpdate) {
        log.info("Atualizando dados para o ID {}", profileId);
        ProfileEntity existingUser = profileEntityRepository.findById(profileId)
                .orElseThrow(() -> {log.warn("Usuário não encontrado com ID {} ", profileId); return new NotFoundException(profileId);});

        emailValidator.validate(profileRequestUpdate.userEmail());

        if (profileRequestUpdate.userEmail() != null
                && !profileRequestUpdate.userEmail().equals(existingUser.getProfileEmail())
                && profileEntityRepository.findByProfileEmail(profileRequestUpdate.userEmail()).isPresent()) {
                log.warn("Erro. {} email já cadastrado", profileRequestUpdate.userEmail());
                throw new DuplicateEmailException(profileRequestUpdate.userEmail());
        }

        if (profileRequestUpdate.userEmail() != null && !profileRequestUpdate.userEmail().isBlank()) {
            existingUser.setProfileEmail(profileRequestUpdate.userEmail());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(existingUser.getUsername(), profileRequestUpdate.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenGenerator.generateJwtToken(authentication);

        existingUser.setPassword(jwt);

        ProfileEntity profileEntity = profileEntityRepository.saveAndFlush(existingUser);
        log.info("Usuário atualizado");

        return ProfileEntity2ProfileResponse.convert(profileEntity);
    }

    @Override
    @Transactional
    public void delete(UUID profileId) {
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
    public List<DepositResponse> getAllDeposits(UUID profileId){
        ProfileEntity profileEntity = profileEntityRepository.findById(profileId).orElseThrow(() -> {log.error("Usuário não encontrado com ID: {}", profileId); return new NotFoundException(profileId);});
        //List<Deposit> listAllDeposit = profileEntity.getDeposit();
        return null;
    }

}
