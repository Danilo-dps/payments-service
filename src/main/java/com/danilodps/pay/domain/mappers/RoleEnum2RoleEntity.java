package com.danilodps.pay.domain.mappers;

import com.danilodps.pay.domain.model.enums.RoleEnum;

public class RoleEnum2RoleEntity {

    private RoleEnum2RoleEntity(){}

    public static Long convert(String documentIdentifier) {
        return switch (documentIdentifier) {
            case "CPF" -> RoleEnum.USER.getId();
            case "CNPJ" -> RoleEnum.COMPANY.getId();
            default -> throw new IllegalArgumentException("documentIdentifier inválido: " + documentIdentifier);
        };
    }

}
