package com.danilodps.pay.domain.security.jwt;

import com.danilodps.pay.domain.service.spring.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenGenerator {

  @Value("${app.jwtSecret}")
  private String jwtSecret;

  @Value("${app.jwtExpirationMs}")
  private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {

        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("id", userPrincipal.getId())
                .claim("email", userPrincipal.getEmail())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = this.jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parser()
            .verifyWith((SecretKey) getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser()
              .verifyWith((SecretKey) getSigningKey())
              .build()
              .parseSignedClaims(authToken);
      return true;
    } catch (io.jsonwebtoken.security.SignatureException e) {
      log.error("Assinatura JWT inválida: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      log.error("Token JWT malformado: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("Token JWT expirado: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("Token JWT não suportado: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT claims vazias: {}", e.getMessage());
    }
    return false;
  }
}
