package com.danilodps.pay.adapters.outbound.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface DepositProjection {

    String getDepositId();
    LocalDateTime getDepositAt();
    BigDecimal getAmount();
}
