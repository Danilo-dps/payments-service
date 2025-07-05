package pay.application.exceptions;

import java.io.Serial;

public class InvalidCNPJException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidCNPJException(String cpnj) {
        super("O CNPJ '" + cpnj + "' é inválido");
    }
}
