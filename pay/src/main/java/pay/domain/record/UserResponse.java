package pay.domain.record;

import java.math.BigDecimal;
import java.util.UUID;

public record UserResponse(UUID userId, String username, String email, BigDecimal balance) {
}
