package pay.domain.model.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReceivedTransferResponse(UUID receivedId, LocalDateTime whenDidItHappen, String fromEmail, BigDecimal amount){
}
