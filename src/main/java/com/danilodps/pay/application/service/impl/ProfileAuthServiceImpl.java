package com.danilodps.pay.application.service.impl;

import com.danilodps.commons.domain.model.response.SignInResponse;
import com.danilodps.commons.domain.model.response.SignUpResponse;
import com.danilodps.commons.domain.validation.ValidatorComponent;
import com.danilodps.pay.adapters.inbound.controller.request.create.SignInRequest;
import com.danilodps.pay.adapters.inbound.controller.request.create.SignUpRequest;
import com.danilodps.pay.adapters.inbound.controller.response.JwtResponse;
import com.danilodps.pay.application.usecases.ProfileAuthUseCase;
import com.danilodps.pay.domain.mappers.RoleEnum2RoleEntity;
import com.danilodps.pay.domain.model.ProfileEntityRepository;
import com.danilodps.pay.domain.model.ProfileRoleEntityRepository;
import com.danilodps.pay.domain.model.entities.ProfileEntity;
import com.danilodps.pay.domain.model.entities.ProfileRoleEntity;
import com.danilodps.pay.domain.model.entities.ProfileRoleId;
import com.danilodps.pay.infrastrucure.config.KafkaEventProducer;
import com.danilodps.pay.infrastrucure.security.jwt.JwtTokenGenerator;
import com.danilodps.pay.infrastrucure.spring.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileAuthServiceImpl implements ProfileAuthUseCase {

    public static final ZoneId SAO_PAULO_ZONE = ZoneId.of("America/Sao_Paulo");
    private final PasswordEncoder passwordEncoder;
    private final ValidatorComponent profileValidator;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final KafkaEventProducer kafkaEventProducer;
    private final AuthenticationManager authenticationManager;
    private final ProfileEntityRepository profileEntityRepository;
    private final ProfileRoleEntityRepository profileRoleEntityRepository;

    @Override
    @Transactional
    public SignUpResponse register(SignUpRequest signUpRequest) {

        profileValidator.validate(signUpRequest.userEmail(), signUpRequest.documentIdentifier(), signUpRequest.document());
        log.info("Registrando novo usuário {}", signUpRequest.username());

        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setProfileId(UUID.randomUUID().toString());
        profileEntity.setUsername(signUpRequest.username());
        profileEntity.setDocumentIdentifier(signUpRequest.documentIdentifier());
        profileEntity.setDocument(signUpRequest.document());
        profileEntity.setProfileEmail(signUpRequest.userEmail());
        profileEntity.setCreatedAt(LocalDateTime.now(SAO_PAULO_ZONE));
        profileEntity.setPassword(passwordEncoder.encode(signUpRequest.password()));
        profileEntity.setBalance(BigDecimal.ZERO);

        profileEntityRepository.save(profileEntity);

        Long roleId = RoleEnum2RoleEntity.convert(signUpRequest.documentIdentifier());
        ProfileRoleId profileRoleId = new ProfileRoleId();
        profileRoleId.setProfileId(profileEntity.getProfileId());
        profileRoleId.setRoleId(roleId);

        ProfileRoleEntity profileRole = new ProfileRoleEntity();
        profileRole.setId(profileRoleId);

        profileRoleEntityRepository.save(profileRole);

        SignUpResponse signUpResponse = new SignUpResponse(
                profileEntity.getProfileId(),
                profileEntity.getUsername(),
                profileEntity.getProfileEmail(),
                profileEntity.getCreatedAt());
        kafkaEventProducer.publishSignUpNotification(signUpResponse);

        return signUpResponse;
    }

    @Override
    public JwtResponse authenticate(SignInRequest loginRequest) {
        log.info("Verificando se o usuário está autenticado");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.userEmail(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenGenerator.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        assert userDetails != null;
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        SignInResponse signInResponse = new SignInResponse(
                userDetails.getProfileId(),
                userDetails.getUsername(),
                userDetails.getProfileEmail(),
                LocalDateTime.now(SAO_PAULO_ZONE));

        kafkaEventProducer.publishSignInNotification(signInResponse);

        return new JwtResponse(jwt,
                userDetails.getProfileId(),
                userDetails.getUsername(),
                userDetails.getProfileEmail(),
                roles);
    }

}