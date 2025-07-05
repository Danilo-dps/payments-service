package pay.application.exceptions;

import java.io.Serial;
import java.util.UUID;

public class NotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NotFoundException(UUID userId) {
        super("Usuário com ID " + userId + " não encontrado.");
    }

    public NotFoundException(String email) {
        super("Usuário com email " + email + " não encontrado.");
    }
}
