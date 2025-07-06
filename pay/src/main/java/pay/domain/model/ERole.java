package pay.domain.model;

public enum ERole {
  ROLE_USER("user"),
  ROLE_MODERATOR("moderator"),
  ROLE_ADMIN("admin");

  private final String shortName;

  ERole(String shortName) {
    this.shortName = shortName;
  }

  public String getShortName() {
    return this.shortName;
  }

  public static ERole fromShortName(String text) {
    if (text == null) {
      throw new IllegalArgumentException("O nome do perfil não pode ser nulo.");
    }

    for (ERole role : ERole.values()) {
      if (role.shortName.equalsIgnoreCase(text)) {
        return role;
      }
    }

    throw new IllegalArgumentException("Nenhum perfil encontrado para o texto: " + text);
  }
}