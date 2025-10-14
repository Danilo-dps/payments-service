package com.danilodps.pay.application.exceptions;

import java.io.Serial;

public class EmailEmptyException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public EmailEmptyException() {
        super("Email de usuário não pode ser vazio");
    }
}
