package com.danilodps.pay.domain.utils;

import com.danilodps.pay.domain.model.RoleEntity;
import com.danilodps.pay.domain.model.enums.RoleEnum;
import com.danilodps.pay.domain.repository.RoleEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    // apenas para testar, saindo do H2,  terá uma tabela com roles
    private final RoleEntityRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("=== INICIALIZANDO ROLES ===");

        for (RoleEnum roleEnum : RoleEnum.values()) {
                RoleEntity role = RoleEntity.builder()
                        .roleId(roleEnum.getId())
                        .shortName(roleEnum.getShortName())
                        .description(roleEnum.getDescription())
                        .build();
                roleRepository.save(role);
                log.info("Role criada: {}", roleEnum);
        }
    }

}
