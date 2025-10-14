package com.danilodps.pay.domain.model.response;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp,
                            String message,
                            String errorType,
                            int statusCode) {
}
