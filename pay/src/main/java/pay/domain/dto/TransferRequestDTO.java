package pay.domain.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {
    private BigDecimal amount;
    private String userEmail;
    private String destinationEmail;
}