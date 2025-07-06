package pay.domain.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import pay.domain.model.Role;

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
public class UserDTO {

    private UUID userId;

    @NotBlank(message = "O nome completo é obrigatório")
    private String username;

    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;

    @NotBlank(message = "O e-mail é obrigatório")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    private String password;

    private Set<Role> role = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    public UserDTO(String username, String cpf, String email, String password, Set<Role> role, BigDecimal balance) {
        this.username = username;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.role = new HashSet<>();
        this.balance = BigDecimal.ZERO;
    }
}