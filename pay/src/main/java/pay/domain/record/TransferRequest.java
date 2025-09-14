package pay.domain.record;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransferRequest(String fromEmail, String destinationEmail, BigDecimal amount) {
}
