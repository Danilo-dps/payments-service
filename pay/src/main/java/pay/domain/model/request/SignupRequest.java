package pay.domain.model.request;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record SignupRequest(UUID id, String username, String email, LocalDateTime now){}
