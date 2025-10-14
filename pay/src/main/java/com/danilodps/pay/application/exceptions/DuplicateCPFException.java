package com.danilodps.pay.application.exceptions;

import java.io.Serial;

public class DuplicateCPFException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateCPFException(String CPF) {
        super("O CPF '" + CPF + "' já está cadastrado.");
    }
}
