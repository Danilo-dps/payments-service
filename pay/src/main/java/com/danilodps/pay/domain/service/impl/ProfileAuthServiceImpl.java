package com.danilodps.pay.domain.service.impl;

import com.danilodps.pay.domain.adapter.RoleEnum2RoleEntity;
//import com.danilodps.pay.domain.config.KafkaEventProducer;
import com.danilodps.pay.domain.model.ProfileEntity;
import com.danilodps.pay.domain.model.request.create.SignInRequest;
import com.danilodps.pay.domain.model.request.create.SignUpRequest;
import com.danilodps.pay.domain.model.response.JwtResponse;
import com.danilodps.pay.domain.model.response.SignUpResponse;
import com.danilodps.pay.domain.repository.ProfileEntityRepository;
import com.danilodps.pay.domain.security.jwt.JwtTokenGenerator;
import com.danilodps.pay.domain.service.ProfileAuthService;
import com.danilodps.pay.domain.service.spring.CustomUserDetails;
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
import java.util.List;
import java.util.stream.Collectors;

// TODO fazer o UserValidator userValidator;
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileAuthServiceImpl implements ProfileAuthService {

    private final ProfileEntityRepository profileEntityRepository;
    private final AuthenticationManager authenticationManager;
//    private final KafkaEventProducer kafkaEventProducer;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SignUpResponse register(SignUpRequest signUpRequest) {

        log.info("Registrando novo usuário {}", signUpRequest.username());

        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setUsername(signUpRequest.username());
        profileEntity.setRoles(RoleEnum2RoleEntity.convertList(signUpRequest.roleEnum()));
        profileEntity.setProfileEmail(signUpRequest.email());
        profileEntity.setPassword(passwordEncoder.encode(signUpRequest.password()));

        profileEntityRepository.saveAndFlush(profileEntity);
        return SignUpResponse.builder()
                .id(profileEntity.getProfileId())
                .username(profileEntity.getUsername())
                .email(profileEntity.getProfileEmail())
                .timestamp(LocalDateTime.now()).build();
    }

    @Override
    public JwtResponse authenticate(SignInRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenGenerator.generateJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        assert userDetails != null;
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

}