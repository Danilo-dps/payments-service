package pay.domain.record;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record DepositResponse(UUID depositId, String username, String userEmail, BigDecimal amount, LocalDateTime whenDidItHappen) {
}
