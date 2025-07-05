package pay.application.exceptions;

import java.io.Serial;

public class DuplicateEmailException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateEmailException(String email) {
        super("O email '" + email + "' já está cadastrado.");
    }
}
