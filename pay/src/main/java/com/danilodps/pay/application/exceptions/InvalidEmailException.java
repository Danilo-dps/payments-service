package com.danilodps.pay.application.exceptions;

import java.io.Serial;

public class InvalidEmailException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidEmailException(String email) {
        super("Email '" + email + "' é inválido");
    }
}
