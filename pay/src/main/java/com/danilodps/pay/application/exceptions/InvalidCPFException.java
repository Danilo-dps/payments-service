package com.danilodps.pay.application.exceptions;

import java.io.Serial;

public class InvalidCPFException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidCPFException(String cpf) {
        super("O CPF '" + cpf + "' é inválido");
    }
}
