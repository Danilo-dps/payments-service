package com.danilodps.pay.application.exceptions;

import java.io.Serial;

public class InsufficientBalanceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InsufficientBalanceException() {
        super("Erro. Saldo disponível é insuficiente");
    }

}
