package pay.domain.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class StoreDTO {

    private UUID storeId;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Nome é obrigatório")
    private String storeName;

    @NotBlank(message = "CNPJ é obrigatório")
    private String cnpj;

    @NotBlank(message = "Email é obrigatório")
    private String storeEmail;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    public StoreDTO(UUID storeId, String storeName, String cnpj, String storeEmail, BigDecimal balance) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.cnpj = cnpj;
        this.storeEmail = storeEmail;
        this.balance = BigDecimal.ZERO;
    }
}