package com.danilodps.pay.adapters.outbound.repositories.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface DepositProjection {

    String getDepositId();
    LocalDateTime getDepositAt();
    BigDecimal getAmount();
}
