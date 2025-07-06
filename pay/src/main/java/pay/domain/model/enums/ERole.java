package pay.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum ERole {
  ROLE_USER("user"),
  ROLE_MODERATOR("moderator"),
  ROLE_ADMIN("admin");

  private final String shortName;

  ERole(String shortName) {
    this.shortName = shortName;
  }

  @JsonValue
  public String getShortName() {
    return this.shortName;
  }

  @JsonCreator
  public static ERole fromShortName(String text) {
    if (text == null) {
      throw new IllegalArgumentException("O nome do perfil não pode ser nulo.");
    }

    return Arrays.stream(ERole.values())
            .filter(role -> role.getShortName().equalsIgnoreCase(text))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Nenhum perfil encontrado para o texto: " + text));
  }
}