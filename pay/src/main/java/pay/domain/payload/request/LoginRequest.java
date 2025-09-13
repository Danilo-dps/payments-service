package pay.domain.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
	@NotBlank
    private String username;

	@NotBlank
	private String password;
}
