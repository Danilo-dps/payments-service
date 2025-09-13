package pay.domain.payload.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
  private String accessToken;
  private UUID id;
  private String username;
  private String email;
  private List<String> roles;
}
