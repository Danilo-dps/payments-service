package pay.domain.record;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record SignupResponse(UUID id, String username, String email, LocalDateTime now){}
