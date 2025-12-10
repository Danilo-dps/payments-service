package com.danilodps.pay.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentTypeEnum {

    CPF(1L, "CPF", "Pessoa física"),
    CNPJ(2L, "CNPJ", "Pessoa jurídica");

    private final Long id;
    private final String shortName;
    private final String description;

}