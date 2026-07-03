package com.danilodps.pay.adapters.outbound.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionProjection {

    String getTransactionId();
    String getProfileReceiver();
    LocalDateTime getTransactionAt();
    BigDecimal getAmount();
}
