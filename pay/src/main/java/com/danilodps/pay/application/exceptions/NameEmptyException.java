package com.danilodps.pay.application.exceptions;

import java.io.Serial;

public class NameEmptyException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NameEmptyException() {
        super("Nome de usuário não pode ser vazio");
    }
}
