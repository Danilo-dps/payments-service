package com.danilodps.pay.application.exceptions;

import java.io.Serial;

public class InvalidValueException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidValueException() {super("Erro. Valor do depósito deve ser maior que zero.");}
}



