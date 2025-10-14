package com.danilodps.pay.domain.model.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
  private String accessToken;
  private String id;
  private String username;
  private String email;
  private List<String> roles;
}
