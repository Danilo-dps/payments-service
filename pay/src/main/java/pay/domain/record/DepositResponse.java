package pay.domain.record;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DepositResponse(UUID depositId, String fullName, BigDecimal amount, LocalDateTime whenDidItHappen) {
}
