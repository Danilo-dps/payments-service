package pay.domain.record;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp,
                            String message,
                            String errorType,
                            int statusCode) {
}
