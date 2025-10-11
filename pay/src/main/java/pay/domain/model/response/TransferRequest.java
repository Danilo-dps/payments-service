package pay.domain.model.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransferRequest(String fromEmail, String destinationEmail, BigDecimal amount) {
}
