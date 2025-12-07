package com.danilodps.pay.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.management.relation.Role;
import java.util.Arrays;
import java.util.Optional;

import static java.util.Arrays.stream;

@Getter
@RequiredArgsConstructor
public enum RoleEnum {

    USER(1L, "CPF", "Pessoa física"),
    STORE(2L, "CNPJ", "Pessoa jurídica");

    private final Long id;
    private final String shortName;
    private final String description;

}