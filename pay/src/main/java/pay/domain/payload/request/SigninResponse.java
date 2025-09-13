package pay.domain.payload.request;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record SigninResponse(UUID id, String username, String email, LocalDateTime now){}
