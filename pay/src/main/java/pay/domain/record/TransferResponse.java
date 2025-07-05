package pay.domain.record;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferResponse(UUID transferId, String fullName, String fromEmail, String destinationEmail, BigDecimal amount, LocalDateTime whenDidItHappen) {
}
