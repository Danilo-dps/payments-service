package com.danilodps.pay.application.exceptions;

import java.io.Serial;

public class UserCPFEmptyException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserCPFEmptyException() {
        super("CPF não pode ser vazio");
    }
}
