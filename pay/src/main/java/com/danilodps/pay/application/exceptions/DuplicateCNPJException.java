package com.danilodps.pay.application.exceptions;

import java.io.Serial;

public class DuplicateCNPJException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateCNPJException(String cpnj) {
        super("O CNPJ '" + cpnj + "' já está cadastrado.");
    }
}
