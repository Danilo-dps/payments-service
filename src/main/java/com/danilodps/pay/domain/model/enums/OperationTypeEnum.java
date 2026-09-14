package com.danilodps.pay.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationTypeEnum {
    DEPOSIT(1L, "deposit"),
    TRANSFER(2L, "transfer");

    private final Long id;
    private final String shortName;

}