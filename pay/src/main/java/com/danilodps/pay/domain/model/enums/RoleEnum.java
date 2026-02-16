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

    USER(1L, "CPF","USER", "Pessoa física"),
    STORE(2L, "CNPJ", "COMPANY","Pessoa jurídica");

    private final Long id;
    private final String docIdentifier;
    private final String roleGrantedAuthority;
    private final String description;

    public static RoleEnum getByShortName(String documentType){
        if(documentType.equals("CPF")){
            return RoleEnum.USER;
        }
        return RoleEnum.STORE;
    }
}