package com.danilodps.pay.domain.service.impl;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.danilodps.pay.domain.config.KafkaEventProducer;
import com.danilodps.pay.domain.dto.StoreDTO;
import com.danilodps.pay.domain.model.Role;
import com.danilodps.pay.domain.model.Store;
import com.danilodps.pay.domain.model.enums.ERole;
import com.danilodps.pay.domain.model.request.SignupRequest;
import com.danilodps.pay.domain.repository.RoleRepository;
import com.danilodps.pay.domain.repository.StoreRepository;
import com.danilodps.pay.domain.security.jwt.JwtUtils;
import com.danilodps.pay.domain.service.AbstractAuthService;
import com.danilodps.pay.domain.utils.validator.StoreValidator;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service("storeAuthService")
public class StoreAuthServiceImpl extends AbstractAuthService<SignupRequest, StoreDTO> {

    private final StoreRepository storeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final StoreValidator storeValidator;

    public StoreAuthServiceImpl(AuthenticationManager authenticationManager, KafkaEventProducer kafkaEventProducer, JwtUtils jwtUtils,
                                StoreRepository storeRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, StoreValidator storeValidator) {
        super(authenticationManager, kafkaEventProducer, jwtUtils);
        this.storeRepository = storeRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.storeValidator = storeValidator;
    }

    @Override
    @Transactional
    public SignupRequest register(StoreDTO signUpRequest) {

        storeValidator.validate(signUpRequest);
        Store store = new Store();
        store.setStoreName(signUpRequest.getStoreName());
        store.setCnpj(signUpRequest.getCnpj());
        store.setStoreEmail(signUpRequest.getStoreEmail());
        store.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        Set<Role> rolesFromDto = signUpRequest.getRole();
        Set<Role> rolesParaSalvar;

        if (rolesFromDto == null || rolesFromDto.isEmpty()) {
            rolesParaSalvar = Set.of(roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Erro: Perfil padrão ROLE_USER não encontrado.")));
        } else {
            rolesParaSalvar = rolesFromDto.stream()
                    .map(role -> {
                        ERole roleEnum = role.getName();
                        return roleRepository.findByName(roleEnum)
                                .orElseThrow(() -> new RuntimeException("Erro: Perfil " + roleEnum.name() + " não configurado no banco."));
                    })
                    .collect(Collectors.toSet());
        }

        store.setRole(rolesParaSalvar);
        storeRepository.saveAndFlush(store);
        kafkaEventProducer.publishKafkaSignUpNotification(SignupRequest.builder().id(store.getStoreId()).username(store.getStoreName()).email(store.getStoreEmail()).now(LocalDateTime.now()).build());
        return SignupRequest.builder().id(store.getStoreId()).username(store.getStoreName()).email(store.getStoreEmail()).now(LocalDateTime.now()).build();
    }

}