package pay.application.exceptions;

import java.io.Serial;

public class StoreCNPJEmptyException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public StoreCNPJEmptyException() {
        super("CNPJ não pode ser vazio");
    }
}
