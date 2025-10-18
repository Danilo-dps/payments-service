package com.danilodps.pay.domain.dto;

import com.danilodps.pay.domain.model.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StoreDTO {

    private UUID storeId;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Nome é obrigatório")
    private String storeName;

    @NotBlank(message = "CNPJ é obrigatório")
    private String cnpj;

    @NotBlank(message = "Email é obrigatório")
    private String storeEmail;

    @NotBlank(message = "A senha é obrigatória")
    private String password;

    private Set<Role> role = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    public StoreDTO(String storeName, String cnpj, String storeEmail, String password, Set<Role> role, BigDecimal balance) {
        this.storeName = storeName;
        this.cnpj = cnpj;
        this.storeEmail = storeEmail;
        this.password = password;
        this.role = new HashSet<>();
        this.balance = BigDecimal.ZERO;
    }
}