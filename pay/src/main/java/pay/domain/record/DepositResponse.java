package pay.domain.record;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DepositResponse(UUID depositId, String username, BigDecimal amount, LocalDateTime whenDidItHappen) {
}
