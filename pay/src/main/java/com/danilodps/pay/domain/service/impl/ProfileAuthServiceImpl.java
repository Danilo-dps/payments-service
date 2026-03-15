package com.danilodps.pay.domain.service.impl;

import com.danilodps.commons.domain.model.response.SignInResponse;
import com.danilodps.commons.domain.model.response.SignUpResponse;
import com.danilodps.commons.domain.validation.ValidatorComponent;
import com.danilodps.pay.application.config.KafkaEventProducer;
import com.danilodps.pay.domain.adapter.RoleEnum2RoleEntity;
import com.danilodps.pay.domain.model.ProfileEntity;
import com.danilodps.pay.domain.model.request.create.SignInRequest;
import com.danilodps.pay.domain.model.request.create.SignUpRequest;
import com.danilodps.pay.domain.model.response.JwtResponse;
import com.danilodps.pay.domain.repository.ProfileEntityRepository;
import com.danilodps.pay.domain.security.jwt.JwtTokenGenerator;
import com.danilodps.pay.domain.service.ProfileAuthService;
import com.danilodps.pay.domain.service.spring.UserDetailsImpl;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileAuthServiceImpl implements ProfileAuthService {

    private final ProfileEntityRepository profileEntityRepository;
    private final AuthenticationManager authenticationManager;
    private final KafkaEventProducer kafkaEventProducer;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final ValidatorComponent profileValidator;
    private final PasswordEncoder passwordEncoder;

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
        profileEntity.setRoles(Collections.singletonList(RoleEnum2RoleEntity.convert(signUpRequest.documentIdentifier())));
        profileEntity.setProfileEmail(signUpRequest.userEmail());
        profileEntity.setCreatedAt(LocalDateTime.now());
        profileEntity.setPassword(passwordEncoder.encode(signUpRequest.password()));

        SignUpResponse signUpResponse = SignUpResponse.builder()
                .id(profileEntity.getProfileId())
                .username(profileEntity.getUsername())
                .email(profileEntity.getProfileEmail())
                .signupTimestamp(LocalDateTime.now()).build();

        profileEntityRepository.saveAndFlush(profileEntity);
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

        kafkaEventProducer.publishSignInNotification(SignInResponse.builder().id(userDetails.getProfileId()).username(userDetails.getUsername()).email(userDetails.getProfileEmail()).signinTimestamp(LocalDateTime.now()).build());

        return new JwtResponse(jwt,
                userDetails.getProfileId(),
                userDetails.getUsername(),
                userDetails.getProfileEmail(),
                roles);
    }

}