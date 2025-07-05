package pay.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "tb_store",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email", name = "uk_store_email"),
                @UniqueConstraint(columnNames = "cnpj", name = "uk_store_cnpj")
        }
)
public class Store implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID storeId;

    @Column(nullable = false, length = 100)
    private String storeName;

    @Column(nullable = false, unique = true, length = 100)
    private String cnpj;

    @Column(nullable = false, unique = true, length = 50)
    private String storeEmail;

    private BigDecimal balance;

    private List<TransferHistory> transferHistory;

}