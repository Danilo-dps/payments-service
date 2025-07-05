package pay.domain.record;

import java.math.BigDecimal;
import java.util.UUID;

public record StoreResponse(UUID storeId, String storeName, String storeEmail, BigDecimal balance) {
}
